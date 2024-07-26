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
import com.elice.nbbang.domain.payment.repository.CardRepository;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;
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
     * 1.결제준비
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
            String partnerUserId = user.getName();

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

                // 결제준비 정보 저장
                Payment payment = new Payment(
                    user,
                    partnerUserId,
                    partnerOrderId,
                    kakaoPayProperties.getPaymentMethodType(),
                    kakaoPayProperties.getTotalAmount(),
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
     * 2.결제승인 (최초결제 자동취소 포함)
     * tid와 pg_token을 파라미터로 전달받아 데이터 세팅후 카카오페이에 요청
     * sid 확보, 상태변경
     */
    public void approveSubscription(String tid, String pgToken) throws Exception {

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

                // 카드 정보 저장
                Card card = new Card(user, kakaoResponse.getCardInfo());
                cardRepository.save(card);
                log.info("카드 정보 저장 완료.");

                //이것도 근데 값을 이렇게 세팅하는게 아니라 받아와야할듯 나중에수정!
                cancelPayment(KakaoPayCancelRequest.builder()
                    .tid(tid)
                    .cancelAmount(kakaoPayProperties.getTotalAmount())
                    .cancelTaxFreeAmount(0)
                    .cancelVatAmount(0)
                    .payload("최초결제 자동취소")
                    .build());
            }
        }
    }

    /**
     * 3.결제취소
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
     * 4.정기결제
     */
    public void subscription(Long userId, String tid, String sid) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(kakaoPayProperties.getSubscribeUrl());

            Optional<Payment> paymentSid = paymentRepository.findByUserIdAndTidAndSid(userId, tid, sid);
            if (paymentSid.isEmpty()) {
                throw new EntityNotFoundException("결제 정보를 찾을 수 없습니다.");
            }
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
