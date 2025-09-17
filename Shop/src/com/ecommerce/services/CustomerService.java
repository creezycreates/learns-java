package com.ecommerce.services;

import com.ecommerce.*;
import com.ecommerce.orders.*;
import java.util.List;
import java.util.Scanner;

public class CustomerService {
    public static void browseProducts(List<Product> inventory) {
        System.out.println("\n--- Available Products ---");
        for (int i = 0; i < inventory.size(); i++) {
            System.out.printf("%d) %s - $%.2f (Qty: %d)\n", i + 1,
                    inventory.get(i).getName(),
                    inventory.get(i).getPrice(),
                    inventory.get(i).getQuantity());
        }
    }

    public static void addToCart(Customer customer, List<Product> inventory, Scanner scanner) {
        browseProducts(inventory);
        System.out.print("Select product number: ");
        int choice = scanner.nextInt() - 1;
        if (choice < 0 || choice >= inventory.size()) {
            System.out.println("Invalid product.");
            return;
        }
        System.out.print("Enter quantity: ");
        int qty = scanner.nextInt();
        Product product = inventory.get(choice);
        customer.getCart().addToCart(product, qty);
        System.out.println("Added to cart.");
    }

    public static void removeFromCart(Customer customer, Scanner scanner) {
        viewCart(customer);
        System.out.print("Enter product index to remove: ");
        int idx = scanner.nextInt() - 1;
        if (idx < 0 || idx >= customer.getCart().getItems().size()) {
            System.out.println("Invalid index.");
            return;
        }
        CartItem item = customer.getCart().getItems().get(idx);
        System.out.print("Enter quantity to remove: ");
        int qty = scanner.nextInt();
        customer.getCart().removeFromCart(item.getProduct(), qty);
        System.out.println("Item updated.");
    }

    public static void viewCart(Customer customer) {
        System.out.println("\n--- Your Cart ---");
        List<CartItem> items = customer.getCart().getItems();
        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
        } else {
            for (int i = 0; i < items.size(); i++) {
                CartItem item = items.get(i);
                System.out.printf("%d) %s x%d ($%.2f)\n", i + 1,
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getProduct().getPrice() * item.getQuantity());
            }
            System.out.printf("Cart Total: $%.2f\n", customer.getCart().getTotal());
        }
    }

    public static Order placeOrder(Customer customer) {
        if (customer.getCart().getItems().isEmpty()) {
            System.out.println("Cart is empty. Cannot place order.");
            return null;
        }
        Order order = new Order(customer, customer.getCart());
        order.updateStatus(OrderStatus.CONFIRMED);
        System.out.println("\n--- Order Placed Successfully ---");
        System.out.println(order.generateOrderSummary());

        // Clear the shopping cart after placing order
        customer.setCart(new ShoppingCart());

        return order;
    }
}
