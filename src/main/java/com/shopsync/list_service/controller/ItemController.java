package com.shopsync.list_service.controller;

import com.shopsync.list_service.entity.Item;
import com.shopsync.list_service.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor // Nadomesti ročni konstruktor
public class ItemController {

    private final ItemService itemService;

    // Posodabljanje lastnosti artikla (npr. sprememba količine ali imena)
    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(
            @PathVariable Long itemId,
            @RequestBody Item itemUpdates,
            @AuthenticationPrincipal Jwt jwt) {

        Item updated = itemService.updateItem(itemId, itemUpdates, jwt.getSubject());
        return ResponseEntity.ok(updated);
    }

    // Hitro preklapljanje stanja "kupljeno" (Checkmark v aplikaciji)
    @PatchMapping("/{itemId}/purchase")
    public ResponseEntity<Item> toggleItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal Jwt jwt) {

        Item item = itemService.togglePurchased(itemId, jwt.getSubject());
        return ResponseEntity.ok(item);
    }

    // Brisanje posameznega artikla s seznama
    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deleteItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal Jwt jwt) {

        itemService.deleteItem(itemId, jwt.getSubject());
        return ResponseEntity.ok("Artikel " + itemId + " je bil uspešno odstranjen.");
    }
}