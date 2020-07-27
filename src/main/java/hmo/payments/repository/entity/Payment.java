package hmo.payments.repository.entity;

import hmo.payments.domain.enums.PaymentState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PAYMENT")
public class Payment {

    @Id
    @Column(name = "PAYMENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(name = "STATE", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentState state;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "payment")
    private Set<PreAuth> preAuthSet;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "payment")
    private Set<Auth> authSet;
}
