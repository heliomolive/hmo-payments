package hmo.payments.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource(value = "classpath:/application.properties", encoding = "UTF-8"),
})
@Import(value = PaymentStateMachineConfig.class)
public class PaymentsConfig {

}
