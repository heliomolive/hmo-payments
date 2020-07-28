package hmo.payments.service.impl;

import hmo.payments.domain.dto.AuthDto;
import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.domain.dto.PreAuthDto;
import hmo.payments.domain.enums.AuthState;
import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PreAuthState;
import hmo.payments.service.AuthService;
import hmo.payments.service.OrderService;
import hmo.payments.service.PaymentService;
import hmo.payments.service.PreAuthService;
import hmo.payments.sm.PaymentStateMachine;
import hmo.payments.sm.PaymentStateMachineFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PreAuthService preAuthService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PaymentStateMachineFactory paymentStateMachineFactory;

    @Override
    @Transactional
    public PaymentDto createAndPreAuthorizePaymentOrder(BigDecimal amount) {

        log.info("Starting creation of new payment for [{}]", amount);

        // 1. create new payment:
        PaymentDto paymentDto = paymentService.createNewPayment(amount);

        // 2. execute pre-auth:
        PreAuthDto preAuthDto = preAuthService.preAuthorization(paymentDto.getPaymentId(), paymentDto.getAmount());

        // 3. handle pre-auth result as an event:
        PaymentStateMachine sm = paymentStateMachineFactory.create(paymentDto.getPaymentId());
        sm.sendEvent(PreAuthState.SUCCESS.equals(preAuthDto.getPreAuthState()) ?
                PaymentEvent.PRE_AUTH_APPROVE : PaymentEvent.PRE_AUTH_DECLINE);

        return paymentService.findPaymentByIdWithFetchDependencies(paymentDto.getPaymentId());
    }

    @Override
    @Transactional
    public PaymentDto authorizePaymentOrder(long paymentId) {
        log.info("Starting authorization of payment [{}]", paymentId);

        // 1. execute auth:
        AuthDto authDto = authService.authorization(paymentId);

        // 2. handle auth result as an event:
        PaymentStateMachine sm = paymentStateMachineFactory.create(paymentId);
        sm.sendEvent(AuthState.SUCCESS.equals(authDto.getAuthState()) ?
                PaymentEvent.AUTH_APPROVE : PaymentEvent.AUTH_DECLINE);

        return paymentService.findPaymentByIdWithFetchDependencies(paymentId);
    }

    @Override
    public PaymentDto findPaymentOrder(long paymentId) {
        return paymentService.findPaymentByIdWithFetchDependencies(paymentId);
    }

    @Override
    @Transactional
    public PaymentDto cancelPreAuthorization(long preAuthId) {
        log.info("Canceling payment pre-authorization [{}]", preAuthId);

        // 1. cancel pre-auth
        PreAuthDto preAuthDto = preAuthService.cancelPreAuthorization(preAuthId);

        // 2. handle pre-auth cancellation as an event:
        if (PreAuthState.CANCELLED.equals(preAuthDto.getPreAuthState())) {
            PaymentStateMachine sm = paymentStateMachineFactory.create(preAuthDto.getPaymentId());
            sm.sendEvent(PaymentEvent.PRE_AUTH_CANCEL);
        }

        return paymentService.findPaymentByIdWithFetchDependencies(preAuthDto.getPaymentId());
    }

    @Override
    @Transactional
    public PaymentDto cancelAuthorization(long authId) {
        log.info("Canceling payment authorization [{}]", authId);

        // 1. cancel auth
        AuthDto authDto = authService.cancelAuthorization(authId);

        // 2. handle auth cancellation as an event
        if (AuthState.CANCELLED.equals(authDto.getAuthState())) {
            PaymentStateMachine sm = paymentStateMachineFactory.create(authDto.getPaymentId());
            sm.sendEvent(PaymentEvent.AUTH_CANCEL);
        }

        return paymentService.findPaymentByIdWithFetchDependencies(authDto.getPaymentId());
    }
}
