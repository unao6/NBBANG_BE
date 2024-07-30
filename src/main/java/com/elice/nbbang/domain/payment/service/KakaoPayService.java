package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.config.KakaoPayProperties;
import com.elice.nbbang.domain.payment.dto.KakaoPayCancelRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPayCancelResponse;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionApproveRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionApproveResponse;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionCreateRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionCreateResponse;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionResponse;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.repository.CardRepository;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;
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


    /**
     * 1.결제준비 (카드 등록을 위한)
     * userId를 파라미터로 전달받아 데이터 세팅후 카카오페이에 요청
     * tid, next_redirect_pc_url 확보
     */
    public KakaoPaySubscriptionCreateResponse createSubscription(Long userId) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {

            // 카카오페이 결제준비 요청 URL
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

            log.info("요청값 완성: {}", json);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("응답값: {}", responseString);

                KakaoPaySubscriptionCreateResponse kakaoResponse = objectMapper.readValue(responseString, KakaoPaySubscriptionCreateResponse.class);
                //todo: 이거 최종 카드 저장 안되면 삭제해야함.
                // 결제준비 정보 저장
                Payment payment = new Payment(
                    user,
                    partnerUserId,
                    partnerOrderId,
                    PaymentType.CARD,
                    kakaoPayProperties.getTestAmount(),
                    PaymentStatus.CREATED,
                    kakaoResponse.getCreatedAt(),
                    kakaoPayProperties.getSubscriptionCid(),
                    kakaoResponse.getTid()
                );

                paymentRepository.save(payment);
                log.info("결제준비단계에서 저장후 로그임.");
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
     * tid와 pg_token을 파라미터로 전달받아 데이터 세팅후 카카오페이에 요청
     * sid 확보, 상태변경
     */
    public void approveSubscription(String tid, String pgToken) throws Exception {
        log.info("결제 승인 요청 시작. tid: {}, pgToken: {}", tid, pgToken);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // 결제 승인 요청 URL
            HttpPost httpPost = new HttpPost(kakaoPayProperties.getReadyApproveUrl());

            // tid를 사용하여 결제 정보 조회
            Optional<Payment> paymentTid = paymentRepository.findByTid(tid);
            if (paymentTid.isEmpty()) {
                throw new EntityNotFoundException("결제 정보를 찾을 수 없습니다.");
            }
            Payment lastPayment = paymentTid.get();
            User user = lastPayment.getUser();

            // 승인 요청 객체 생성
            KakaoPaySubscriptionApproveRequest request = KakaoPaySubscriptionApproveRequest.fromProperties(kakaoPayProperties, lastPayment, pgToken);

            // 헤더 세팅
            setHeaders(httpPost, kakaoPayProperties.getSecretKey());

            // 바디 세팅
            String json = objectMapper.writeValueAsString(request);
            log.info("승인요청 제이슨...: {}", json);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("카카오페이 응답: {}", responseString);

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("카카오페이 요청 실패: " + responseString);
                }

                KakaoPaySubscriptionApproveResponse kakaoResponse = objectMapper.readValue(responseString, KakaoPaySubscriptionApproveResponse.class);
                log.info("결제 승인 정보 저장 전.");

                // 결제 상태와 승인 시간 업데이트
                lastPayment.updateApprovePayment(PaymentStatus.APPROVED, kakaoResponse.getSid(), kakaoResponse.getApprovedAt());
                paymentRepository.save(lastPayment);
                log.info("결제 승인 정보 저장 후.");

                // 기존 카드 정보 조회 및 삭제 중간에 한번 커밋 날려줘야함.
                Optional<Card> existingCard = cardRepository.findByUserId(user.getId());
                if (existingCard.isPresent()) {
                    cardRepository.delete(existingCard.get());
                    cardRepository.flush(); // 영속성 컨텍스트를 즉시 반영
                    log.info("기존 카드 정보 삭제 완료.");
                }

                // 카드 정보 저장
                Card card = new Card(user, kakaoResponse.getCardInfo(), kakaoResponse.getSid());
                cardRepository.save(card);
                log.info("카드 정보 저장 완료.");
            }
        }
    }

    /**
     * 3.결제취소 API 사용
     */
    public void cancelPayment(KakaoPayCancelRequest request) throws Exception {
        String tid = request.getTid();
        Integer cancelAmount = request.getCancelAmount();
        Integer cancelTaxFreeAmount = request.getCancelTaxFreeAmount();
        Integer cancelVatAmount = request.getCancelVatAmount();
        Integer cancelAvailableAmount = request.getCancelAvailableAmount();
        String payload = request.getPayload();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(kakaoPayProperties.getCancelUrl());

            Optional<Payment> paymentTid = paymentRepository.findByTid(tid);
            if (paymentTid.isEmpty()) {
                throw new EntityNotFoundException("결제 정보를 찾을 수 없습니다.");
            }
            Payment payment = paymentTid.get();

            KakaoPayCancelRequest cancelRequest = KakaoPayCancelRequest.fromProperties(kakaoPayProperties, payment, cancelAmount, cancelTaxFreeAmount, cancelVatAmount, cancelAvailableAmount, payload);

            setHeaders(httpPost, kakaoPayProperties.getSecretKey());

            String json = objectMapper.writeValueAsString(cancelRequest);
            log.info("Sending cancel request to KakaoPay: {}", json);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("Kakao Pay Cancel 응답: {}", responseString);

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("카카오페이 취소 요청 실패: " + responseString);
                }

                KakaoPayCancelResponse cancelResponse = objectMapper.readValue(responseString, KakaoPayCancelResponse.class);
                log.info("결제 취소 정보 저장 전.");

                payment.updateApprovePayment(PaymentStatus.CANCELED, payment.getSid(), cancelResponse.getCanceledAt());
                paymentRepository.save(payment);
                log.info("결제 취소 정보 저장 후.");
            }
        }
    }

    /**
     *  3-1. 결제취소 내부 자동취소 로직 -> 3번 API 사용
     */
    public void autoCancelPayment(Long userId, Long ottId) throws Exception {
        List<Payment> paymentList = paymentRepository.findByUserIdAndOttIdOrderByPaymentApprovedAtDesc(userId, ottId);

        log.info("결제 내역 조회 userId: {}, ottId: {}", userId, ottId);
        if (!paymentList.isEmpty()) {
            Payment latestPayment = paymentList.get(0);

            String tid = latestPayment.getTid();
            LocalDateTime paymentApprovedDateTime = latestPayment.getPaymentApprovedAt();
            LocalDateTime currentDateTime = LocalDateTime.now();

            //Integer ottMonthlyAmount = ottService.getOttMonthlyAmount(ottId);
            Integer ottMonthlyAmount = 5000;

            Integer totalCancelAmount = calculateTotalCancelAmount(paymentApprovedDateTime, currentDateTime, ottMonthlyAmount);

            Integer cancelTaxFreeAmount = 0; // 세금 비과세 금액
            Integer cancelVatAmount = 0; // 부가세 금액
            Integer cancelAvailableAmount = totalCancelAmount - cancelTaxFreeAmount - cancelVatAmount;
            String payload = "";

            KakaoPayCancelRequest cancelRequest = KakaoPayCancelRequest.builder()
                .tid(tid)
                .cancelAmount(totalCancelAmount)
                .cancelTaxFreeAmount(cancelTaxFreeAmount)
                .cancelVatAmount(cancelVatAmount)
                .cancelAvailableAmount(cancelAvailableAmount)
                .payload(payload)
                .build();

            cancelPayment(cancelRequest);

        } else {
            System.out.println("결제 내역이 없습니다. userId: " + userId + ", ottId: " + ottId);
        }
    }

    /**
     * 3-2. 결제취소 내부 자동취소 로직
     */
    public Integer calculateTotalCancelAmount(LocalDateTime paymentApprovedDateTime, LocalDateTime currentDateTime, Integer ottMonthlyAmount) {
        long daysBetween = ChronoUnit.DAYS.between(paymentApprovedDateTime.toLocalDate(), currentDateTime.toLocalDate());
        // 하루가 지났으면 1일로 간주하고, 지나지 않았으면 0일로 간주
        long extraDay = paymentApprovedDateTime.toLocalTime().isBefore(currentDateTime.toLocalTime()) ? 1 : 0;
        long totalDays = daysBetween + extraDay;

        // 정기 결제일을 30일로 고정
        long daysInMonth = 30;
        Integer dailyAmount = ottMonthlyAmount / (int) daysInMonth;

        Integer cancelAmount = dailyAmount * (int) totalDays;
        return cancelAmount;
    }



    /**
     * 4.정기결제 여기근데 tid가 필요한가? 필요없을거같은데..??가 아니라 필요가 없음 새로운 payment 생성해서 sid를 가져와야함
     */
    public void subscription(Long userId, String tid, String sid) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(kakaoPayProperties.getSubscribeUrl());
            //이 과정이 필요가 없음
            //OTT에서 금액을 가져와서 넣어줘야함
            Optional<Payment> paymentSid = paymentRepository.findByUserIdAndTidAndSid(userId, tid, sid);
            if (paymentSid.isEmpty()) {
                throw new EntityNotFoundException("결제 정보를 찾을 수 없습니다.");
            }
            //이거 카드에서 가져와야함. sid 값을
            Payment lastPayment = paymentSid.get();

            setHeaders(httpPost, kakaoPayProperties.getSecretKey());

            String json = objectMapper.writeValueAsString(KakaoPaySubscriptionRequest.fromProperties(kakaoPayProperties, lastPayment));
            log.info("정기결제 요청 KakaoPay: {}", json);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(),
                    StandardCharsets.UTF_8);
                log.info("Kakao Pay Subscription 응답: {}", responseString);

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("카카오페이 정기결제 요청 실패: " + responseString);
                }

                KakaoPaySubscriptionResponse subscriptionResponse = objectMapper.readValue(
                    responseString, KakaoPaySubscriptionResponse.class);
                log.info("정기결제 정보 저장 전.");

                lastPayment.updateSubscribtionPayment(PaymentStatus.SUBSCRIBED, subscriptionResponse.getApprovedAt());
                paymentRepository.save(lastPayment);
                //test ott id 추가
                //lastPayment.setOttId(1L);
                log.info("정기결제 정보 저장 후.");
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
