package hmo.payments.controller;

import hmo.payments.controller.request.AuthRequest;
import hmo.payments.controller.request.PreAuthRequest;
import hmo.payments.controller.response.PaymentResponse;
import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.domain.mapper.PaymentMapper;
import hmo.payments.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentMapper paymentMapper;

    @PostMapping("/v1/preauth")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse requestPreAuth(@Valid @RequestBody PreAuthRequest request) {

        PaymentDto paymentDto = orderService.createAndPreAuthorizePaymentOrder(request.getValue());
        return paymentMapper.getPaymentResponse(paymentDto);
    }

    @PostMapping("/v1/auth")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse requestAuth(@Valid @RequestBody AuthRequest request) {

        PaymentDto paymentDto = orderService.authorizePaymentOrder(request.getPaymentId());
        return paymentMapper.getPaymentResponse(paymentDto);
    }

    @GetMapping("/v1/payment/{paymentId}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse getPayment(@PathVariable Long paymentId) {

        PaymentDto paymentDto = orderService.findPaymentOrder(paymentId);
        return paymentMapper.getPaymentResponse(paymentDto);
    }

    @DeleteMapping("/v1/preauth/{preAuthId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public PaymentResponse cancelPreAuth(@PathVariable Long preAuthId) {

        PaymentDto paymentDto = orderService.cancelPreAuthorization(preAuthId);
        return paymentMapper.getPaymentResponse(paymentDto);
    }

    @DeleteMapping("/v1/auth/{authId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public PaymentResponse cancelAuth(@PathVariable Long authId) {

        PaymentDto paymentDto = orderService.cancelAuthorization(authId);
        return paymentMapper.getPaymentResponse(paymentDto);
    }
}
