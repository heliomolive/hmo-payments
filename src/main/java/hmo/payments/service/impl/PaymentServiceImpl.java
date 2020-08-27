package hmo.payments.service.impl;

import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.domain.exception.NotFoundException;
import hmo.payments.domain.mapper.PaymentMapper;
import hmo.payments.repository.PaymentRepository;
import hmo.payments.repository.entity.Payment;
import hmo.payments.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.String.format;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentDto createNewPayment(BigDecimal amount) {
        Payment payment = paymentRepository.save(
                Payment.builder()
                        .state(PaymentState.NEW)
                        .amount(amount)
                        .build());
        log.info("New payment created: [{}]", payment.toString());
        return paymentMapper.getPaymentDto(payment);
    }

    @Override
    public PaymentDto findPaymentById(Long paymentId) {
        return paymentMapper.getPaymentDto(
                findPaymentEntity(paymentId, false));
    }

    @Override
    public PaymentDto findPaymentByIdWithFetchDependencies(Long paymentId) {
        return paymentMapper.getPaymentDtoWithDependences(
                findPaymentEntity(paymentId, true));
    }

    @Override
    @Transactional
    public PaymentDto preAuthRequested(Long paymentId) {
        return updatePaymentStateAndReturnDto(paymentId, PaymentState.PRE_AUTH_REQUESTED);
    }

    @Override
    @Transactional
    public PaymentDto preAuthApproved(Long paymentId) {
        return updatePaymentStateAndReturnDto(paymentId, PaymentState.PRE_AUTH_SUCCESS);
    }

    @Override
    @Transactional
    public PaymentDto preAuthDeclined(Long paymentId) {
        return updatePaymentStateAndReturnDto(paymentId, PaymentState.PRE_AUTH_DECLINED);
    }

    @Override
    @Transactional
    public PaymentDto authRequested(Long paymentId) {
        return updatePaymentStateAndReturnDto(paymentId, PaymentState.AUTH_REQUESTED);
    }

    @Override
    @Transactional
    public PaymentDto authApproved(Long paymentId) {
        return updatePaymentStateAndReturnDto(paymentId, PaymentState.AUTH_SUCCESS);
    }

    @Override
    @Transactional
    public PaymentDto authDeclined(Long paymentId) {
        return updatePaymentStateAndReturnDto(paymentId, PaymentState.AUTH_DECLINED);
    }

    @Override
    @Transactional
    public PaymentDto paymentSettled(Long paymentId) {
        return updatePaymentStateAndReturnDto(paymentId, PaymentState.SETTLED);
    }

    @Override
    @Transactional
    public PaymentDto paymentCancelRequested(Long paymentId) {
        return updatePaymentStateAndReturnDto(paymentId, PaymentState.CANCEL_REQUESTED);
    }

    @Override
    @Transactional
    public PaymentDto paymentCancelled(Long paymentId) {
        return updatePaymentStateAndReturnDto(paymentId, PaymentState.CANCELLED);
    }

    private Payment findPaymentEntity(Long paymentId, boolean fetchDependence) {
        Payment payment;

        if (fetchDependence) {
            payment = paymentRepository.findByIdWithFetchDependence(paymentId)
                    .orElseThrow(() -> new NotFoundException(format("Payment [%d] not found", paymentId)));
        } else {
            payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new NotFoundException(format("Payment [%d] not found", paymentId)));
        }

        payment.setAmount(payment.getAmount().setScale(2, RoundingMode.FLOOR));
        return payment;
    }

    private PaymentDto updatePaymentStateAndReturnDto(Long paymentId, PaymentState paymentState) {
        log.info("Updating payment [{}] to state [{}]", paymentId, paymentState);
        Payment payment = findPaymentEntity(paymentId, false);
        payment.setState(paymentState);
        return paymentMapper.getPaymentDto(paymentRepository.save(payment));
    }
}
