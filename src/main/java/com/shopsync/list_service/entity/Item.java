package com.shopsync.list_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "list_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer quantity = 1;

    private Boolean purchased = false;

    /**
     * Povezava na tvoj osebni katalog produktov.
     * nullable = true, ker lahko uporabnik doda artikel, ki ga še nima v katalogu.
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    /**
     * Povezava na seznam, kateremu artikel pripada.
     */
    @ManyToOne
    @JoinColumn(name = "shopping_list_id")
    @JsonIgnore // Prepreči neskončno zanko v JSON-u
    private ShoppingList shoppingList;

    // Pomožna metoda za Boolean, če jo potrebuješ (Lombok generira isPurchased ali getPurchased)
    public Boolean getPurchased() {
        return purchased != null && purchased;
    }
}