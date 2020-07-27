package hmo.payments.repository.entity;

import hmo.payments.domain.enums.AuthState;
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
@Table(name = "AUTH")
public class Auth {

    @Id
    @Column(name = "AUTH_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @Column(name = "STATE", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AuthState authState;

    @ManyToOne
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private Payment payment;
}
