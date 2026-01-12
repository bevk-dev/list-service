package com.shopsync.list_service.repository;

import com.shopsync.list_service.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    // Iskanje samo tistih seznamov, kjer je uporabnik lastnik
    List<ShoppingList> findByOwnerId(String ownerId);

    // Glavna metoda: Najde vse sezname, ki so ali moji ALI deljeni z mano
    @Query("SELECT s FROM ShoppingList s " +
            "LEFT JOIN s.sharedWithIds sw " +
            "WHERE s.ownerId = :auth0Id OR sw = :auth0Id")
    List<ShoppingList> findAllForUser(@Param("auth0Id") String auth0Id);
}