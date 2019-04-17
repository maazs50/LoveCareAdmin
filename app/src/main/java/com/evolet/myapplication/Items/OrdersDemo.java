package com.evolet.myapplication.Items;

import java.util.Date;

public class OrdersDemo {
    String order_id;
    Date time;
    String price;

    public OrdersDemo(String order_id, String price, Date time) {
        this.order_id = order_id;
        this.time = time;
        this.price = price;
    }
    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
