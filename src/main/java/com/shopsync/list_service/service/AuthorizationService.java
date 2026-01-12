package com.shopsync.list_service.service;

import com.shopsync.list_service.entity.Product;
import com.shopsync.list_service.entity.ShoppingList;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthorizationService {

    /**
     * Preveri, če ima uporabnik pravico do branja/urejanja seznama.
     * Uporabnik mora biti ali lastnik ali na seznamu deljenih uporabnikov.
     */
    public void validateListAccess(ShoppingList list, String auth0Id) {
        // 1. Preveri če je uporabnik LASTNIK (direktna primerjava Stringov)
        boolean isOwner = list.getOwnerId().equals(auth0Id);

        // 2. Preveri če je uporabnik v seznamu deljenih ID-jev
        boolean isShared = list.getSharedWithIds().contains(auth0Id);

        if (!isOwner && !isShared) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nimate pravic za dostop do tega seznama.");
        }
    }

    /**
     * Preveri, če je uporabnik lastnik seznama (npr. za brisanje seznama ali deljenje).
     */
    public void validateListOwner(ShoppingList list, String auth0Id) {
        if (!list.getOwnerId().equals(auth0Id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Samo lastnik lahko izvede to dejanje.");
        }
    }

    /**
     * Preveri, če je uporabnik lastnik izdelka v svojem katalogu.
     */
    public void validateProductOwner(Product product, String auth0Id) {
        if (!product.getOwnerId().equals(auth0Id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nimate pravice za urejanje tega izdelka.");
        }
    }
}