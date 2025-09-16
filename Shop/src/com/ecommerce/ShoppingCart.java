package com.ecommerce;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<CartItem> items;


    public List<CartItem> getItems() {
        return items;
    }
    public void setItems(List<CartItem> items) {
        this.items = items;
    }


    public ShoppingCart() {
        items = new  ArrayList<CartItem>();
    }
    public ShoppingCart(List<CartItem> items) {
        this.items = items;
    }


    public void addToCart(Product product, int quantity) {
        // check if a product already exists in the cart
        int productIndex = getProductIndex(product);
         if(productIndex >= 0) {
             CartItem item = items.get(productIndex);
             item.setQuantity(item.getQuantity() + quantity);
         } else {
             CartItem item = new CartItem(product, quantity);
             items.add(item);
         }
    }
    public void removeFromCart(Product product, int quantity) {
        int productIndex = getProductIndex(product);
        if(productIndex >= 0) {
            CartItem item = items.get(productIndex);
            if(item.getQuantity() > quantity) {
                item.setQuantity(item.getQuantity() - quantity);
            } else {
                items.remove(productIndex);
            }
        }
    }
    public double getTotal() {
        double total = 0;
        if(items != null && !items.isEmpty()) {
            for(CartItem item : items) {
                total += item.getProduct().getPrice() * item.getQuantity();
            }
        }
        return total;
    }
    public List<CartItem> cloneItems() {
        ArrayList<CartItem> clonedItems = new ArrayList<CartItem>();

        if(items != null && !items.isEmpty()) {
            for(CartItem item : items) {
                clonedItems.add(new CartItem(item.getProduct(), item.getQuantity()));
            }
        }

        return clonedItems;
    }



    private int getProductIndex(Product product) {
        int index = -1;

        if(items != null && !items.isEmpty()) {
            for(int i=0; i < items.size(); i++) {
                if(items.get(i).getProduct().getId().equals(product.getId())) {
                    index = i;
                    break;
                }
            }
        }

        return index;
    }


}
