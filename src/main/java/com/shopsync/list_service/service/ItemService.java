package com.shopsync.list_service.service;

import com.shopsync.list_service.entity.Item;
import com.shopsync.list_service.entity.ShoppingList;
import com.shopsync.list_service.repository.ItemRepository;
import com.shopsync.list_service.repository.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor // Avtomatsko ustvari konstruktor za final polja
public class ItemService {

    private final ItemRepository itemRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final AuthorizationService authService;

    public List<Item> getAllItemsInList(Long listId, String auth0Id) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seznam ne obstaja."));

        authService.validateListAccess(list, auth0Id);
        return itemRepository.findByShoppingListId(listId);
    }

    public List<Item> getPendingItems(Long listId, String auth0Id) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seznam ne obstaja."));

        authService.validateListAccess(list, auth0Id);
        return itemRepository.findByShoppingListIdAndPurchasedFalse(listId);
    }

    public Item togglePurchased(Long itemId, String auth0Id) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artikel ni najden."));

        authService.validateListAccess(item.getShoppingList(), auth0Id);

        item.setPurchased(!item.getPurchased());
        return itemRepository.save(item);
    }

    public Item updateItem(Long itemId, Item updatedItem, String auth0Id) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artikel ni najden."));

        authService.validateListAccess(existingItem.getShoppingList(), auth0Id);

        // Posodobimo polja, če so podana v requestu
        if (updatedItem.getName() != null) {
            existingItem.setName(updatedItem.getName());
        }
        if (updatedItem.getQuantity() != null) {
            existingItem.setQuantity(updatedItem.getQuantity());
        }
        // Uporabimo wrapper Boolean, da preverimo null
        if (updatedItem.getPurchased() != null) {
            existingItem.setPurchased(updatedItem.getPurchased());
        }

        return itemRepository.save(existingItem);
    }

    @Transactional
    public void deleteItem(Long itemId, String auth0Id) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Izdelek ne obstaja"));

        authService.validateListAccess(item.getShoppingList(), auth0Id);

        // Odstranimo povezavo iz seznama pred brisanjem (dobra praksa pri @OneToMany)
        ShoppingList list = item.getShoppingList();
        if (list != null) {
            list.getItems().remove(item);
        }

        itemRepository.delete(item);
    }
}