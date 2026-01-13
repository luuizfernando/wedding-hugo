package com.capisite.backend.modules.payments;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capisite.backend.modules.payments.dto.CreateDonationDTO;
import com.capisite.backend.modules.payments.dto.PaymentResponseDTO;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(@RequestBody @Validated CreateDonationDTO dto) {
        Payment payment = paymentService.createPayment(dto);

        var response = new PaymentResponseDTO(
            payment.getId(),
            payment.getStatus().toString(),
            payment.getPaymentUrl(),
            payment.getAmount(),
            payment.getExternalReference()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}