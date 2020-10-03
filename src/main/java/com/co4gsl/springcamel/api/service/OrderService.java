package com.co4gsl.springcamel.api.service;

import com.co4gsl.springcamel.api.dto.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Comparator.comparing;

@Service
public class OrderService {

    List<Order> orders = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void initOrders() {
        orders.add(new Order(11, "Apple", 6));
        orders.add(new Order(12, "Pear", 50));
        orders.add(new Order(13, "Mango", 60));
    }

    public Order addOrder(Order order) {
        int nextOrderId = orders.stream().max(comparing(Order::getId)).get().getId() + 1;
        order.setId(nextOrderId);
        orders.add(order);
        return order;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
