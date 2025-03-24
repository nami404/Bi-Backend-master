package com.nami.springbootinit.proxytest2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author nami404
 * * @date 2025/2/27 16:53
 */
@Component
public class RestTemplateProxy {

    private final RestTemplate restTemplate;

    // @Value("${name.service.url}")
    private String paymentServiceUrl = "test";

    public RestTemplateProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Payment createPayment(ApiHeader apiHeader, Payment payment) {
        String uri = paymentServiceUrl + "/api/payment";

        HttpHeaders headers = new HttpHeaders();
        headers.add("requestId", apiHeader.getId());
        headers.add("clientType", apiHeader.getClientType().toString());

        HttpEntity<Payment> httpsEntity = new HttpEntity<>(payment, headers);

        ResponseEntity<Payment> response = restTemplate.exchange(uri,
                HttpMethod.POST,
                httpsEntity,
                Payment.class);

        return response.getBody();
    }
}
