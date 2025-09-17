package com.ecommerce.helpers;

import com.ecommerce.Customer;
import com.ecommerce.Location;
import com.ecommerce.ShoppingCart;

import java.util.Scanner;

public class CustomerInputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    private CustomerInputHelper() {
        // Private constructor to prevent instantiation (utility class)
    }

    public static Customer populateCustomer(Scanner scanner) {
        System.out.println("Enter your details to continue:");

        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Phone Number: ");
        String phone = scanner.nextLine().trim();



        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);

        Location loc = new Location();
        System.out.println("Enter shipping address (press Enter to skip a field):");
        System.out.print("  Street: ");
        loc.setStreet(readLine());
        System.out.print("  City: ");
        loc.setCity(readLine());
        System.out.print("  State: ");
        loc.setState(readLine());
        System.out.print("  Postal Code: ");
        loc.setPostalCode(readLine());
        System.out.print("  Country: ");
        loc.setCountry(readLine());
        customer.setAddress(loc);

        // ensures the customer has an empty cart
        if (customer.getCart() == null) customer.setCart(new ShoppingCart());

        System.out.println("Thanks, " + customer.getName() + ". Your account is ready.");

        return customer;
    }



    private static String readLine() {
        String s = scanner.nextLine();
        return s == null ? "" : s.trim();
    }
}
