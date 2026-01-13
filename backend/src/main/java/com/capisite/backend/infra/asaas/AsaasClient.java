package com.capisite.backend.infra.asaas;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.capisite.backend.infra.asaas.dto.AsaasCustomerRequestDTO;
import com.capisite.backend.infra.asaas.dto.AsaasCustomerResponseDTO;
import com.capisite.backend.infra.asaas.dto.AsaasPaymentRequestDTO;
import com.capisite.backend.infra.asaas.dto.AsaasPaymentResponseDTO;
import com.capisite.backend.infra.asaas.dto.AsaasPixResponseDTO;

@FeignClient(name = "AsaasClient", url = "${asaas.url}")
public interface AsaasClient {

    @PostMapping(value = "/customers")
    AsaasCustomerResponseDTO createCustomer(
            @RequestHeader("access_token") String accessToken,
            @RequestBody AsaasCustomerRequestDTO request
    );

    @PostMapping(value = "/payments")
    AsaasPaymentResponseDTO createPayment(
            @RequestHeader("access_token") String accessToken,
            @RequestBody AsaasPaymentRequestDTO request
    );

    @GetMapping(value = "/payments/{id}/pixQrCode")
    AsaasPixResponseDTO getPixQrCode(
            @RequestHeader("access_token") String accessToken,
            @PathVariable("id") String paymentId
    );

}