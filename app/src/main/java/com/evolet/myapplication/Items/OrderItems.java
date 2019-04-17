package com.evolet.myapplication.Items;

public class OrderItems {
    String itemName;
    String quantity;

    public String getItemName() {
        return itemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public OrderItems(String itemName, String quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }
}