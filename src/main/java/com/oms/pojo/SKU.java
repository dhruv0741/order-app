package com.oms.pojo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sku")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SKU {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skuId;

    @Column(nullable = false, unique = true)
    private String skuName;

    @Column(nullable = false)
    private int availableQuantity;

    @Column(nullable = false)
    private int totalQuantity;
}
