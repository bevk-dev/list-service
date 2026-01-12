package com.shopsync.list_service.controller;

import com.shopsync.list_service.entity.Item;
import com.shopsync.list_service.entity.ShoppingList;
import com.shopsync.list_service.service.ItemService;
import com.shopsync.list_service.service.ShoppingListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ShoppingList> createList(@RequestBody ShoppingList list, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(shoppingListService.createList(jwt.getSubject(), list));
    }

    @PostMapping("/{listId}/items")
    public ResponseEntity<Item> addItemToList(@PathVariable Long listId, @RequestBody Item item, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(shoppingListService.addItemToList(listId, item, jwt.getSubject()));
    }

    @GetMapping("/{listId}/items")
    public ResponseEntity<List<Item>> getAllItemsInList(@PathVariable Long listId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(itemService.getAllItemsInList(listId, jwt.getSubject()));
    }

    @GetMapping("/{listId}/pending")
    public ResponseEntity<List<Item>> getPendingItems(@PathVariable Long listId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(itemService.getPendingItems(listId, jwt.getSubject()));
    }

    @GetMapping
    public ResponseEntity<List<ShoppingList>> getMyLists(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(shoppingListService.getListsForUser(jwt.getSubject()));
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<String> deleteList(@PathVariable Long listId, @AuthenticationPrincipal Jwt jwt) {
        shoppingListService.deleteList(listId, jwt.getSubject());
        return ResponseEntity.ok("Seznam uspešno izbrisan.");
    }

    /**
     * V mikrostoritvah bo frontend najprej vprašal user-service za ID uporabnika
     * na podlagi emaila, nato pa poklical to točko s friendAuth0Id.
     */
    @PostMapping("/{listId}/share")
    public ResponseEntity<String> shareList(@PathVariable Long listId, @RequestBody Map<String, String> body, @AuthenticationPrincipal Jwt jwt) {
        String friendAuth0Id = body.get("friendAuth0Id");
        shoppingListService.shareList(listId, friendAuth0Id, jwt.getSubject());
        return ResponseEntity.ok("Seznam uspešno deljen.");
    }

    @PatchMapping("/{listId}/name")
    public ResponseEntity<ShoppingList> updateListName(@PathVariable Long listId, @RequestBody String newName, @AuthenticationPrincipal Jwt jwt) {
        // Očistimo narekovaje, če jih JSON pošlje kot navaden String
        String name = newName.replace("\"", "");
        return ResponseEntity.ok(shoppingListService.updateListName(listId, name, jwt.getSubject()));
    }
}