package com.talha.cravecrush.utils;

import com.talha.cravecrush.models.CartItem;
import com.talha.cravecrush.models.MenuItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private final List<CartItem> cartItems;
    private CartListener cartListener;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void setListener(CartListener listener) {
        this.cartListener = listener;
    }

    public void addToCart(MenuItem menuItem, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getMenuItem().getId().equals(menuItem.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                if (item.getQuantity() <= 0) {
                    cartItems.remove(item);
                }
                notifyCartChanged();
                return;
            }
        }
        if (quantity > 0) {
            cartItems.add(new CartItem(menuItem, quantity));
            notifyCartChanged();
        }
    }

    public void removeFromCart(MenuItem menuItem) {
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getMenuItem().getId().equals(menuItem.getId())) {
                item.setQuantity(item.getQuantity() - 1);
                if (item.getQuantity() <= 0) {
                    iterator.remove();
                }
                notifyCartChanged();
                return;
            }
        }
    }

    public int getQuantity(MenuItem menuItem) {
        for (CartItem item : cartItems) {
            if (item.getMenuItem().getId().equals(menuItem.getId())) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public int getTotalItemCount() {
        int totalCount = 0;
        for (CartItem item : cartItems) {
            totalCount += item.getQuantity();
        }
        return totalCount;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
        notifyCartChanged();
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getMenuItem().getPrice() * item.getQuantity();
        }
        return total;
    }

    private void notifyCartChanged() {
        if (cartListener != null) {
            cartListener.onCartChanged();
        }
    }

    public interface CartListener {
        void onCartChanged();
    }
}