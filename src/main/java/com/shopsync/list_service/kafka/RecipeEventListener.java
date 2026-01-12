package com.shopsync.list_service.kafka;

import com.shopsync.list_service.entity.ShoppingList;
import com.shopsync.list_service.entity.Item;
import com.shopsync.list_service.service.ShoppingListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeEventListener {

    private final ShoppingListService shoppingListService;

    @KafkaListener(topics = "recipe-events", groupId = "list-group")
    public void handleRecipeEvent(String message) {
        log.info("Prejeto sporočilo iz Kafke: {}", message);

        try {
            String[] parts = message.split("~");

            if (parts.length < 4) {
                log.warn("Sporočilo nima dovolj delov: {}", message);
                return;
            }

            String type = parts[0];

            if ("CREATE_LIST_FROM_RECIPE".equals(type)) {
                String auth0Id = parts[1].trim();   // To mora biti "auth0|..."
                String recipeTitle = parts[2].trim(); // To mora biti naslov recepta
                String ingredientsRaw = parts[3];    // To so sestavine ločene s podpičjem

                log.info("Procesiram: Naslov='{}', OwnerID='{}'", recipeTitle, auth0Id);

                // 1. Pripravimo objekt ShoppingList
                ShoppingList newList = new ShoppingList();
                newList.setName(recipeTitle); // Nastavimo ime seznama na naslov recepta
                newList.setSharedWithIds(new ArrayList<>());
                newList.setItems(new ArrayList<>());

                // 2. Ustvarimo seznam preko servisa
                // POZOR: Prepričaj se, da shoppingListService.createList(String ownerId, ShoppingList list)
                // znotraj sebe naredi: list.setOwnerId(ownerId);
                ShoppingList savedList = shoppingListService.createList(auth0Id, newList);

                // 3. Dodamo sestavine
                String[] ingredients = ingredientsRaw.split(";");
                for (String ingName : ingredients) {
                    String cleanName = ingName.trim();
                    if (!cleanName.isEmpty()) {
                        Item item = new Item();
                        item.setName(cleanName);
                        item.setQuantity(1);
                        item.setPurchased(false);

                        // Kličemo addItemToList(Long listId, Item item, String ownerId)
                        shoppingListService.addItemToList(savedList.getId(), item, auth0Id);
                    }
                }
                log.info("Seznam '{}' uspešno kreiran za {}", recipeTitle, auth0Id);
            }
        } catch (Exception e) {
            log.error("Napaka pri procesiranju: {}", e.getMessage(), e);
        }
    }
}