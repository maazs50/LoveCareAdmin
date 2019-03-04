package com.evolet.myapplication.Items;

public class OrderProducts {

    String prodID, prodName;
    int prodImage, quantity;

    public OrderProducts(String prodID, String prodName, int prodImage, int quantity) {
        this.prodID = prodID;
        this.prodName = prodName;
        this.prodImage = prodImage;
        this.quantity = quantity;
    }

    public String getProdID() {
        return prodID;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public int getProdImage() {
        return prodImage;
    }

    public void setProdImage(int prodImage) {
        this.prodImage = prodImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
