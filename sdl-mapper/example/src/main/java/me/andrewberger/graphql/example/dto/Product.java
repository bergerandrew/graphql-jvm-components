/*
package me.andrewberger.graphql.example.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.jetbrains.annotations.NotNull;

import me.andrewberger.graphql.Node;

@Entity
public class Product implements Node {

    @GeneratedValue(strategy= GenerationType.AUTO)
    @Id
    private Long productId;

    private String name;

    private String manufacturer;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @NotNull
    @Override
    public String getId() {
        return name;
    }
}
*/
