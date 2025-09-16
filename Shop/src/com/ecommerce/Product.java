package com.ecommerce;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Describes a product that can be purchased by a customer.
 */
public class Product {
    private final UUID id;
    private String name;
    private double price;
    private int quantity;
    private String description;
    private List<String> categories;
    private List<String> imagesUrls;
    private List<String> videosUrls;



    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<String> getCategories() {
        return categories;
    }
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    public List<String> getImagesUrls() {
        return imagesUrls;
    }
    public void setImagesUrls(List<String> imagesUrls) {
        this.imagesUrls = imagesUrls;
    }
    public List<String> getVideosUrls() {
        return videosUrls;
    }
    public void setVideosUrls(List<String> videosUrls) {
        this.videosUrls = videosUrls;
    }



    public Product() {
        id = UUID.randomUUID();
        name = "";
        price = 0.0;
        quantity = 0;
        description = "";
        categories = new ArrayList<String>();
        imagesUrls = new ArrayList<String>();
        videosUrls = new ArrayList<String>();
    }

    public Product(UUID id, String name, double price, int quantity, String description,
                   List<String> categories, List<String> imagesUrls, List<String> videosUrls) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.categories = categories;
        this.imagesUrls = imagesUrls;
        this.videosUrls = videosUrls;
    }



    @Override
    public String toString() {
        String result = "Product: " + name + "\n";
        result += "Price: " + price + "\n";
        result += "Quantity: " + quantity + "\n";
        result += "Description: " + description + "\n";
        result += "Categories: " + categories + "\n";
        result += "ImagesUrls: " + imagesUrls + "\n";
        result += "VideosUrls: " + videosUrls + "\n";
        return result;
    }


}
