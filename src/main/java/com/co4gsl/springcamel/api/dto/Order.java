package com.co4gsl.springcamel.api.dto;

public class Order {

    private int id;
    private String name;
    private int qty;

    public Order(int id, String name, int qty) {
        this.id = id;
        this.name = name;
        this.qty = qty;
    }

    public Order() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", qty=" + qty +
                '}';
    }
}
