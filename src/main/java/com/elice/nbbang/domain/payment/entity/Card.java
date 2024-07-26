package com.elice.nbbang.domain.payment.entity;

import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionApproveResponse;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.global.util.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card")
@Entity
public class Card extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    //카드 매입사명
    private String purchaseCorp;

    //매입사 코드
    private String purchaseCorpCode;

    //카드 발급사명
    private String issuerCorp;

    //카드 발급사 코드
    private String issuerCorpCode;

    //카드 BIN (카드발급사/금융기관을 식별하는 번호)
    private String bin;

    //카드 타입(신용/체크 등)
    private String cardType;

    //할부 개월 수
    private String installMonth;

    //카드 승인번호
    private String approvedId;

    //카드 가맹점 번호
    private String cardMid;

    //무이자 할부여부(Y/N)
    private String interestFreeInstall;

    //카드 상품 코드
    private String cardItemCode;

    //할부 유형 2가지 (CARD_INSTALLMENT(업종 무이자),SHARE_INSTALLMENT(분담 무이자))
    //디폴트 세팅
    private String installmentType="CARD_INSTALLMENT";

    public Card(User user, KakaoPaySubscriptionApproveResponse.CardInfo cardInfo) {
        this.user = user;
        this.purchaseCorp = cardInfo.getPurchaseCorp();
        this.purchaseCorpCode = cardInfo.getPurchaseCorpCode();
        this.issuerCorp = cardInfo.getIssuerCorp();
        this.issuerCorpCode = cardInfo.getIssuerCorpCode();
        this.bin = cardInfo.getBin();
        this.cardType = cardInfo.getCardType();
        this.installMonth = cardInfo.getInstallMonth();
        this.approvedId = cardInfo.getApprovedId();
        this.cardMid = cardInfo.getCardMid();
        this.interestFreeInstall = cardInfo.getInterestFreeInstall();
        this.cardItemCode = cardInfo.getCardItemCode();
        this.installmentType = cardInfo.getInstallmentType();
    }
}
