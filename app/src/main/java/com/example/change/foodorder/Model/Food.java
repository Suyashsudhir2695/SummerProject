package com.example.change.foodorder.Model;

public class Food {
    private String name;
    private String image;
    private String description;
    private String discount;
    private String menuId;
    private String price;

    public Food() {
    }

    public Food(String name, String image, String description, String discount, String menuId, String price) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.discount = discount;
        this.menuId = menuId;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}