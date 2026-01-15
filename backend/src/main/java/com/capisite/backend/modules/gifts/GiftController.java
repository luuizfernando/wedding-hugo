package com.capisite.backend.modules.gifts;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class GiftController {

    private final GiftService giftService;
    
    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @GetMapping
    public ResponseEntity<List<Gift>> getAllGifts() {
        List<Gift> gifts = giftService.getAllGifts();
        return ResponseEntity.ok(gifts);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Gift> getGiftById(@PathVariable Long id) {
        Gift gift = giftService.getGiftById(id);
        return ResponseEntity.ok(gift);
    }

    @PostMapping
    public ResponseEntity<Gift> createGift(@RequestBody Gift gift) {
        Gift createdGift = giftService.createGift(gift);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGift);
    }

}