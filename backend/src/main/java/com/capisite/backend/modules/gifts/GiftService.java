package com.capisite.backend.modules.gifts;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capisite.backend.modules.gifts.dto.UpdateGiftDTO;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GiftService {

    private final GiftRepository giftRepository;

    public GiftService(GiftRepository giftRepository) {
        this.giftRepository = giftRepository;
    }

    public List<Gift> getAllProducts() {
        return giftRepository.findAll();
    }

    public Gift getProductById(Long id) {
        return giftRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + id + " não foi encontrado"));
    }

    public Gift createProduct(Gift product) {
        return giftRepository.save(product);
    }

    public Gift updateProduct(Long id, UpdateGiftDTO dto) {
        Gift product = giftRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + id + " não foi encontrado"));

        if (dto.name() != null) {
            product.setName(dto.name());
        }
        if (dto.description() != null) {
            product.setDescription(dto.description());
        }
        if (dto.price() != null) {
            product.setPrice(dto.price());
        }
        if (dto.image() != null) {
            product.setImage(dto.image());
        }

        return giftRepository.save(product);
    }

    public void deleteProduct(Long id) {
        giftRepository.deleteById(id);
    }

}