package com.capisite.backend.modules.payments;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.capisite.backend.infra.asaas.AsaasClient;
import com.capisite.backend.infra.asaas.dto.AsaasCustomerRequestDTO;
import com.capisite.backend.infra.asaas.dto.AsaasPaymentRequestDTO;
import com.capisite.backend.modules.donors.Donor;
import com.capisite.backend.modules.donors.DonorService;
import com.capisite.backend.modules.donors.dto.CreateDonorDTO;
import com.capisite.backend.modules.payments.dto.CreateDonationDTO;
import com.capisite.backend.modules.payments.enums.PaymentStatus;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DonorService donorService;
    private final AsaasClient asaasClient;

    @Value("${asaas.api.key}")
    private String apiKey;

    public PaymentService(PaymentRepository paymentRepository, DonorService donorService, AsaasClient asaasClient) {
        this.paymentRepository = paymentRepository;
        this.donorService = donorService;
        this.asaasClient = asaasClient;
    }

    @Transactional
    public Payment createPayment(CreateDonationDTO data) {
        Donor donor = donorService.findOrCreateDonor(new CreateDonorDTO(data.name(), data.email(), data.document()));
        if (donor.getExternalId() == null) {
            try {
                var request = new AsaasCustomerRequestDTO(donor.getName(), donor.getDocument());
                var response = asaasClient.createCustomer(apiKey, request);
                donor.setExternalId(response.id());
            } catch (Exception e) {
                throw new RuntimeException("Erro ao criar cliente no Asaas: " + e.getMessage());
            }
        }

        Payment payment = new Payment();
        payment.setDonor(donor);
        payment.setAmount(data.amount());
        payment.setBillingType(data.billingType());
        payment.setMessage(data.message());
        payment.setStatus(PaymentStatus.PENDING);
        
        payment = paymentRepository.save(payment);

        try {
            var request = new AsaasPaymentRequestDTO(
                    donor.getExternalId(),
                    data.billingType(),
                    data.amount(),
                    LocalDate.now(),
                    data.message()
            );

            var asaasResponse = asaasClient.createPayment(apiKey, request);
            
            payment.setExternalReference(asaasResponse.id());
            payment.setPaymentUrl(asaasResponse.invoiceUrl());

            if ("PIX".equalsIgnoreCase(data.billingType())) {
                try {
                    var pixResponse = asaasClient.getPixQrCode(apiKey, asaasResponse.id());
                    payment.setPaymentUrl(pixResponse.payload());
                } catch (Exception e) {
                    System.out.println("Falha ao pegar Pix (usando link da fatura): " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com o Asaas", e);
        }

        return paymentRepository.save(payment);
    }

}