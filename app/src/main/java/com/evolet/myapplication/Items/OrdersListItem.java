package com.evolet.myapplication.Items;

import java.util.ArrayList;

public class OrdersListItem {

    String orderID,userID,userName,userPhoneNumber,userAddress;
    ArrayList<OrderProducts> orderProductsArrayList;
    boolean orderDelivered;

    public OrdersListItem(String orderID, String userID, String userName, String userPhoneNumber, String userAddress, ArrayList<OrderProducts> orderProductsArrayList, boolean orderDelivered) {
        this.orderID = orderID;
        this.userID = userID;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userAddress = userAddress;
        this.orderProductsArrayList = orderProductsArrayList;
        this.orderDelivered = orderDelivered;
    }


    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public ArrayList<OrderProducts> getOrderProductsArrayList() {
        return orderProductsArrayList;
    }

    public void setOrderProductsArrayList(ArrayList<OrderProducts> orderProductsArrayList) {
        this.orderProductsArrayList = orderProductsArrayList;
    }

    public boolean isOrderDelivered() {
        return orderDelivered;
    }

    public void setOrderDelivered(boolean orderDelivered) {
        this.orderDelivered = orderDelivered;
    }

}
