package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.config.KakaoPayProperties;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionApproveRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionCreateRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionCreateResponse;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * 최초 카카오페이 카드등록 요청처리
     * userId를 파라미터로 전달받아 데이터 세팅후 카카오페이에 요청
     * tid, next_redirect_pc_url 확보
     */
    public KakaoPaySubscriptionCreateResponse createSubscription(Long userId) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(kakaoPayProperties.getSubscriptionCreateUrl());

            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new EntityNotFoundException("유저 ID를 찾을 수 없습니다.");
            }
            User user = userOptional.get();

            // partner_user_id는 유저 이름을 이용
            // partner_order_id는 랜덤한 8자리 문자열
            String partnerOrderId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String partnerUserId = user.getName();

            KakaoPaySubscriptionCreateRequest request = KakaoPaySubscriptionCreateRequest.fromProperties(kakaoPayProperties, partnerOrderId, partnerUserId);

            //헤더 세팅
            setHeaders(httpPost, kakaoPayProperties.getApiKey());

            //request 바디 세팅, application/x-www-form-urlencoded 형식 사용.
            String urlEncodedRequest = request.toFormUrlEncoded();
            log.info("Sending request to KakaoPay: {}", urlEncodedRequest);

            StringEntity entity = new StringEntity(urlEncodedRequest, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("Kakao Pay Subscription Create 응답: {}", responseString);

                KakaoPaySubscriptionCreateResponse kakaoResponse = objectMapper.readValue(responseString, KakaoPaySubscriptionCreateResponse.class);
                log.info("결제 정보 저장 전.");
                // 결제 정보 저장
                Payment payment = new Payment(
                    user,
                    partnerUserId,
                    partnerOrderId,
                    "CARD", // 예: 결제 유형 의미 있을까... 고민고민 여기있으면 나중에 수정도 어려움
                    kakaoPayProperties.getAmount(),
                    PaymentStatus.REQUESTED,
                    LocalDateTime.now(),
                    kakaoPayProperties.getCid(),
                    kakaoResponse.getTid(),
                    ""// SID는 추후 설정 일단 빈값
                );

                paymentRepository.save(payment);
                log.info("결제 정보 저장 후.");
                //결제 QR코드 URL 반환
                return KakaoPaySubscriptionCreateResponse.builder()
                    .nextRedirectPcUrl(kakaoResponse.getNextRedirectPcUrl())
                    .build();
            }
        }
    }

    /**
     * 카카오페이 정기결제를 위한 서비스 로직
     *
     */
    public KakaoPaySubscriptionCreateResponse approveSubscription(
        KakaoPaySubscriptionApproveRequest request) throws Exception {

        log.info("approveSubscription called with pgToken: {}", request.getPgToken());

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(kakaoPayProperties.getSubscriptionApprovalUrl());
            log.info("Subscription Approval URL: {}", approvalUrl);
            setHeaders(httpPost, kakaoPayProperties.getApiKey());
            StringEntity entity = new StringEntity(request.toFormData());
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                return objectMapper.readValue(responseString, KakaoPaySubscriptionCreateResponse.class);
            }
        }
    }

    private void setHeaders(HttpPost httpPost, String apiKey) {
        httpPost.setHeader("Authorization", "KakaoAK " + apiKey);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
    }
}
