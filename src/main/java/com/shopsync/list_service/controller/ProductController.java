package com.shopsync.list_service.controller;

import com.shopsync.list_service.entity.Product;
import com.shopsync.list_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor // Lombok poskrbi za konstruktor
public class ProductController {

    private final ProductService productService;

    // Pridobi vse izdelke uporabnika ali filtriraj po kategoriji
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String category,
            @AuthenticationPrincipal Jwt jwt) {

        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(productService.getProductsByCategory(category, jwt.getSubject()));
        }
        return ResponseEntity.ok(productService.getMyProducts(jwt.getSubject()));
    }

    // Pridobi podrobnosti posameznega izdelka
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long productId,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(productService.getProductById(productId, jwt.getSubject()));
    }

    // Dodaj nov izdelek v svoj osebni katalog
    @PostMapping
    public ResponseEntity<Product> addProduct(
            @RequestBody Product product,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(productService.addProduct(product, jwt.getSubject()));
    }

    // Posodobi podatke o izdelku (cena, kategorija, ime)
    @PatchMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody Product productDetails,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(productService.updateProduct(productId, productDetails, jwt.getSubject()));
    }

    // Izbriši izdelek iz kataloga
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal Jwt jwt) {

        productService.deleteProduct(productId, jwt.getSubject());
        return ResponseEntity.ok("Izdelek " + productId + " uspešno odstranjen.");
    }
}