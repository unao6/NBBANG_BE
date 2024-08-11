package com.elice.nbbang.domain.auth.service;

import com.elice.nbbang.domain.auth.dto.request.PhoneCertificationRequestDto;
import com.elice.nbbang.domain.auth.dto.request.PhoneCheckRequestDto;
import com.elice.nbbang.domain.auth.repository.SmsCertification;
import com.elice.nbbang.global.config.MessageProperties;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final SmsCertification smsCertification;
    private final MessageProperties messageProperties;

    private String createRandomNumber() {
        Random rand = new Random();
        String randomNum = "";
        for (int i = 0; i < 4; i++) {
            String random = Integer.toString(rand.nextInt(10));
            randomNum += random;
        }

        return randomNum;
    }

    private HashMap<String, String> makeParams(String to, String randomNum) {
        HashMap<String, String> params = new HashMap<>();
        params.put("from", messageProperties.getFromnumber());
        params.put("type", "SMS");
        params.put("app_version", "test app 1.2");
        params.put("to", to);
        params.put("text", randomNum);

        return params;
    }

    // 인증번호 전송하기
    public String sendSMS(PhoneCertificationRequestDto phoneCertificationRequestDto) {
        Message coolsms = new Message(messageProperties.getApikey(), messageProperties.getApisecret());

        // 랜덤한 인증 번호 생성
        String randomNum = createRandomNumber();
        System.out.println(randomNum);

        // 발신 정보 설정
        HashMap<String, String> params = makeParams(phoneCertificationRequestDto.getPhoneNumber(), randomNum);
        params.put("text", "N/BBANG 휴대폰인증 메시지 : 인증번호는" + "["+randomNum+"]" + "입니다.");

        try {
            Map<String, Object> responseMap = coolsms.send(params); // Map으로 반환값을 받음

            // Map을 JSONObject로 변환
            JSONObject obj = new JSONObject(responseMap);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }

        // DB에 발송한 인증번호 저장
        smsCertification.createSmsCertification(phoneCertificationRequestDto.getPhoneNumber(),randomNum);

        return "문자 전송이 완료되었습니다.";
    }

    // 인증 번호 검증
    public String verifySms(PhoneCheckRequestDto requestDto) {
        if (isVerify(requestDto)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }
        smsCertification.deleteSmsCertification(requestDto.getPhoneNumber());

        return "인증 완료되었습니다.";
    }

    private boolean isVerify(PhoneCheckRequestDto requestDto) {
        return !(smsCertification.hasKey(requestDto.getPhoneNumber()) &&
                smsCertification.getSmsCertification(requestDto.getPhoneNumber())
                        .equals(requestDto.getRandomNumber()));
    }
}
