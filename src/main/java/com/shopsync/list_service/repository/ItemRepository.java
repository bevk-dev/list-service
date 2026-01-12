package com.shopsync.list_service.repository;

import com.shopsync.list_service.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Pridobi vse artikle za specifičen seznam
    List<Item> findByShoppingListId(Long shoppingListId);

    // Pridobi samo artikle, ki še niso kupljeni/označeni (purchased = false)
    List<Item> findByShoppingListIdAndPurchasedFalse(Long shoppingListId);

    // Preveri, če artikel z določenim imenom že obstaja na specifičnem seznamu
    // (Da ne dodaš dvakrat "Mleko" na isti seznam)
    Optional<Item> findByNameAndShoppingListId(String name, Long shoppingListId);

    // Pobriši vse artikle določenega seznama (uporabno pri brisanju celotnega seznama)
    void deleteByShoppingListId(Long shoppingListId);
}