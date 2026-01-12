package com.shopsync.list_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shopping_lists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * V mikrostoritvah ne shranimo celotnega objekta User,
     * ampak samo njegov Auth0 ID (String).
     */
    @Column(nullable = false)
    private String ownerId;

    /**
     * Seznam uporabnikov, s katerimi je seznam deljen.
     * Shranimo le njihove Auth0 ID-je.
     */
    @ElementCollection
    @CollectionTable(name = "shopping_list_shared_users", joinColumns = @JoinColumn(name = "list_id"))
    @Column(name = "user_id")
    private List<String> sharedWithIds = new ArrayList<>();

    /**
     * Item ostane v istem servisu, zato je povezava @OneToMany pravilna.
     */
    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();
}