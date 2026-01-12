package com.shopsync.list_service.service;

import com.shopsync.list_service.entity.Product;
import com.shopsync.list_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AuthorizationService authService;

    public List<Product> getMyProducts(String auth0Id) {
        // Uporabimo predelano metodo iz Repositoryja
        return productRepository.findByOwnerId(auth0Id);
    }

    public List<Product> getProductsByCategory(String category, String auth0Id) {
        // Uporabimo predelano metodo iz Repositoryja
        return productRepository.findByCategoryAndOwnerId(category, auth0Id);
    }

    public Product addProduct(Product product, String auth0Id) {
        // V mikrostoritvah ne preverjamo userRepository-ja.
        // Če ima uporabnik veljaven JWT, mu zaupamo in shranimo njegov ID.
        product.setOwnerId(auth0Id);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id, String auth0Id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Izdelek ne obstaja"));

        authService.validateProductOwner(product, auth0Id);
        productRepository.delete(product);
    }

    public Product updateProduct(Long id, Product updatedProduct, String auth0Id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Izdelek z ID " + id + " ne obstaja."));

        authService.validateProductOwner(existingProduct, auth0Id);

        if (updatedProduct.getName() != null) {
            existingProduct.setName(updatedProduct.getName());
        }

        if (updatedProduct.getPrice() != null) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }

        if (updatedProduct.getCategory() != null) {
            existingProduct.setCategory(updatedProduct.getCategory());
        }

        return productRepository.save(existingProduct);
    }

    public Product getProductById(Long id, String auth0Id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Izdelek ni bil najden."));

        authService.validateProductOwner(product, auth0Id);
        return product;
    }
}