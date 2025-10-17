import java.util.ArrayList;
import java.util.List;

// Generic Catalog class
public class GenericCatalog<T extends LibraryItem<?>> {
    private List<T> items;

    public GenericCatalog() {
        this.items = new ArrayList<>();
    }

    // Add a new library item
    public void addItem(T item) {
        items.add(item);
        System.out.println("✅ Item added successfully!");
    }

    // Remove a library item by ID
    public void removeItem(Object itemID) {
        boolean removed = items.removeIf(item -> item.getItemID().equals(itemID));
        if (removed)
            System.out.println("🗑️ Item removed successfully!");
        else
            System.out.println("⚠️ Error: Item with ID " + itemID + " not found.");
    }

    // Retrieve and display item details
    public void viewCatalog() {
        if (items.isEmpty()) {
            System.out.println("📭 Catalog is empty.");
        } else {
            System.out.println("\n📚 Current Library Catalog:");
            for (T item : items) {
                System.out.println(item);
            }
        }
    }
}