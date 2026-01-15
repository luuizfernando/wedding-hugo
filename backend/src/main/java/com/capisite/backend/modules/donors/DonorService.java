package com.capisite.backend.modules.donors;

import org.springframework.stereotype.Service;

import com.capisite.backend.modules.donors.dto.CreateDonorDTO;

import jakarta.transaction.Transactional;

@Service
public class DonorService {

    private final DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    @Transactional
    public Donor findOrCreateDonor(CreateDonorDTO data) {
        String cleanedDocument = cleanDocument(data.document());
        return donorRepository.findByDocument(cleanedDocument)
                .orElseGet(() -> createNewDonor(data, cleanedDocument));
    }

    private String cleanDocument(String document) {
        return document.replaceAll("\\D", "");
    }

    private Donor createNewDonor(CreateDonorDTO data, String cleanedDocument) {
        Donor newDonor = new Donor(
                data.name(),
                data.email(),
                cleanedDocument
        );

        return donorRepository.save(newDonor);
    }

}