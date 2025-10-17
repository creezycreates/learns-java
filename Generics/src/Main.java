import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GenericCatalog<LibraryItem<String>> catalog = new GenericCatalog<>();

        while (true) {
            System.out.println("\n===== 📖 LIBRARY CATALOG MENU =====");
            System.out.println("1. Add Library Item");
            System.out.println("2. Remove Library Item");
            System.out.println("3. View Catalog");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter item ID: ");
                    String id = scanner.nextLine();

                    LibraryItem<String> newItem = new LibraryItem<>(title, author, id);
                    catalog.addItem(newItem);
                    break;

                case "2":
                    System.out.print("Enter item ID to remove: ");
                    String removeId = scanner.nextLine();
                    catalog.removeItem(removeId);
                    break;

                case "3":
                    catalog.viewCatalog();
                    break;

                case "4":
                    System.out.println("👋 Exiting the catalog. Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }
}