package hmo.payments.service;

import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.repository.PaymentRepository;
import hmo.payments.repository.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

//@SpringBootTest
class PaymentStateMachineInterceptorTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    public void beforeEach() {
//        payment = Payment.builder().amount(new BigDecimal(1198)).build();
    }

//    @Test
    public void preAuth() {
        // Given:
        PaymentDto savedPayment = paymentService.createNewPayment(new BigDecimal(1198));

        // When:
//        paymentService.requestPreAuth(savedPayment.getPaymentId());

        // Then:
        Payment payment2 = paymentRepository.findById(savedPayment.getPaymentId()).get();
        System.out.println(payment);
    }
}