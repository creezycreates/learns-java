package com.ecommerce.orders;
import com.ecommerce.CartItem;
import com.ecommerce.Customer;
import com.ecommerce.ShoppingCart;

import java.time.LocalDateTime;
import java.util.*;

public class Order {
    private final UUID id ;
    private Customer customer;
    private List<CartItem> items;
    private double total;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    public UUID getId() {
        return id;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public Customer getCustomer() {
        return customer;
    }
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }
    public double getTotal() {
        return total;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setCustomer(Customer customer) {
            this.customer = customer;
            this.updatedAt = LocalDateTime.now();
    }



    public Order(){
        id = UUID.randomUUID();
        status =  OrderStatus.PENDING;
        customer = null;
        items = new ArrayList<>();
        total = 0.0;
        createdAt = LocalDateTime.now();
        updatedAt = null;

    }

    public Order(Customer customer, ShoppingCart cart) {
         this.customer = Objects.requireNonNull(customer, "customer required");
         this.items = cart.cloneItems();
         this.total = cart.getTotal();
         this.id = UUID.randomUUID();
         this.status = OrderStatus.PENDING;
         this.createdAt = LocalDateTime.now();
         this.updatedAt = createdAt;
         computeTotal();
    }



    public void updateStatus(OrderStatus newStatus) {
        if (newStatus == null) return;
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    public void clearItems() {
        items.clear();
        computeTotal();
    }
    public void computeTotal(){
        double total = 0.0;
        for(CartItem item : items){
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        this.total = total;
    }
    /**
     * Adds an item to the order. If the product already exists in order, increase its quantity.
     */
    public void addItem(CartItem newItem) {
        if (newItem == null || newItem.getProduct() == null || newItem.getQuantity() <= 0) return;
        int idx = findItemIndexByProductId(newItem.getProduct().getId());
        if (idx >= 0) {
            CartItem existing = items.get(idx);
            existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
        } else {
            items.add(newItem);
        }
        computeTotal();
    }

    /**
     * Removes quantity of a product from the order. If quantity <= 0 or >= existing quantity,
     * remove the item.
     */
    public void removeItemByProductId(java.util.UUID productId, int quantityToRemove) {
        int idx = findItemIndexByProductId(productId);
        if (idx < 0) return;
        CartItem item = items.get(idx);
        if (quantityToRemove >= item.getQuantity()) {
            items.remove(idx);
        } else {
            item.setQuantity(item.getQuantity() - quantityToRemove);
        }
        computeTotal();
    }

    public String generateOrderSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(id).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Created: ").append(createdAt).append("\n");
        sb.append("Updated: ").append(updatedAt).append("\n");
        if (customer != null) {
            sb.append("Customer: ").append(customer.getName()).append(" (").append(
                    customer.getEmail()).append(")\n");
        }
        sb.append("Items:\n");
        for (CartItem item : items) {
            if (item != null && item.getProduct() != null) {
                sb.append(" - ").append(item.getProduct().getName())
                        .append(" x").append(item.getQuantity())
                        .append(" @ ").append(item.getProduct().getPrice())
                        .append(" = ").append(item.getProduct().getPrice() * item.getQuantity())
                        .append("\n");
            }
        }
        sb.append("Order Total: ").append(total).append("\n");
        return sb.toString();
    }
    @Override
    public String toString() {
        String result = "Order: " + id + "\n";
        result += "Status: " + status + "\n";
        result += "Created: " + createdAt + "\n";
        result += "Updated: " + updatedAt + "\n";
        if (customer != null) {
            result += "Customer: " + customer.getName() + " (email: " + customer.getEmail() + ")\n";
        }
        result += "Items:\n";
        for (CartItem item : items) {
            if (item != null && item.getProduct() != null) {
                result = result + " - " + item.getProduct().getName() + " x" +
                        item.getQuantity() + " @ "
                        + item.getProduct().getPrice() + " = " +
                        item.getProduct().getPrice() * item.getQuantity() + "\n";
            }
        }
        return result;
    }




    private int findItemIndexByProductId(UUID productId) {
        if (productId == null) return -1;
        for (int i = 0; i < items.size(); i++) {
            CartItem it = items.get(i);
            if (it != null && it.getProduct() != null && productId.equals(it.getProduct().getId())) {
                return i;
            }
        }
        return -1;
    }


}
