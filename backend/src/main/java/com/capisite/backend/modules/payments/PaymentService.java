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
        Donor donor = donorService.findOrCreateDonor(
                new CreateDonorDTO(data.name(), data.email(), data.document())
        );

        if (donor.getExternalId() == null) {
            try {
                CustomerSaveRequestDto customerSaveRequestDto = CustomerSaveRequestDto.builder()
                        .name(donor.getName())
                        .email(donor.getEmail())
                        .cpfCnpj(donor.getDocument())
                        .build();
                CustomerGetResponseDto response = asaasSdk.customer.createNewCustomer(customerSaveRequestDto);
                donor.setExternalId(response.getId());
            } catch (Exception e) {
                e.printStackTrace();

                System.out.println("⚠️ ERRO 400 NO ASAAS: Verifique se o CPF '" + donor.getDocument() + "' é válido!");

                throw new RuntimeException("Erro ao criar cliente no Asaas SDK. Verifique CPF/Email. Detalhe: " + e.getMessage());
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
            if ("CREDIT_CARD".equalsIgnoreCase(data.billingType())) {

                CreditCardRequestDto creditCardRequestDto = CreditCardRequestDto.builder()
                        .holderName(data.creditCardDetails().holderName())
                        .number(data.creditCardDetails().number())
                        .expiryMonth(data.creditCardDetails().expiryMonth())
                        .expiryYear(data.creditCardDetails().expiryYear())
                        .ccv(data.creditCardDetails().ccv())
                        .build();

                CreditCardHolderInfoRequestDto holderInfo = CreditCardHolderInfoRequestDto.builder()
                        .name(donor.getName())
                        .email(donor.getEmail())
                        .cpfCnpj(donor.getDocument())
                        .postalCode("00000000")
                        .addressNumber("0")
                        .phone("11999999999")
                        .build();

                PaymentSaveWithCreditCardRequestDto paymentSaveWithCreditCardRequestDto =
                        PaymentSaveWithCreditCardRequestDto.builder()
                                .customer(donor.getExternalId())
                                .billingType(PaymentSaveWithCreditCardRequestBillingType.CREDIT_CARD)
                                .value(data.amount().doubleValue())
                                .dueDate(formatDate(LocalDate.now()))
                                .description(data.message())
                                .creditCard(creditCardRequestDto)
                                .creditCardHolderInfo(holderInfo)
                                .remoteIp("0.0.0.0")
                                .build();

                PaymentGetResponseDto response = asaasSdk.payment.createNewPaymentWithCreditCard(paymentSaveWithCreditCardRequestDto);

                payment.setExternalReference(response.getId());
                payment.setPaymentUrl(response.getInvoiceUrl());
            }

            if ("PIX".equalsIgnoreCase(data.billingType())) {
                PaymentSaveRequestDto pixRequest = PaymentSaveRequestDto.builder()
                        .customer(donor.getExternalId())
                        .billingType(PaymentSaveRequestBillingType.PIX)
                        .value(data.amount().doubleValue())
                        .dueDate(formatDate(LocalDate.now()))
                        .description(data.message())
                        .build();

                PaymentGetResponseDto response = asaasSdk.payment.createNewPayment(pixRequest);

                payment.setExternalReference(response.getId());

                try {
                    PaymentPixQrCodeResponseDto pixQrCode = asaasSdk.payment.getQrCodeForPixPayments(response.getId());
                    payment.setPaymentUrl(pixQrCode.getPayload());
                } catch (RuntimeException e) {
                    System.out.println("Erro ao buscar QR Code Pix: " + e.getMessage());
                    payment.setPaymentUrl(response.getInvoiceUrl());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro Asaas: " + e.getMessage());
        }

        return paymentRepository.save(payment);
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

}