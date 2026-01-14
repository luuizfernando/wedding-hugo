package com.capisite.backend.modules.payments;

import com.asaas.apisdk.AsaasSdk;
import com.asaas.apisdk.models.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.capisite.backend.modules.donors.Donor;
import com.capisite.backend.modules.donors.DonorService;
import com.capisite.backend.modules.donors.dto.CreateDonorDTO;
import com.capisite.backend.modules.payments.dto.CreateDonationDTO;
import com.capisite.backend.modules.payments.enums.PaymentStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DonorService donorService;
    private final AsaasSdk asaasSdk;

    public PaymentService(PaymentRepository paymentRepository, DonorService donorService, AsaasSdk asaasSdk) {
        this.paymentRepository = paymentRepository;
        this.donorService = donorService;
        this.asaasSdk = asaasSdk;
    }

    @Transactional
    public Payment createPayment(CreateDonationDTO data) {
        Donor donor = setupDonor(data);

        Payment payment = initializePayment(donor, data);

        try {
            PaymentGetResponseDto asaasResponse;

            if ("CREDIT_CARD".equalsIgnoreCase(data.billingType())) {
                asaasResponse = processCreditCardPayment(donor, data);
            } else if ("PIX".equalsIgnoreCase(data.billingType())) {
                asaasResponse = processPixPayment(donor, data);
            } else {
                throw new IllegalArgumentException("Método de pagamento não suportado: " + data.billingType());
            }

            updatePixQrCodeLink(payment, asaasResponse);

        } catch (Exception e) {
            throw new RuntimeException("Falha na integração com Asaas: " + e.getMessage(), e);
        }

        return paymentRepository.save(payment);
    }

    // --- Métodos Auxiliares ---
    private Donor setupDonor(CreateDonationDTO data) {
        Donor donor = donorService.findOrCreateDonor(
                new CreateDonorDTO(data.name(), data.email(), data.document())
        );
        ensureAsaasCustomer(donor);
        return donor;
    }

    private void ensureAsaasCustomer(Donor donor) {
        if (donor.getExternalId() != null) return;

        try {
            CustomerSaveRequestDto request = CustomerSaveRequestDto.builder()
                    .name(donor.getName())
                    .email(donor.getEmail())
                    .cpfCnpj(donor.getDocument())
                    .build();

            CustomerGetResponseDto response = asaasSdk.customer.createNewCustomer(request);
            donor.setExternalId(response.getId());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar cliente no Asaas: " + e.getMessage());
        }
    }

    private Payment initializePayment(Donor donor, CreateDonationDTO data) {
        Payment payment = new Payment();
        payment.setDonor(donor);
        payment.setAmount(data.amount());
        payment.setBillingType(data.billingType());
        payment.setMessage(data.message());
        payment.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    private PaymentGetResponseDto processCreditCardPayment(Donor donor, CreateDonationDTO data) {
        CreditCardRequestDto card = CreditCardRequestDto.builder()
                .holderName(data.creditCardDetails().holderName())
                .number(data.creditCardDetails().number())
                .expiryMonth(data.creditCardDetails().expiryMonth())
                .expiryYear(data.creditCardDetails().expiryYear())
                .ccv(data.creditCardDetails().ccv())
                .build();

        CreditCardHolderInfoRequestDto holderInfo = buildHolderInfo(donor);

        PaymentSaveWithCreditCardRequestDto request = PaymentSaveWithCreditCardRequestDto.builder()
                .customer(donor.getExternalId())
                .billingType(PaymentSaveWithCreditCardRequestBillingType.CREDIT_CARD)
                .value(data.amount().doubleValue())
                .dueDate(getTodayIsoDate())
                .description(data.message())
                .creditCard(card)
                .creditCardHolderInfo(holderInfo)
                .remoteIp("0.0.0.0")
                .authorizeOnly(false)
                .build();

        return asaasSdk.payment.createNewPaymentWithCreditCard(request);
    }

    private PaymentGetResponseDto processPixPayment(Donor donor, CreateDonationDTO data) {
        PaymentSaveRequestDto request = PaymentSaveRequestDto.builder()
                .customer(donor.getExternalId())
                .billingType(PaymentSaveRequestBillingType.PIX)
                .value(data.amount().doubleValue())
                .dueDate(getTodayIsoDate())
                .description(data.message())
                .build();

        return asaasSdk.payment.createNewPayment(request);
    }

    private void updatePixQrCodeLink(Payment payment, PaymentGetResponseDto response) {
        payment.setExternalReference(response.getId());

        payment.setPaymentUrl(response.getInvoiceUrl());

        if ("PIX".equalsIgnoreCase(payment.getBillingType())) {
            try {
                PaymentPixQrCodeResponseDto qrCode = asaasSdk.payment.getQrCodeForPixPayments(response.getId());

                payment.setPaymentUrl(qrCode.getPayload());

            } catch (Exception e) {
                System.out.println("Não foi possível buscar o Payload do Pix.");
            }
        }
    }

    private CreditCardHolderInfoRequestDto buildHolderInfo(Donor donor) {
        return CreditCardHolderInfoRequestDto.builder()
                .name(donor.getName())
                .email(donor.getEmail())
                .cpfCnpj(donor.getDocument())
                .postalCode("00000000")
                .addressNumber("0")
                .phone("11999999999")
                .build();
    }

    private String getTodayIsoDate() {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}