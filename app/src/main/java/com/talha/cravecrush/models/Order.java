package com.talha.cravecrush.models;

import com.google.firebase.Timestamp;
import java.util.List;

public class Order {
    private String id;
    private String userId; // Optional
    private String guestName;
    private String guestEmail;
    private String address;
    private String mobile;
    private String note;
    private double totalPrice;
    private String status;
    private Timestamp createdAt;
    private List<OrderItem> items;

    public Order() {}

    // Constructor for logged-in users
    public Order(String id, String userId, String guestName, String guestEmail, String address,
                 String mobile, String note, double totalPrice, String status,
                 Timestamp createdAt, List<OrderItem> items) {
        this.id = id;
        this.userId = userId;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.address = address;
        this.mobile = mobile;
        this.note = note;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    // Constructor for guest users
    public Order(String id, String guestName, String guestEmail, String address,
                 String mobile, String note, double totalPrice, String status,
                 Timestamp createdAt, List<OrderItem> items) {
        this.id = id;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.address = address;
        this.mobile = mobile;
        this.note = note;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}