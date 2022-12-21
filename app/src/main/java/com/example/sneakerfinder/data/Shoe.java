package com.example.sneakerfinder.data;

public class Shoe {

    private String name, description;
    private int price, image_id;

    public Shoe(String name, String description, int price, int image_id) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_id = image_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getImage_id() {
        return image_id;
    }
}
