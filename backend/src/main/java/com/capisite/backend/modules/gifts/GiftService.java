package com.capisite.backend.modules.gifts;

import java.util.List;
import java.util.Optional;

import com.capisite.backend.exceptions.ResourceNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.capisite.backend.modules.gifts.dto.UpdateGiftDTO;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GiftService {

    private final GiftRepository giftRepository;

    public GiftService(GiftRepository giftRepository) {
        this.giftRepository = giftRepository;
    }

    public List<Gift> getAllGifts() {
        return giftRepository.findAll();
    }

    public Gift getGiftById(Long id) {
        Optional<Gift> gift = giftRepository.findById(id);
        return gift.orElseThrow(() -> new ResourceNotFoundException(id));
    }

//    public Gift createGift(Gift gift) {
//        // Criar DTO de criação de Gift
//        return giftRepository.save(gift);
//    }
//
//    public Gift updateGift(Long id, UpdateGiftDTO data) {
//        Gift gift = getGiftById(id);
//        updateData(gift, data);
//        return giftRepository.save(gift);
//    }
//
//    public void deleteGift(Long id) {
//        if (!giftRepository.existsById(id)) throw new ResourceNotFoundException(id);
//        giftRepository.deleteById(id);
//    }
//
//    private void updateData(Gift updatedGift, UpdateGiftDTO data) {
//        if(data.name() != null) updatedGift.setName(data.name());
//        if(data.description() != null) updatedGift.setDescription(data.description());
//        if(data.price() != null) updatedGift.setPrice(data.price());
//        if(data.image() != null) updatedGift.setImage(data.image());
//    }

}