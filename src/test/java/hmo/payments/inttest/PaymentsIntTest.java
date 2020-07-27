package hmo.payments.inttest;

import com.fasterxml.jackson.databind.ObjectMapper;
import hmo.payments.controller.request.AuthRequest;
import hmo.payments.controller.request.PreAuthRequest;
import hmo.payments.controller.response.PaymentResponse;
import hmo.payments.domain.enums.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentsIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PRE_AUTH_V1_URI = "/v1/preauth";
    private static final String AUTH_V1_URI = "/v1/auth";

    @Test
    public void preAuthSuccessAndAuthSuccess() throws Exception {
        BigDecimal value = new BigDecimal("29.90").setScale(2, RoundingMode.FLOOR);

        ResultActions resultActions = mockMvc.perform(post(PRE_AUTH_V1_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PreAuthRequest(value))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.state").value(PaymentState.PRE_AUTH_SUCCESS.toString()))
                .andExpect(jsonPath("$.amount").value(value.floatValue()));

        PaymentResponse paymentResponse = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(), PaymentResponse.class);
        assertNotNull(paymentResponse.getPaymentId());

        mockMvc.perform(post(AUTH_V1_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthRequest(paymentResponse.getPaymentId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.state").value(PaymentState.AUTH_SUCCESS.toString()))
                .andExpect(jsonPath("$.amount").value(value.floatValue()))
                .andExpect(jsonPath("$.paymentId").value(paymentResponse.getPaymentId()));
    }

}
