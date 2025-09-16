package com.ecommerce;

import java.util.UUID;

public class Customer {
     private final UUID id;
     private String name;
     private String email;
     private String phone;
     private Location address;
     private ShoppingCart cart;


     public UUID getId() {
         return id;
     }
     public String getName() {
         return name;
     }
     public void setName(String name) {
         this.name = name;
     }
     public String getEmail() {
         return email;
     }
     public void setEmail(String email) {
         this.email = email;
     }
     public String getPhone() {
         return phone;
     }
     public void setPhone(String phone) {
         this.phone = phone;
     }
     public Location getAddress() {
         return address;
     }
     public void setAddress(Location address) {
         this.address = address;
     }
     public ShoppingCart getCart() {
         return cart;
     }
     public void setCart(ShoppingCart cart) {
         this.cart = cart;
     }


     public Customer() {
         id = UUID.randomUUID();
         name = "";
         email = "";
         phone = "";
         address = new Location();
         cart = new ShoppingCart();
     }
     public Customer(UUID id, String name, String email, String phone, Location address,
                     ShoppingCart cart) {
          this.id = id;
          this.name = name;
          this.email = email;
          this.phone = phone;
          this.address = address;
          this.cart = cart;
     }


}
