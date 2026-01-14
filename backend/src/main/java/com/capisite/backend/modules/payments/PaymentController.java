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

        var payment = paymentService.createPayment(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PaymentResponseDTO.from(payment));
    }

}