import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

// استيراد كافة الحزم والملفات بناءً على مشروعك
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
    private static NotificationService notificationService = new ConsoleNotificationService();
    private static Auth authService = new Auth(userRepo);
    private static MenuService menuService = new MenuService(menuRepo);
    private static OrderService orderService = new OrderService(orderRepo, restaurantRepo, notificationService);
    
    // DriverService مع NearestDriverStrategy افتراضياً
    private static DriverService driverService = new DriverService(driverRepo, new NearestDriverStrategy());
    
    // PricingService مع NormalPricingStrategy افتراضياً
    private static PricingService pricingService = new PricingService(new NormalPricingStrategy());
    
    // PaymentService
    private static PaymentService paymentService = new PaymentService();

    // حفظ كائن المستخدم الحالي (null للزائر)
    private static user currentUser = null;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("    Smart Food Delivery Management System (Console)  ");
        System.out.println("=================================================");

        boolean exit = false;
        while (!exit) {
            printDynamicMenu();
            System.out.print("اختر رقم الخيار: ");
            String input = scanner.nextLine().trim();

            if (currentUser == null) {
                // ==========================================
                // 1. خيارات الزائر (Guest Options)
                // ==========================================
                switch (input) {
                    case "1": 
                        handleLogin(); 
                        break;
                    case "2": 
                        handleViewRestaurantsAndMenu(); 
                        break;
                    case "0":
                        exit = true;
                        System.out.println("\nتم الخروج من النظام بنجاح. شكراً لك!");
                        break;
                    default: 
                        System.out.println("⚠️ خيار غير صحيح! يرجى تسجيل الدخول أولاً للوصول لخدمات النظام.");
                }
            } else if (isAdminOrManager(currentUser)) {
                // ==========================================
                // 2. خيارات المدير / المشرف (Admin & Manager Options)
                // ==========================================
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
                        System.out.println("\nتم الخروج من النظام بنجاح. شكراً لك!");
                        break;
                    default: 
                        System.out.println("⚠️ خيار غير صحيح لمدير النظام!");
                }
            } else {
                // ==========================================
                // 3. خيارات الزبون (Customer Role Options)
                // ==========================================
                switch (input) {
                    case "1": 
                        handleViewRestaurantsAndMenu(); 
                        break;
                    case "2": 
                        handlePlaceOrder(); 
                        break;
                    case "3": 
                        handleViewOrderDetails(); // الخيار المضاف لاستعلام حالة الطلب
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
                        System.out.println("\nتم الخروج من النظام بنجاح. شكراً لك!");
                        break;
                    default: 
                        System.out.println("⚠️ خيار غير صحيح للزبون!");
                }
            }
        }
        scanner.close();
    }

    // دالة فحص هل يمتلك المستخدم صلاحيات إدارية (Admin أو Manager)
    private static boolean isAdminOrManager(user u) {
        if (u == null || u.getRole() == null) return false;
        String role = u.getRole().toUpperCase();
        return role.contains("ADMIN") || role.contains("MANAGER");
    }

    // طباعة القائمة ديناميكياً بناءً على حالة تسجيل الدخول ودور المستخدم
    private static void printDynamicMenu() {
        System.out.println("\n-------------------------------------------------");
        if (currentUser == null) {
            System.out.println("👤 الحالة: زائر (Guest) - سجل دخولك لعرض خيارات حسابك");
            System.out.println("1. [Sprint 1] تسجيل الدخول (User Login)");
            System.out.println("2. [Sprint 1] عرض المطاعم المتاحة وقائمة الوجبات");
            System.out.println("0. الخروج من البرنامج");
        } else if (isAdminOrManager(currentUser)) {
            System.out.println("👑 تم تسجيل الدخول كـ (مدير/مشرف): " + currentUser.getUsername() + " | [Role: " + currentUser.getRole() + "]");
            System.out.println("1. عرض المطاعم والوجبات");
            System.out.println("2. [Sprint 3 & 5] تعيين سائق ذكي للطلب (Smart Driver Assignment)");
            System.out.println("3. [Sprint 4] تحديث حالة الطلب (Order Status Lifecycle)");
            System.out.println("4. [Sprint 4] تطبيق التسعير الديناميكي (Dynamic Pricing)");
            System.out.println("5. تسجيل الخروج (Logout)");
            System.out.println("0. الخروج من البرنامج");
        } else {
            System.out.println("🛍️ تم تسجيل الدخول كـ (زبون): " + currentUser.getUsername() + " | [Role: " + currentUser.getRole() + "]");
            System.out.println("1. عرض المطاعم والوجبات المتاحة");
            System.out.println("2. [Sprint 2 & 5] إنشاء طلب جديد (Place Food Order)");
            System.out.println("3. [Sprint 4] معاينة تفاصيل وحالة الطلب (View Order Details)");
            System.out.println("4. [Sprint 4] دفع قيمة الطلب نقداً (Process Payment)");
            System.out.println("5. [Sprint 2] إلغاء طلب (Cancel Order)");
            System.out.println("6. تسجيل الخروج (Logout)");
            System.out.println("0. الخروج من البرنامج");
        }
        System.out.println("-------------------------------------------------");
    }

    // ==========================================
    // Sprint 1 Functions
    // ==========================================
    private static void handleLogin() {
        System.out.println("\n--- [Sprint 1: Login] ---");
        System.out.print("اسم المستخدم (Username): ");
        String username = scanner.nextLine().trim();
        System.out.print("كلمة المرور (Password): ");
        String password = scanner.nextLine().trim();

        try {
            currentUser = authService.login(username, password);
            System.out.println("🎉 تم تسجيل الدخول بنجاح! مرحباً بك بصلاحية (" + currentUser.getRole() + ").");
        } catch (Exception e) {
            System.out.println("❌ فشل تسجيل الدخول: " + e.getMessage());
        }
    }

    private static void handleLogout() {
        if (currentUser != null) {
            System.out.println("\n👋 تم تسجيل الخروج بنجاح من حساب: " + currentUser.getUsername());
            currentUser = null;
        }
    }

    private static void handleViewRestaurantsAndMenu() {
        System.out.println("\n--- [Sprint 1: Restaurant Catalog] ---");
        List<Restaurant> openRestaurants = restaurantRepo.findByIsOpenTrue();
        System.out.println("المطاعم المفتوحة حالياً:");
        for (Restaurant r : openRestaurants) {
            System.out.println(" - [" + r.getId() + "] " + r.getName() + " | المنطقة: " + r.getDeliveryZone() + " | الحد الأدنى للطلب: " + r.getMinimumOrderValue() + " شيكل");
        }

        System.out.print("\nأدخل رقم المطعم لرؤية المنيو (مثال: 1): ");
        String restId = scanner.nextLine().trim();
        if (!restId.isEmpty()) {
            try {
                List<Menu> availableItems = menuService.getRestaurantMenu(restId);
                if (availableItems.isEmpty()) {
                    System.out.println("⚠️ لا توجد وجبات متاحة حالياً لهذا المطعم.");
                } else {
                    System.out.println("الوجبات المتاحة في المطعم:");
                    for (Menu item : availableItems) {
                        System.out.println("   * [" + item.getId() + "] " + item.getName() + " - " + item.getPrice() + " شيكل");
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ خطأ: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // Sprint 2 & 5 Functions
    // ==========================================
    private static void handlePlaceOrder() {
        System.out.println("\n--- [Sprint 2 & 5: Place Order] ---");
        System.out.print("أدخل رقم المطعم (المتاح: 1): ");
        String restId = scanner.nextLine().trim();
        System.out.print("أدخل عنوان التوصيل (مثال: Nablus Center): ");
        String address = scanner.nextLine().trim();

        System.out.println("اختر نوع الطلب (Sprint 5):");
        System.out.println("1. Standard Order");
        System.out.println("2. Express Order (رسوم توصيل إضافية)");
        System.out.println("3. Scheduled Order (طلب موجه لوقت لاحق)");
        System.out.print("الخيار: ");
        String typeChoice = scanner.nextLine().trim();

        // 🔴 قراءة وقت التوصيل المستقبلي إذا كان الخيار Scheduled Order (3)
        LocalDateTime scheduledTime = null;
        if ("3".equals(typeChoice) || "SCHEDULED".equalsIgnoreCase(typeChoice)) {
            System.out.print("أدخل عدد الساعات القادمة لتحديد وقت التوصيل (مثال: 2): ");
            try {
                int hours = Integer.parseInt(scanner.nextLine().trim());
                if (hours > 0) {
                    scheduledTime = LocalDateTime.now().plusHours(hours);
                } else {
                    scheduledTime = LocalDateTime.now().plusHours(2); // وقت افتراضي إذا أُدخل 0 أو رقم سالب
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️ رقم غير صحيح، تم اعتماد وقت افتراضي بعد ساعتين.");
                scheduledTime = LocalDateTime.now().plusHours(2);
            }
        }

        List<Menu> availableItems = menuRepo.findByRestaurantIdAndIsAvailableTrue(restId);
        if (availableItems.isEmpty()) {
            System.out.println("❌ لا توجد وجبات متاحة لهذا المطعم لإتمام الطلب!");
            return;
        }

        System.out.println("الوجبات المتاحة للاختيار:");
        for (Menu item : availableItems) {
            System.out.println("   * [" + item.getId() + "] " + item.getName() + " - " + item.getPrice() + " شيكل");
        }

        System.out.print("أدخل رقم الوجبة المرادة (مثال: m1 أو m3): ");
        String selectedItemId = scanner.nextLine().trim();
        System.out.print("أدخل الكمية: ");
        
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ تم رفض الطلب: الكمية يجب أن تكون رقماً صريحاً!");
            return;
        }

        Menu selectedMenu = null;
        for (Menu item : availableItems) {
            if (item.getId().equalsIgnoreCase(selectedItemId)) {
                selectedMenu = item;
                break;
            }
        }

        if (selectedMenu == null) {
            System.out.println("❌ الوجبة غير موجودة أو غير متاحة!");
            return;
        }

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(selectedMenu, qty));

        try {
            String activeUserId = currentUser.getUsername();

            // 🔴 التعديل هنا: تمرير نوع الطلب (typeChoice) ووقت التوصيل (scheduledTime) لـ OrderService
            Order order = orderService.placeOrder(activeUserId, restId, address, items, typeChoice, scheduledTime);

            System.out.println("✅ تم إنشاء الطلب بنجاح للزبون: " + activeUserId);
            System.out.println("   رقم الطلب (Order ID): " + order.getId());
            System.out.println("   الحالة الحالية: " + order.getStatus());
            System.out.println("   الإجمالي النهائي: " + order.getFinalPrice() + " شيكل");
        } catch (IllegalArgumentException e) {
            // 🔴 التقاط رسائل الرفض القادمة من OrderService
            System.out.println("❌ تم رفض الطلب: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ فشل إنشاء الطلب: " + e.getMessage());
        }
    }

    private static void handleCancelOrder() {
        System.out.println("\n--- [Sprint 2: Cancel Order] ---");
        System.out.print("أدخل رقم الطلب للإلغاء: ");
        String orderId = scanner.nextLine().trim();

        try {
            Order order = orderRepo.findById(orderId);
            if (order != null && order.canBeCancelled()) {
                order.setStatus("CANCELLED");
                System.out.println("✅ تم إلغاء الطلب بنجاح وتحويل حالته إلى CANCELLED.");
            } else {
                System.out.println("❌ لا يمكن إلغاء الطلب الحالي (الحالة غير مسموحة للإلغاء).");
            }
        } catch (Exception e) {
            System.out.println("❌ لا يمكن إلغاء الطلب: " + e.getMessage());
        }
    }

    // ==========================================
    // Sprint 3 & 5 Functions (Admin / Manager)
    // ==========================================
    private static void handleAssignDriver() {
        System.out.println("\n--- [Sprint 3 & 5: Smart Driver Assignment] ---");
        System.out.print("أدخل رقم الطلب لتعيين سائق له: ");
        String orderId = scanner.nextLine().trim();

        Order order = orderRepo.findById(orderId);
        if (order == null) {
            System.out.println("❌ لم يتم العثور على الطلب ورقم الـ ID غير موجود!");
            return;
        }

        System.out.println("اختر استراتيجية تعيين السائق (Strategy Pattern):");
        System.out.println("1. Nearest Driver (أقرب سائق)");
        System.out.println("2. Fastest Driver (أسرع سائق)");
        System.out.print("الخيار: ");
        String stratChoice = scanner.nextLine().trim();

        if ("2".equals(stratChoice)) {
            driverService.setSelectionStrategy(new FastestDriverStrategy());
        } else {
            driverService.setSelectionStrategy(new NearestDriverStrategy());
        }

        try {
            // 1. حساب حجم الطلب تلقائياً من الوجبات
            int orderSize = 0;
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    orderSize += item.getQuantity();
                }
            }

            // 2. حساب مسافة التوصيل التقريبية
            double deliveryDistance = 2.5; // قيمة افتراضية
            if (order.getDeliveryFee() != null && order.getDeliveryFee().doubleValue() > 0) {
                double baseFee = order.getDeliveryFee().doubleValue();
                if (order instanceof ExpressOrder) {
                    baseFee -= 5.0; // خصم رسوم التوصيل السريع للحصول على التكلفة الأصلية
                }
                deliveryDistance = baseFee / 2.0; // المعادلة الأصلية لحساب المسافة
            }

            // 3. التعيين المتعدد الأشكال (US5.4) بحساب أهلية المركبة
            Driver assignedDriver = driverService.assignDriver(order, deliveryDistance, orderSize);
            
            System.out.println("=================================================");
            System.out.println("✅ تم تعيين السائق بنجاح!");
            System.out.println("   اسم السائق        : " + assignedDriver.getName());
            System.out.println("   نوع المركبة/السائق: " + assignedDriver.getDriverType()); // 🟢 طباعة نوع المركبة
            System.out.println("   حالة السائق الآن  : " + assignedDriver.getStatus());
            System.out.println("=================================================");
        } catch (Exception e) {
            System.out.println("❌ فشل تعيين السائق: " + e.getMessage());
        }
    }

    // ==========================================
    // Sprint 4 Functions (Admin / Manager)
    // ==========================================
    private static void handleUpdateOrderStatus() {
        System.out.println("\n--- [Sprint 4: Order Status Transitions] ---");
        System.out.print("أدخل رقم الطلب: ");
        String orderId = scanner.nextLine().trim();

        System.out.println("اختر الحالة الجديدة للطلب:");
        System.out.println("1. CONFIRMED");
        System.out.println("2. PREPARING");
        System.out.println("3. READY");
        System.out.println("4. OUT_FOR_DELIVERY");
        System.out.println("5. DELIVERED");
        System.out.print("الخيار: ");
        
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ خيار غير صحيح!");
            return;
        }

        String[] statuses = {"CONFIRMED", "PREPARING", "READY", "OUT_FOR_DELIVERY", "DELIVERED"};

        if (choice >= 1 && choice <= 5) {
            try {
                orderService.updateOrderStatus(orderId, statuses[choice - 1]);
                System.out.println("✅ تم تحديث حالة الطلب إلى: " + statuses[choice - 1]);
                System.out.println("📢 (تم إشعار المراقبين Observer Pattern بنجاح)");
            } catch (Exception e) {
                System.out.println("❌ الانتقال للحالة غير مسموح: " + e.getMessage());
            }
        }
    }

    private static void handleCalculatePricing() {
        System.out.println("\n--- [Sprint 4: Dynamic Pricing & Apply Strategy] ---");
        System.out.print("أدخل رقم الطلب لتطبيق خطة التسعير عليه: ");
        String orderId = scanner.nextLine().trim();

        Order order = orderRepo.findById(orderId);
        if (order == null) {
            System.out.println("❌ لم يتم العثور على الطلب!");
            return;
        }

        System.out.println("\nاختر استراتيجية التسعير المراد تطبيقها (Strategy Pattern):");
        System.out.println("1. Normal Pricing (تسعير عادي)");
        System.out.println("2. Peak-Hour Pricing (ساعات الذروة - إضافة 15%)");
        System.out.println("3. Express Delivery Pricing (توصيل سريع - إضافة 5 شيكل)");
        System.out.println("4. Promotional Pricing (خصم ترويجي - 10%)");
        System.out.print("الخيار: ");
        String pChoice = scanner.nextLine().trim();

        PricingStrategy strategy;
        switch (pChoice) {
            case "2": 
                strategy = new PeakHourPricingStrategy(new BigDecimal("15.00")); 
                break;
            case "3": 
                strategy = new ExpressDeliveryPricingStrategy(new BigDecimal("5.00")); 
                break;
            case "4": 
                strategy = new PromotionalPricingStrategy(new BigDecimal("10.00")); 
                break;
            default: 
                strategy = new NormalPricingStrategy(); 
                break;
        }

        pricingService.setPricingStrategy(strategy);

        try {
            BigDecimal finalPrice = pricingService.applyPricing(order);
            System.out.println("-------------------------------------------------");
            System.out.println("💰 المجموع النهائي الجديد للطلب بعد تطبيق الاستراتيجية: " + finalPrice + " شيكل");
        } catch (Exception e) {
            System.out.println("❌ تعذر حساب التسعير: " + e.getMessage());
        }
    }

    // ==========================================
    // Sprint 4 Functions (Customer Details Query)
    // ==========================================
    private static void handleViewOrderDetails() {
        System.out.println("\n--- [Sprint 4: View Order Details] ---");
        System.out.print("أدخل رقم الطلب لمعاينة تفاصيله: ");
        String orderId = scanner.nextLine().trim();

        Order order = orderRepo.findById(orderId);
        if (order == null) {
            System.out.println("❌ لم يتم العثور على الطلب!");
            return;
        }

        System.out.println("=================================================");
        System.out.println("📋 تفاصيل الطلب (Order Details):");
        System.out.println(" - رقم الطلب (Order ID)   : " + order.getId());
        System.out.println(" - صاحب الطلب (Customer)  : " + order.getUserId());
        System.out.println(" - حالة الطلب الحالية     : " + order.getStatus());
        System.out.println(" - حالة الدفع             : " + order.getPaymentStatus());
        System.out.println(" - المجموع النهائي       : " + order.getFinalPrice() + " شيكل");
        System.out.println("=================================================");
    } 

    private static void handleProcessPayment() {
        System.out.println("\n--- [Sprint 4: Process Cash Payment] ---");
        System.out.print("أدخل رقم الطلب للدفع: ");
        String orderId = scanner.nextLine().trim();

        Order order = orderRepo.findById(orderId);
        if (order == null) {
            System.out.println("❌ لم يتم العثور على الطلب!");
            return;
        }

        System.out.print("أدخل المبلغ المدفوع نقداً: ");
        BigDecimal amountPaid = new BigDecimal(scanner.nextLine().trim());

        try {
            String resultMessage = paymentService.processCashPayment(order, amountPaid);
            System.out.println("✅ " + resultMessage);
            System.out.println("   حالة الدفع الحالية للطلب: " + order.getPaymentStatus());
        } catch (Exception e) {
            System.out.println("❌ عملية الدفع مرفوضة: " + e.getMessage());
        }
    }
}