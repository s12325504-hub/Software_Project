import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MainTest {

    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() throws Exception {
        // إعادة تعيين المستخدم الحالي لضمان البداية كـ Guest في كل تست
        Field currentUserField = Main.class.getDeclaredField("currentUser");
        currentUserField.setAccessible(true);
        currentUserField.set(null, null);
    }

    @AfterEach
    void restoreSystemInput() {
        System.setIn(originalIn);
    }

    private void provideInputAndResetScanner(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);

        try {
            Field scannerField = Main.class.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            scannerField.set(null, new Scanner(System.in));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Should test Main constructor coverage")
    void testConstructor() {
        Main mainInstance = new Main();
        assertNotNull(mainInstance);
    }

    @Test
    @DisplayName("Should cover Guest Options Flow")
    void testGuestFlow() {
        String input = String.join("\n",
            "99",                  // خيار خاطئ للزائر
            "2", "1",              // عرض المطاعم والمنيو لمطعم 1
            "2", "999",            // عرض المنيو لمطعم غير موجود
            "2", "",               // رقم مطعم فارغ
            "1", "wrong", "pass",   // تسجيل دخول خاطئ
            "0"                    // الخروج من البرنامج
        ) + "\n";

        provideInputAndResetScanner(input);
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }

    @Test
    @DisplayName("Should cover Customer Ordering, Details, Payment & Cancellation Flow")
    void testCustomerFlow() {
        // تسلسل الإدخال الكامل لشاشة الزبون بما فيها تفاصيل إنشاء الطلبات والدفع والإلغاء
        String input = String.join("\n",
            // 1. تسجيل الدخول كزبون
            "1", "customer", "password",  
            "1", "1",                     // عرض المطاعم والمنيو
            
            // 2. إنشاء طلبات متنوعة (Standard, Express, Scheduled)
            "2", "1", "Nablus Center", "1", "m1", "2",          // Standard Order
            "2", "1", "Nablus Center", "2", "m1", "1",          // Express Order
            "2", "1", "Nablus Center", "3", "2", "m1", "1",      // Scheduled Order (ساعات صحيحة)
            "2", "1", "Nablus Center", "3", "0", "m1", "1",      // Scheduled Order (ساعات 0)
            "2", "1", "Nablus Center", "3", "abc", "m1", "1",    // Scheduled Order (إدخال خاطئ للساعات)
            "2", "1", "Nablus Center", "1", "m1", "abc",        // كمية غير رقمية
            "2", "1", "Nablus Center", "1", "unknown_item", "1",// وجبة غير موجودة
            "2", "999", "Nablus Center", "1", "m1", "1",        // مطعم لا توجد به وجبات متاحة
            
            // 3. معاينة التفاصيل والدفع والإلغاء
            "3", "ORD1",                  // معاينة تفاصيل طلب موجود
            "3", "INVALID_ORD",           // معاينة طلب غير موجود
            "4", "ORD1", "50.00",         // دفع قيمة الطلب نقداً
            "4", "INVALID_ORD", "10.00",  // دفع لطلب غير موجود
            "5", "ORD1",                  // إلغاء طلب
            "5", "INVALID_ORD",           // إلغاء طلب غير موجود
            
            "99",                         // خيار خاطئ للزبون
            "6",                          // تسجيل الخروج
            "0"                           // الخروج النهائي
        ) + "\n";

        provideInputAndResetScanner(input);
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }

    @Test
    @DisplayName("Should cover Admin Assignment, Status Transition & Dynamic Pricing Flow")
    void testAdminFlow() {
        // تسلسل الإدخال الكامل لشاشة المدير/الأدمن
        String input = String.join("\n",
            // 1. تسجيل الدخول كأدمن
            "1", "admin", "admin123",    
            "1", "1",                    // عرض المنيو
             
            // 2. تعيين السائق
            "2", "ORD1", "1",            // Nearest Driver Strategy
            "2", "ORD1", "2",            // Fastest Driver Strategy
            "2", "INVALID_ORD", "1",     // تعيين سائق لطلب غير موجود
            
            // 3. تحديث حالات الطلب
            "3", "ORD1", "1",            // CONFIRMED
            "3", "ORD1", "2",            // PREPARING
            "3", "ORD1", "3",            // READY
            "3", "ORD1", "4",            // OUT_FOR_DELIVERY
            "3", "ORD1", "5",            // DELIVERED
            "3", "ORD1", "abc",          // خيار غير رقمي للحالة
            
            // 4. تطبيق التسعير الديناميكي
            "4", "ORD1", "1",            // Normal Pricing
            "4", "ORD1", "2",            // Peak-Hour Pricing
            "4", "ORD1", "3",            // Express Pricing
            "4", "ORD1", "4",            // Promotional Pricing
            "4", "ORD1", "99",           // Default Strategy
            "4", "INVALID_ORD", "1",     // تسعير طلب غير موجود
            
            "88",                        // خيار خاطئ للمدير
            "5",                         // تسجيل الخروج
            "0"                          // الخروج النهائي
        ) + "\n";

        provideInputAndResetScanner(input);
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}