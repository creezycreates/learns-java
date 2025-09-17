import com.ecommerce.*;
import com.ecommerce.helpers.*;
import com.ecommerce.orders.*;
import com.ecommerce.services.CustomerService;
import java.util.*;

/**
 * Interactive console demo for our e-commerce system.
 * It uses our existing domain classes in package com.ecommerce and com.ecommerce.orders.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Setup inventory
        List<Product> inventory = new ArrayList<>();
        inventory.add(new Product(UUID.randomUUID(), "Laptop", 1200,
                5, "High-end laptop", List.of("Electronics"),
                List.of(), List.of()));
        inventory.add(new Product(UUID.randomUUID(), "Phone", 800,
                10, "Latest smartphone", List.of("Electronics"),
                List.of(), List.of()));
        inventory.add(new Product(UUID.randomUUID(), "Headphones", 150,
                20, "Noise-cancelling", List.of("Audio"),
                List.of(), List.of()));

        // 2. Register customer
        Customer customer = CustomerInputHelper.populateCustomer(scanner);


        // 3. Menu loop
        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Browse products");
            System.out.println("2. Add to cart");
            System.out.println("3. Remove from cart");
            System.out.println("4. View cart");
            System.out.println("5. Place order");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: CustomerService.browseProducts(inventory);break;
                case 2 : CustomerService.addToCart(customer, inventory, scanner);break;
                case 3 : CustomerService.removeFromCart(customer, scanner);break;
                case 4 : CustomerService.viewCart(customer);break;
                case 5 : CustomerService.placeOrder(customer);break;
                case 0 : {
                    System.out.println("Goodbye!");
                    return;
                }
                default : System.out.println("Invalid choice.");
            }
        }
    }
}