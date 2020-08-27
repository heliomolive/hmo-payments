package hmo.payments.service.impl;

import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.domain.exception.UnprocessableEntityException;
import hmo.payments.service.OrderService;
import hmo.payments.service.PaymentService;
import hmo.payments.sm.PaymentStateMachine;
import hmo.payments.sm.PaymentStateMachineFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentStateMachineFactory paymentStateMachineFactory;

    @Override
    public PaymentDto createAndPreAuthorizePaymentOrder(BigDecimal amount) {

        log.info("Starting creation of new payment for [{}]", amount);
        PaymentDto paymentDto = paymentService.createNewPayment(amount);

        PaymentStateMachine sm = paymentStateMachineFactory.create(paymentDto);
        PaymentState newState = sm.sendEvent(PaymentEvent.PRE_AUTH_REQUEST);
        paymentDto.setState(newState);

        log.info("Payment [{}] [{}]", paymentDto.getPaymentId(), paymentDto.getState());
        return paymentDto;
    }

    @Override
    public PaymentDto authorizePaymentOrder(Long paymentId) {

        PaymentDto paymentDto = paymentService.findPaymentById(paymentId);
        if (!PaymentState.PRE_AUTH_SUCCESS.equals(paymentDto.getState())) {
            throw new UnprocessableEntityException(
                    String.format("Can not authorize payment with state [%s]", paymentDto.getState()));
        }

        log.info("Starting authorization of payment [{}]", paymentId);

        PaymentStateMachine sm = paymentStateMachineFactory.create(paymentDto);
        PaymentState newState = sm.sendEvent(PaymentEvent.AUTH_REQUEST);
        paymentDto.setState(newState);

        log.info("Payment [{}] [{}]", paymentDto.getPaymentId(), paymentDto.getState());
        return paymentDto;
    }

    @Override
    public PaymentDto findPaymentOrder(Long paymentId) {
        return paymentService.findPaymentByIdWithFetchDependencies(paymentId);
    }

    @Override
    public PaymentDto settlePaymentOrder(Long paymentId) {

        PaymentDto paymentDto = paymentService.findPaymentById(paymentId);
        if (!PaymentState.AUTH_SUCCESS.equals(paymentDto.getState())) {
            throw new UnprocessableEntityException(
                    String.format("Can not settle payment with state [%s]", paymentDto.getState()));
        }

        log.info("Starting settlement of payment [{}]", paymentId);

        PaymentStateMachine sm = paymentStateMachineFactory.create(paymentDto);
        PaymentState newState = sm.sendEvent(PaymentEvent.PAYMENT_SETTLEMENT);
        paymentDto.setState(newState);

        log.info("Payment [{}] [{}]", paymentDto.getPaymentId(), paymentDto.getState());
        return paymentDto;
    }

    @Override
    public PaymentDto cancelPaymentOrder(Long paymentId) {

        PaymentDto paymentDto = paymentService.findPaymentById(paymentId);
        if (!paymentDto.getState().isPaymentRegistered() && !paymentDto.getState().isPaymentInProgress()) {
            throw new UnprocessableEntityException(
                    String.format("Can not cancel payment with state [%s]", paymentDto.getState()));
        }

        log.info("Starting cancel of payment [{}]", paymentId);

        PaymentStateMachine sm = paymentStateMachineFactory.create(paymentDto);
        PaymentState newState = sm.sendEvent(PaymentEvent.PAYMENT_CANCEL);
        paymentDto.setState(newState);

        log.info("Payment [{}] [{}]", paymentDto.getPaymentId(), paymentDto.getState());
        return paymentDto;
    }

}
