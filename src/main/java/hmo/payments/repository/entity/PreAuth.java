package hmo.payments.repository.entity;

import hmo.payments.domain.enums.PreAuthState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PRE_AUTH")
public class PreAuth {

    @Id
    @Column(name = "PRE_AUTH_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preAuthId;

    @Column(name = "STATE", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PreAuthState preAuthState;

    @ManyToOne
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private Payment payment;
}
