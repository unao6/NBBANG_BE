package com.elice.nbbang.domain.payment.dto;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReserve {

    private String billingKey;

    private User user;

    private Ott ott;

    private LocalDateTime paymentSubscribedAt;
}
