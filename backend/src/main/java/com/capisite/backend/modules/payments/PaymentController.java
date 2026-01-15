package com.capisite.backend.modules.payments;

import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<PaymentResponseDTO> create(@RequestBody @Validated CreateDonationDTO dto, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        var payment = paymentService.createPayment(dto, clientIp);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PaymentResponseDTO.from(payment));
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // Caso tenha múltiplos proxies, pega o primeiro IP da lista
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

}