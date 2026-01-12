package com.shopsync.list_service.repository;

import com.shopsync.list_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Iskanje vseh produktov določenega uporabnika
    List<Product> findByOwnerId(String ownerId);

    // Iskanje po kategoriji znotraj uporabnikovega kataloga
    List<Product> findByCategoryAndOwnerId(String category, String ownerId);

    // Preverjanje, če produkt z določenim imenom že obstaja v uporabnikovem katalogu
    boolean existsByNameAndOwnerId(String name, String ownerId);

    // Iskanje specifičnega produkta po imenu za določenega uporabnika
    Optional<Product> findByNameAndOwnerId(String name, String ownerId);

    // Iskanje s ključno besedo (uporabno za iskalnik v aplikaciji)
    List<Product> findByOwnerIdAndNameContainingIgnoreCase(String ownerId, String name);
}