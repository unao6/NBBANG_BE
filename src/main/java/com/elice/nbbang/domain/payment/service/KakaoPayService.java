package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.payment.config.KakaoPayProperties;
import com.elice.nbbang.domain.payment.dto.KakaoPayCancelRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPayCancelResponse;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionApproveRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionApproveResponse;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionCreateRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionCreateResponse;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionResponse;
import com.elice.nbbang.domain.payment.dto.PaymentRefundDTO;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.repository.CardRepository;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.config.EncryptUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class KakaoPayService {

    private final KakaoPayProperties kakaoPayProperties;
    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OttRepository ottRepository;
    private final EncryptUtils encryptUtils;
    private final PaymentService paymentService;

    /**
     * 1.결제준비 (카드 등록을 위한)
     * userId를 파라미터로 전달받아 데이터 세팅 후 카카오페이에 요청
     * tid, next_redirect_pc_url 확보
     */
    public KakaoPaySubscriptionCreateResponse createSubscription(Long userId) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {

            // 카카오페이 결제준비 요청 URL 세팅
            HttpPost httpPost = new HttpPost(kakaoPayProperties.getReadyCreateUrl());

            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new EntityNotFoundException("유저 ID를 찾을 수 없습니다.");
            }
            User user = userOptional.get();

            // partner_user_id는 유저 이름을 이용
            // partner_order_id는 랜덤한 8자리 문자열
            String partnerOrderId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String partnerUserId = user.getNickname();

            //요청 객체 생성
            KakaoPaySubscriptionCreateRequest request = KakaoPaySubscriptionCreateRequest.fromProperties(kakaoPayProperties, partnerOrderId, partnerUserId);

            //헤더 세팅
            setHeaders(httpPost, kakaoPayProperties.getSecretKey());

            //바디 세팅
            String json = objectMapper.writeValueAsString(request);

            log.info("1. 결제 승인 준비 요청값 json: {}", json);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            //결제 승인 준비 요청 반환값 받아서 처리
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("1-1. 결제 승인 준비 응답값 json: {}", responseString);

                KakaoPaySubscriptionCreateResponse kakaoResponse = objectMapper.readValue(responseString, KakaoPaySubscriptionCreateResponse.class);
                //todo: 이거 최종 카드 저장 안되면 해당 카드 정보는 삭제되어야 함.
                Payment payment = new Payment(
                    user,
                    partnerUserId,
                    partnerOrderId,
                    PaymentType.KAKAOPAY,
                    //카드등록 testAmount=0원
                    kakaoPayProperties.getTestAmount(),
                    PaymentStatus.CREATED,
                    kakaoResponse.getCreatedAt(),
                    kakaoPayProperties.getSubscriptionCid(),
                    kakaoResponse.getTid()
                );

                paymentRepository.save(payment);
                log.info("1-3. 결제 승인 준비 정상 등록");

                //결제 QR코드 URL 반환
                return KakaoPaySubscriptionCreateResponse.builder()
                    .tid(kakaoResponse.getTid())
                    .nextRedirectPcUrl(kakaoResponse.getNextRedirectPcUrl())
                    .build();
            }
        }
    }

    /**
     * 2.결제승인 (카드 등록을 위한)
     * tid와 pg_token을 파라미터로 전달받아 데이터 세팅 후 카카오페이에 요청
     * sid 확보, 상태변경, 암호화
     */
    public void approveSubscription(String tid, String pgToken) throws Exception {
        log.info("2.결제 승인 요청 시작. tid: {}, pgToken: {}", tid, pgToken);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // 결제 승인 요청 URL 세팅
            HttpPost httpPost = new HttpPost(kakaoPayProperties.getReadyApproveUrl());
            // tid를 사용하여 결제 정보 조회
            Optional<Payment> paymentTid = paymentRepository.findByTid(tid);
            if (paymentTid.isEmpty()) {
                throw new EntityNotFoundException("결제 정보를 찾을 수 없습니다.");
            }
            Payment lastPayment = paymentTid.get();
            // 해당 결제 건에 대한 user 정보 세팅.
            User user = lastPayment.getUser();

            // 승인 요청 객체 생성
            KakaoPaySubscriptionApproveRequest request = KakaoPaySubscriptionApproveRequest.fromProperties(kakaoPayProperties, lastPayment, pgToken);

            // 헤더 세팅
            setHeaders(httpPost, kakaoPayProperties.getSecretKey());

            // 바디 세팅
            String json = objectMapper.writeValueAsString(request);
            log.info("2.결제 승인 요청 json: {}", json);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            //응답값 받아서 처리
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("2-1. 결제 승인 응답값 json: {}", responseString);

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("카카오페이 요청 실패: " + responseString);
                }

                KakaoPaySubscriptionApproveResponse kakaoResponse = objectMapper.readValue(responseString, KakaoPaySubscriptionApproveResponse.class);

                // 결제 상태와 승인 시간 업데이트
                lastPayment.updateApprovePayment(PaymentStatus.APPROVED, kakaoResponse.getSid(), kakaoResponse.getApprovedAt());
                paymentRepository.save(lastPayment);
                log.info("2-2. 결제 승인 정보 저장 완료, 상태 APPROVED로 변경");

                // 기존 카드 정보 조회 및 삭제 중간에 한번 커밋 날려줘야함.
                Optional<Card> existingCard = cardRepository.findByUserId(user.getId());
                if (existingCard.isPresent()) {
                    cardRepository.delete(existingCard.get());
                    cardRepository.flush(); // 영속성 컨텍스트를 즉시 반영
                    log.info("2-3 기존 카드 정보가 있다면 삭제.");
                }

                // 카드 정보 암호화
                log.info("2-4. 카드 정보 암호화 전 sid: {}", kakaoResponse.getSid());
                String encryptedSid = encryptUtils.encrypt(kakaoResponse.getSid());
                log.info("2-4. 카드 정보 암호화 후 sid: {}", encryptedSid);

                // 카드 정보 저장
                Card card = new Card(user, kakaoResponse.getCardInfo(), encryptedSid);
                cardRepository.save(card);
                log.info("2-4. 카드 정보 저장");

                // payment 정보 삭제
                paymentRepository.delete(lastPayment);
                log.info("2-5. payment 정보 삭제");
            }
        }
    }

    /**
     * 3.결제취소 API 사용
     */
    public void cancelPayment(KakaoPayCancelRequest request) throws Exception {
        String tid = request.getTid();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(kakaoPayProperties.getCancelUrl());

            Optional<Payment> paymentOptional = paymentRepository.findByTid(tid);
            if (paymentOptional.isEmpty()) {
                throw new EntityNotFoundException("결제 정보를 찾을 수 없습니다.");
            }
            Payment payment = paymentOptional.get();

            // 환불 금액 계산
            PaymentRefundDTO refundDTO = paymentService.calculateRefund(payment);
            int refundAmount = refundDTO.getRefundAmount();

            // 요청값을 사용하여 KakaoPayCancelRequest 생성
            KakaoPayCancelRequest cancelRequest = KakaoPayCancelRequest.builder()
                .tid(tid)
                .cancelAmount(refundAmount) // 환불 금액 사용
                .cancelTaxFreeAmount(request.getCancelTaxFreeAmount())
                .cancelVatAmount(request.getCancelVatAmount())
                .cancelAvailableAmount(refundAmount)
                .payload(request.getPayload())
                .build();

            // HTTP 헤더 설정
            setHeaders(httpPost, kakaoPayProperties.getSecretKey());

            // JSON 직렬화 및 요청 실행
            String json = objectMapper.writeValueAsString(cancelRequest);
            log.info("취소요청 정보  KakaoPay: {}", json);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("Kakao Pay Cancel 응답: {}", responseString);

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("카카오페이 취소 요청 실패: " + responseString);
                }

                // 응답 처리
                KakaoPayCancelResponse cancelResponse = objectMapper.readValue(responseString, KakaoPayCancelResponse.class);
                log.info("결제 취소 정보 저장 전.");

                // 결제 상태 업데이트 및 저장
                payment.updateApprovePayment(PaymentStatus.REFUNDED_COMPLETED, payment.getSid(), cancelResponse.getCanceledAt());
                paymentRepository.save(payment);
                log.info("결제 취소 정보 저장 후.");
            }
        }
    }


    /**
     * 4.정기결제 userId와 ottId 필요.
     */
    @Transactional
    public void subscription(Long userId, Long ottId) throws Exception {
        log.info("4.정기결제 시작 ");
        //ott id를 역순으로 조회하고 가장 최근 것이 상태가 subscribed이면 regularNumber를 +1 하고 nextPaymentDate를 +30한다


        //Ott 정보 가져와서 가격 세팅
        Optional<Ott> ottOptional = ottRepository.findById(ottId);
        if (ottOptional.isEmpty()) {
            throw new EntityNotFoundException("OTT 정보를 찾을 수 없습니다.");
        }
        Ott ott = ottOptional.get();
        //가격 분할
        int price = ott.getPrice()/ott.getCapacity();

        //Card 정보 가져와서 sid 세팅
        Optional<Card> cardOptional = cardRepository.findByUserId(userId);
        if(cardOptional.isEmpty()){
            throw new EntityNotFoundException("Card 정보를 찾을 수 없습니다.");
        }
        Card card = cardOptional.get();
        String sid = card.getSid();
        log.info("2-5. 카드 정보 복호화 전 sid: {}", sid);
        String decryptedSid = encryptUtils.decrypt(sid);
        log.info("2-5. 카드 정보 복호화 후 sid: {}", decryptedSid);

        // User 정보 가져와서 partnerUserId 세팅
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User 정보를 찾을 수 없습니다."));

        String partnerUserId = user.getNickname();
        String partnerOrderId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        Optional<Payment> latestPaymentOptional = paymentRepository.findTopByUserIdAndOttIdOrderByPaymentApprovedAtDesc(userId, ottId);
        int installmentNumber = 1;

        // OTT의 최근 Payment가 존재하면 회차 정보 업데이트
        if (latestPaymentOptional.isPresent()) {
            Payment latestPayment = latestPaymentOptional.get();
            if (latestPayment.getStatus() == PaymentStatus.SUBSCRIBED) {
                // 이전 Payment의 상태를 COMPLETED로 변경
                log.info("이전 상태: {}", latestPayment.getStatus());
                latestPayment.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(latestPayment);
                log.info("변경 후 상태: {}", latestPayment.getStatus());

                log.info("4-0.이전 회차의 Payment 상태를 COMPLETED로 변경");

                installmentNumber = latestPayment.getInstallmentNumber() + 1;
            }
        }

        //정기결제 URL 세팅
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(kakaoPayProperties.getSubscribeUrl());

            //헤더 세팅
            setHeaders(httpPost, kakaoPayProperties.getSecretKey());

            String json = objectMapper.writeValueAsString(KakaoPaySubscriptionRequest.fromProperties(kakaoPayProperties, partnerOrderId, partnerUserId, price, decryptedSid));
            log.info("4-1.정기결제 요청 json: {}", json);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(),
                    StandardCharsets.UTF_8);
                log.info("4-2.정기결제 응답 json: {}", responseString);

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("카카오페이 정기결제 요청 실패: " + responseString);
                }

                KakaoPaySubscriptionResponse subscriptionResponse = objectMapper.readValue(
                    responseString, KakaoPaySubscriptionResponse.class);
                log.info("4-3.정기결제 정보 저장 전.");

                LocalDateTime approvedAt = subscriptionResponse.getApprovedAt();
                LocalDateTime subscribedAt = approvedAt.plusMonths(1);

                //응답 payment 저장.
                //todo: 몇 회차인지 체크할 필요가 있을까?
                Payment newPayment = Payment.builder()
                    .user(user)
                    .partnerUserId(partnerUserId)
                    .partnerOrderId(partnerOrderId)
                    .paymentType(PaymentType.KAKAOPAY)
                    .amount(price)
                    .cardCompany(subscriptionResponse.getCardInfo().getPurchaseCorp())
                    .status(PaymentStatus.SUBSCRIBED)
                    .paymentSubscribedAt(subscribedAt)
                    .paymentCreatedAt(subscriptionResponse.getCreatedAt())
                    .paymentApprovedAt(subscriptionResponse.getApprovedAt())
                    .cid(kakaoPayProperties.getSubscriptionCid())
                    .tid(subscriptionResponse.getTid())
                    .sid(subscriptionResponse.getSid())
                    .ottId(ottId)
                    .installmentNumber(installmentNumber)
                    .build();

                paymentRepository.save(newPayment);
                log.info("4-4. 정기결제 정보 저장 후.");
            }
        }
    }


    /**
     * 헤더 세팅 메소드
     */
    private void setHeaders(HttpPost httpPost, String secretKey) {
        httpPost.setHeader("Authorization", "SECRET_KEY " + secretKey);
        httpPost.setHeader("Content-Type", "application/json");
    }
}
