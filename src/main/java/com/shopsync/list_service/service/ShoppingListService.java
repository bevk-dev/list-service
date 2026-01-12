package com.shopsync.list_service.service;

import com.shopsync.list_service.entity.Item;
import com.shopsync.list_service.entity.Product;
import com.shopsync.list_service.entity.ShoppingList;
import com.shopsync.list_service.repository.ItemRepository;
import com.shopsync.list_service.repository.ProductRepository;
import com.shopsync.list_service.repository.ShoppingListRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final AuthorizationService authorizationService;

    @Autowired
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ShoppingList createList(String auth0Id, ShoppingList list) {
        list.setOwnerId(auth0Id);
        return shoppingListRepository.save(list);
    }

    @Transactional
    public Item addItemToList(Long listId, Item itemRequest, String userAuth0Id) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("Seznam ni bil najden"));

        authorizationService.validateListAccess(list, userAuth0Id);

        Optional<Item> existingItemOnList = itemRepository
                .findByNameAndShoppingListId(itemRequest.getName(), listId);

        if(existingItemOnList.isPresent()) {
            Item itemToUpdate = existingItemOnList.get();
            itemToUpdate.setQuantity(itemRequest.getQuantity());
            return itemRepository.save(itemToUpdate);
        }

        String listOwnerId = list.getOwnerId();
        Optional<Product> existingProduct = productRepository
                .findByNameAndOwnerId(itemRequest.getName(), listOwnerId);

        if (existingProduct.isPresent()) {
            itemRequest.setProduct(existingProduct.get());
        } else {
            itemRequest.setProduct(null);
        }

        itemRequest.setPurchased(false);
        itemRequest.setShoppingList(list);
        return itemRepository.save(itemRequest);
    }

    public List<ShoppingList> getListsForUser(String auth0Id) {
        return shoppingListRepository.findAllForUser(auth0Id);
    }

    public void deleteList(Long listId, String auth0Id) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("Seznam ne obstaja"));

        authorizationService.validateListOwner(list, auth0Id);
        shoppingListRepository.delete(list);
    }

    public void shareList(Long listId, String friendAuth0Id, String ownerAuth0Id) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("Seznam ne obstaja"));

        authorizationService.validateListOwner(list, ownerAuth0Id);

        if (!list.getSharedWithIds().contains(friendAuth0Id)) {
            list.getSharedWithIds().add(friendAuth0Id);
            shoppingListRepository.save(list);
        }

        String eventData = String.format("LIST_SHARED|%s|%s|%d", ownerAuth0Id, friendAuth0Id, listId);
        kafkaTemplate.send("notification-events", eventData);
    }

    public ShoppingList updateListName(Long listId, String newName, String userId) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("Seznam ni bil najden."));

        // Preverimo, če ima uporabnik pravico do urejanja (je lastnik ali mu je deljeno)
        authorizationService.validateListAccess(list, userId);

        list.setName(newName);
        return shoppingListRepository.save(list);
    }
}