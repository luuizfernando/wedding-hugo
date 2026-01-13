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
        String cleanedDocument = data.document().replaceAll("\\D", "");
        return donorRepository.findByDocument(cleanedDocument)
                .orElseGet(() -> donorRepository.save(
                        new Donor(data.name(), data.email(), cleanedDocument)
                    )
                );
    }

}