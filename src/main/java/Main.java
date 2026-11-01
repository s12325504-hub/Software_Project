
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import domain.*;
import observer.*;
import repository.*;
import service.*;
import strategy.*;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    // Repositories
    private static userRepository userRepo = new userRepository();
    private static RestaurantRepository restaurantRepo = new RestaurantRepository();
    private static OrderRepository orderRepo = new OrderRepository();
    private static DriverRepository driverRepo = new DriverRepository();
    private static MenuIRepository menuRepo = new MenuIRepository();

    // Services
    private static NotificationService notificationService =
            new ConsoleNotificationService();

    private static Auth authService = new Auth(userRepo);
    private static MenuService menuService = new MenuService(menuRepo);

    private static OrderService orderService =
            new OrderService(
                    orderRepo,
                    restaurantRepo,
                    notificationService
            );

    private static DriverService driverService =
            new DriverService(
                    driverRepo,
                    new NearestDriverStrategy()
            );

    private static PricingService pricingService =
            new PricingService(new NormalPricingStrategy());

    private static PaymentService paymentService =
            new PaymentService();

    private static user currentUser = null;

    public static void main(String[] args) {

        System.out.println("=================================================");
        System.out.println("    Smart Food Delivery Management System (Console)");
        System.out.println("=================================================");

        boolean exit = false;

        while (!exit) {

            printDynamicMenu();

            System.out.print("Select an option: ");
            String input = scanner.nextLine().trim();

            if (currentUser == null) {

                // Guest
                switch (input) {

                    case "1":
                        handleLogin();
                        break;

                    case "2":
                        handleViewRestaurantsAndMenu();
                        break;

                    case "0":
                        exit = true;
                        System.out.println(
                                "\nExited system successfully. Thank you!"
                        );
                        break;

                    default:
                        System.out.println(
                                "⚠️ Invalid option! Please log in first."
                        );
                }

            } else if (isAdminOrManager(currentUser)) {

                // Admin / Manager
                switch (input) {

                    case "1":
                        handleViewRestaurantsAndMenu();
                        break;

                    case "2":
                        handleAssignDriver();
                        break;

                    case "3":
                        handleUpdateOrderStatus();
                        break;

                    case "4":
                        handleCalculatePricing();
                        break;

                    case "5":
                        handleLogout();
                        break;

                    case "0":
                        exit = true;
                        System.out.println(
                                "\nExited system successfully. Thank you!"
                        );
                        break;

                    default:
                        System.out.println(
                                "⚠️ Invalid option for Admin/Manager!"
                        );
                }

            } else {

                // Customer
                switch (input) {

                    case "1":
                        handleViewRestaurantsAndMenu();
                        break;

                    case "2":
                        handlePlaceOrder();
                        break;

                    case "3":
                        handleViewOrderDetails();
                        break;

                    case "4":
                        handleProcessPayment();
                        break;

                    case "5":
                        handleCancelOrder();
                        break;

                    case "6":
                        handleLogout();
                        break;

                    case "0":
                        exit = true;
                        System.out.println(
                                "\nExited system successfully. Thank you!"
                        );
                        break;

                    default:
                        System.out.println(
                                "⚠️ Invalid option for Customer!"
                        );
                }
            }
        }

        scanner.close();
    }

    // ==========================================
    // Role Checking
    // ==========================================

    private static boolean isAdminOrManager(user u) {

        if (u == null || u.getRole() == null) {
            return false;
        }

        String role = u.getRole().toUpperCase();

        return role.contains("ADMIN")
                || role.contains("MANAGER");
    }

    // ==========================================
    // Sprint 4 FIX: Order Ownership
    // ==========================================

    private static boolean isOrderOwnedByCurrentUser(Order order) {

        return currentUser != null
                && order != null
                && order.getUserId() != null
                && order.getUserId().equals(
                        currentUser.getUsername()
                );
    }

    // ==========================================
    // Dynamic Menu
    // ==========================================

    private static void printDynamicMenu() {

        System.out.println("\n-------------------------------------------------");

        if (currentUser == null) {

            System.out.println("👤 Status: Guest");
            System.out.println("1. [Sprint 1] User Login");
            System.out.println(
                    "2. [Sprint 1] View Available Restaurants & Menu"
            );
            System.out.println("0. Exit Application");

        } else if (isAdminOrManager(currentUser)) {

            System.out.println(
                    "👑 Logged in as (Admin/Manager): "
                    + currentUser.getUsername()
                    + " | [Role: "
                    + currentUser.getRole()
                    + "]"
            );

            System.out.println("1. View Restaurants & Menu");
            System.out.println(
                    "2. [Sprint 3 & 5] Smart Driver Assignment"
            );
            System.out.println(
                    "3. [Sprint 4] Update Order Status Lifecycle"
            );
            System.out.println(
                    "4. [Sprint 4] Apply Dynamic Pricing Strategy"
            );
            System.out.println("5. Logout");
            System.out.println("0. Exit Application");

        } else {

            System.out.println(
                    "🛍️ Logged in as (Customer): "
                    + currentUser.getUsername()
                    + " | [Role: "
                    + currentUser.getRole()
                    + "]"
            );

            System.out.println("1. View Available Restaurants & Menu");
            System.out.println(
                    "2. [Sprint 2 & 5] Place Food Order"
            );
            System.out.println(
                    "3. [Sprint 4] View Order Details & Status"
            );
            System.out.println(
                    "4. [Sprint 4] Process Cash Payment"
            );
            System.out.println(
                    "5. [Sprint 2] Cancel Order"
            );
            System.out.println("6. Logout");
            System.out.println("0. Exit Application");
        }

        System.out.println("-------------------------------------------------");
    }

    // ==========================================
    // Sprint 1: Login
    // ==========================================

    private static void handleLogin() {

        System.out.println("\n--- [Sprint 1: Login] ---");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {

            currentUser = authService.login(username, password);

            System.out.println(
                    "🎉 Logged in successfully! Welcome back ("
                    + currentUser.getRole()
                    + ")."
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Login failed: " + e.getMessage()
            );
        }
    }

    private static void handleLogout() {

        if (currentUser != null) {

            System.out.println(
                    "\n👋 Logged out successfully from account: "
                    + currentUser.getUsername()
            );

            currentUser = null;
        }
    }

    // ==========================================
    // Sprint 1: Restaurants & Menu
    // ==========================================

    private static void handleViewRestaurantsAndMenu() {

        System.out.println(
                "\n--- [Sprint 1: Restaurant Catalog] ---"
        );

        List<Restaurant> openRestaurants =
                restaurantRepo.findByIsOpenTrue();

        System.out.println("Currently open restaurants:");

        for (Restaurant r : openRestaurants) {

            System.out.println(
                    " - ["
                    + r.getId()
                    + "] "
                    + r.getName()
                    + " | Zone: "
                    + r.getDeliveryZone()
                    + " | Min Order: $"
                    + r.getMinimumOrderValue()
            );
        }

        System.out.print(
                "\nEnter restaurant ID to view menu (e.g., 1): "
        );

        String restId = scanner.nextLine().trim();

        if (!restId.isEmpty()) {

            try {

                List<Menu> availableItems =
                        menuService.getRestaurantMenu(restId);

                if (availableItems.isEmpty()) {

                    System.out.println(
                            "⚠️ No menu items available for this restaurant."
                    );

                } else {

                    System.out.println("Available Menu Items:");

                    for (Menu item : availableItems) {

                        System.out.println(
                                "   * ["
                                + item.getId()
                                + "] "
                                + item.getName()
                                + " - $"
                                + item.getPrice()
                        );
                    }
                }

            } catch (Exception e) {

                System.out.println(
                        "❌ Error: " + e.getMessage()
                );
            }
        }
    }

    // ==========================================
    // Sprint 2 & 5: Place Order
    // ==========================================

    private static void handlePlaceOrder() {

        System.out.println(
                "\n--- [Sprint 2 & 5: Place Order] ---"
        );

        System.out.print(
                "Enter restaurant ID (Available: 1): "
        );
        String restId = scanner.nextLine().trim();

        System.out.print(
                "Enter delivery address (e.g., Nablus Center): "
        );
        String address = scanner.nextLine().trim();

        System.out.println("Select order type (Sprint 5):");
        System.out.println("1. Standard Order");
        System.out.println(
                "2. Express Order (Additional delivery fee)"
        );
        System.out.println(
                "3. Scheduled Order (Future time delivery)"
        );
        System.out.print("Option: ");

        String typeChoice = scanner.nextLine().trim();

        LocalDateTime scheduledTime = null;

        if ("3".equals(typeChoice)
                || "SCHEDULED".equalsIgnoreCase(typeChoice)) {

            System.out.print(
                    "Enter delivery delay in hours (e.g., 2): "
            );

            try {

                int hours = Integer.parseInt(
                        scanner.nextLine().trim()
                );

                if (hours > 0) {

                    scheduledTime =
                            LocalDateTime.now().plusHours(hours);

                } else {

                    scheduledTime =
                            LocalDateTime.now().plusHours(2);
                }

            } catch (NumberFormatException e) {

                System.out.println(
                        "⚠️ Invalid input, defaulting to 2 hours from now."
                );

                scheduledTime =
                        LocalDateTime.now().plusHours(2);
            }
        }

        List<Menu> availableItems =
                menuRepo.findByRestaurantIdAndIsAvailableTrue(
                        restId
                );

        if (availableItems.isEmpty()) {

            System.out.println(
                    "❌ No menu items available at this restaurant!"
            );
            return;
        }

        System.out.println("Available Menu Items:");

        for (Menu item : availableItems) {

            System.out.println(
                    "   * ["
                    + item.getId()
                    + "] "
                    + item.getName()
                    + " - $"
                    + item.getPrice()
            );
        }

        System.out.print(
                "Enter item ID (e.g., m1 or m3): "
        );
        String selectedItemId = scanner.nextLine().trim();

        System.out.print("Enter quantity: ");

        int qty;

        try {

            qty = Integer.parseInt(
                    scanner.nextLine().trim()
            );

        } catch (NumberFormatException e) {

            System.out.println(
                    "❌ Order rejected: Quantity must be a valid integer!"
            );
            return;
        }

        Menu selectedMenu = null;

        for (Menu item : availableItems) {

            if (item.getId().equalsIgnoreCase(
                    selectedItemId
            )) {

                selectedMenu = item;
                break;
            }
        }

        if (selectedMenu == null) {

            System.out.println(
                    "❌ Item not found or unavailable!"
            );
            return;
        }

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(selectedMenu, qty));

        try {

            String activeUserId =
                    currentUser.getUsername();

            Order order = orderService.placeOrder(
                    activeUserId,
                    restId,
                    address,
                    items,
                    typeChoice,
                    scheduledTime
            );

            System.out.println(
                    "✅ Order created successfully for customer: "
                    + activeUserId
            );

            System.out.println(
                    "   Order ID       : " + order.getId()
            );

            System.out.println(
                    "   Current Status : " + order.getStatus()
            );

            System.out.println(
                    "   Grand Total    : $" + order.getFinalPrice()
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "❌ Order rejected: " + e.getMessage()
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Order creation failed: " + e.getMessage()
            );
        }
    }

    // ==========================================
    // Sprint 2: Cancel Order (OWNERSHIP FIX)
    // ==========================================

    private static void handleCancelOrder() {

        System.out.println(
                "\n--- [Sprint 2: Cancel Order] ---"
        );

        System.out.print("Enter order ID to cancel: ");
        String orderId = scanner.nextLine().trim();

        try {

            Order order = orderRepo.findById(orderId);

            if (!isOrderOwnedByCurrentUser(order)) {

                System.out.println(
                        "❌ Order not found or access denied!"
                );
                return;
            }

            if (order.canBeCancelled()) {

                order.setStatus("CANCELLED");

                System.out.println(
                        "✅ Order cancelled successfully and status changed to CANCELLED."
                );

            } else {

                System.out.println(
                        "❌ Order cannot be cancelled in its current state."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "❌ Order cancellation failed: "
                    + e.getMessage()
            );
        }
    }

    // ==========================================
    // Sprint 3 & 5: Driver Assignment
    // ==========================================

    private static void handleAssignDriver() {

        System.out.println(
                "\n--- [Sprint 3 & 5: Smart Driver Assignment] ---"
        );

        System.out.print(
                "Enter order ID for driver assignment: "
        );
        String orderId = scanner.nextLine().trim();

        Order order = orderRepo.findById(orderId);

        if (order == null) {

            System.out.println("❌ Order ID not found!");
            return;
        }

        System.out.println(
                "Select driver assignment strategy (Strategy Pattern):"
        );
        System.out.println("1. Nearest Driver");
        System.out.println("2. Fastest Driver");
        System.out.print("Option: ");

        String stratChoice = scanner.nextLine().trim();

        if ("2".equals(stratChoice)) {

            driverService.setSelectionStrategy(
                    new FastestDriverStrategy()
            );

        } else {

            driverService.setSelectionStrategy(
                    new NearestDriverStrategy()
            );
        }

        try {

            int orderSize = 0;

            if (order.getItems() != null) {

                for (OrderItem item : order.getItems()) {

                    orderSize += item.getQuantity();
                }
            }

            double deliveryDistance = 2.5;

            if (order.getDeliveryFee() != null
                    && order.getDeliveryFee().doubleValue() > 0) {

                double baseFee =
                        order.getDeliveryFee().doubleValue();

                if (order instanceof ExpressOrder) {

                    baseFee -= 5.0;
                }

                deliveryDistance = baseFee / 2.0;
            }

            Driver assignedDriver =
                    driverService.assignDriver(
                            order,
                            deliveryDistance,
                            orderSize
                    );

            System.out.println(
                    "================================================="
            );
            System.out.println(
                    "✅ Driver assigned successfully!"
            );
            System.out.println(
                    "   Driver Name  : "
                    + assignedDriver.getName()
            );
            System.out.println(
                    "   Driver Type  : "
                    + assignedDriver.getDriverType()
            );
            System.out.println(
                    "   Driver Status: "
                    + assignedDriver.getStatus()
            );
            System.out.println(
                    "================================================="
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Driver assignment failed: "
                    + e.getMessage()
            );
        }
    }

    // ==========================================
    // Sprint 4: Update Order Status
    // ==========================================

    private static void handleUpdateOrderStatus() {

        System.out.println(
                "\n--- [Sprint 4: Order Status Transitions] ---"
        );

        System.out.print("Enter order ID: ");
        String orderId = scanner.nextLine().trim();

        System.out.println("Select new order status:");
        System.out.println("1. CONFIRMED");
        System.out.println("2. PREPARING");
        System.out.println("3. READY");
        System.out.println("4. OUT_FOR_DELIVERY");
        System.out.println("5. DELIVERED");
        System.out.print("Option: ");

        int choice;

        try {

            choice = Integer.parseInt(
                    scanner.nextLine().trim()
            );

        } catch (NumberFormatException e) {

            System.out.println("❌ Invalid option!");
            return;
        }

        String[] statuses = {
                "CONFIRMED",
                "PREPARING",
                "READY",
                "OUT_FOR_DELIVERY",
                "DELIVERED"
        };

        if (choice < 1 || choice > 5) {

            System.out.println(
                    "❌ Invalid status option!"
            );
            return;
        }

        try {

            orderService.updateOrderStatus(
                    orderId,
                    statuses[choice - 1]
            );

            System.out.println(
                    "✅ Order status updated to: "
                    + statuses[choice - 1]
            );

            System.out.println(
                    "📢 Notification process triggered. Check logs for email delivery errors."
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Status transition not allowed: "
                    + e.getMessage()
            );
        }
    }

    // ==========================================
    // Sprint 4: Dynamic Pricing
    // ==========================================

    private static void handleCalculatePricing() {

        System.out.println(
                "\n--- [Sprint 4: Dynamic Pricing & Apply Strategy] ---"
        );

        System.out.print(
                "Enter order ID to apply pricing strategy: "
        );

        String orderId = scanner.nextLine().trim();

        Order order = orderRepo.findById(orderId);

        if (order == null) {

            System.out.println("❌ Order not found!");
            return;
        }

        System.out.println(
                "\nSelect pricing strategy (Strategy Pattern):"
        );
        System.out.println("1. Normal Pricing");
        System.out.println(
                "2. Peak-Hour Pricing (+15% surcharge)"
        );
        System.out.println(
                "3. Express Delivery Pricing (+$5.00 surcharge)"
        );
        System.out.println(
                "4. Promotional Pricing (10% discount)"
        );
        System.out.print("Option: ");

        String pChoice = scanner.nextLine().trim();

        PricingStrategy strategy;

        switch (pChoice) {

            case "2":
                strategy = new PeakHourPricingStrategy(
                        new BigDecimal("15.00")
                );
                break;

            case "3":
                strategy = new ExpressDeliveryPricingStrategy(
                        new BigDecimal("5.00")
                );
                break;

            case "4":
                strategy = new PromotionalPricingStrategy(
                        new BigDecimal("10.00")
                );
                break;

            default:
                strategy = new NormalPricingStrategy();
                break;
        }

        pricingService.setPricingStrategy(strategy);

        try {

            BigDecimal finalPrice =
                    pricingService.applyPricing(order);

            System.out.println(
                    "-------------------------------------------------"
            );

            System.out.println(
                    "💰 Updated grand total after strategy application: $"
                    + finalPrice
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Pricing calculation failed: "
                    + e.getMessage()
            );
        }
    }

    // ==========================================
    // Sprint 4: View Order Details (OWNERSHIP FIX)
    // ==========================================

    private static void handleViewOrderDetails() {

        System.out.println(
                "\n--- [Sprint 4: View Order Details] ---"
        );

        System.out.print(
                "Enter order ID to inspect details: "
        );

        String orderId = scanner.nextLine().trim();

        Order order = orderRepo.findById(orderId);

        if (!isOrderOwnedByCurrentUser(order)) {

            System.out.println(
                    "❌ Order not found or access denied!"
            );
            return;
        }

        System.out.println(
                "================================================="
        );

        System.out.println("📋 Order Details:");

        System.out.println(
                " - Order ID      : " + order.getId()
        );

        System.out.println(
                " - Customer ID   : " + order.getUserId()
        );

        System.out.println(
                " - Current Status: " + order.getStatus()
        );

        System.out.println(
                " - Payment Status: " + order.getPaymentStatus()
        );

        System.out.println(
                " - Grand Total   : $" + order.getFinalPrice()
        );

        System.out.println(
                "================================================="
        );
    }

    // ==========================================
    // Sprint 4: Cash Payment (OWNERSHIP + INPUT FIX)
    // ==========================================

    private static void handleProcessPayment() {

        System.out.println(
                "\n--- [Sprint 4: Process Cash Payment] ---"
        );

        System.out.print(
                "Enter order ID to process payment: "
        );

        String orderId = scanner.nextLine().trim();

        Order order = orderRepo.findById(orderId);

        if (!isOrderOwnedByCurrentUser(order)) {

            System.out.println(
                    "❌ Order not found or access denied!"
            );
            return;
        }

        try {

            System.out.print("Enter cash amount paid: ");

            BigDecimal amountPaid = new BigDecimal(
                    scanner.nextLine().trim()
            );

            String resultMessage =
                    paymentService.processCashPayment(
                            order,
                            amountPaid
                    );

            System.out.println(
                    "✅ " + resultMessage
            );

            System.out.println(
                    "   Current payment status: "
                    + order.getPaymentStatus()
            );

        } catch (NumberFormatException e) {

            System.out.println(
                    "❌ Invalid amount! Please enter a valid number."
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Payment process rejected: "
                    + e.getMessage()
            );
        }
    }
}
