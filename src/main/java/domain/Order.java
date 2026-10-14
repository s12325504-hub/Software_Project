package domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import observer.OrderObserver;

public class Order {

    private String id;
    private String customerId;
    private String restaurantId;
    private List<OrderItem> items;
    private BigDecimal totalPrice;
    private BigDecimal deliveryFee;
    private BigDecimal finalPrice;
    private String status;
    private String paymentStatus;
    private Driver assignedDriver;
    private String lastEvent;

    private List<OrderObserver> observers = new ArrayList<>();

    public Order(String id, String customerId, String restaurantId,
                 List<OrderItem> items, BigDecimal totalPrice) {

        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.deliveryFee = BigDecimal.ZERO;
        this.finalPrice = totalPrice;
        this.status = "PENDING";
        this.paymentStatus = "UNPAID";
        this.assignedDriver = null;
        this.lastEvent = "";
    }

    public String getId() {
        return id;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
        this.finalPrice = totalPrice.add(deliveryFee);
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastEvent = "STATUS_CHANGED";
        notifyObservers();
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Driver getAssignedDriver() {
        return assignedDriver;
    }

    public void setAssignedDriver(Driver assignedDriver) {
        this.assignedDriver = assignedDriver;
        this.lastEvent = "DRIVER_ASSIGNED";
        notifyObservers();
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (OrderObserver observer : observers) {
            observer.update(this);
        }
    }

    public boolean canBeCancelled() {
        return "PENDING".equalsIgnoreCase(this.status);
    }
}