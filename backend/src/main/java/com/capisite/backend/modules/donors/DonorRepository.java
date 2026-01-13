package com.capisite.backend.modules.donors;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorRepository extends JpaRepository<Donor, String> {
    
    Optional<Donor> findByDocument(String document);

}