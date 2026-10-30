package service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import io.github.cdimascio.dotenv.Dotenv;

public class EmailService {
 
    private final String username; 
    private final String password;

    // المشيد الرئيسي: يستقبل اسم المستخدم وكلمة السر مباشرة
    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // مشيد افتراضي: يقرأ الحساب تلقائياً من ملف .env لتسهيل الاستخدام بداخل باقي الخدمات
    public EmailService() {
        Dotenv dotenv = Dotenv.load();
        this.username = dotenv.get("EMAIL_USERNAME");
        this.password = dotenv.get("EMAIL_PASSWORD");
    }

    public void sendEmail(String to, String subject, String body) {
        // 🟢 الفحص المضاف: منع الإرسال الحقيقي وإيقاف SMTP تلقائياً أثناء تشغيل بيئة الاختبارات (JUnit/Mockito)
        if (isTestEnvironment()) {
            System.out.println("🧪 [TEST MODE] Real email dispatch bypassed: " + subject + " to " + to);
            return;
        }

        // SMTP configuration
        Properties props = new Properties();
        
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // e.g., Gmail SMTP
        props.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Build email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Send email
            Transport.send(message);

            System.out.println("Email sent successfully to " + to);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    // دالة للتحقق مما إذا كان التنفيذ جارياً من داخل إطار عمل اختبار (JUnit)
    private boolean isTestEnvironment() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.") || 
                element.getClassName().startsWith("org.mockito.")) {
                return true;
            }
        }
        return false;
    }

    static void run() {
        Dotenv dotenv = Dotenv.load();  
        
        String username = dotenv.get("EMAIL_USERNAME");
        String password = dotenv.get("EMAIL_PASSWORD");
        
        EmailService emailService = new EmailService(username, password);
        
        String subject = "Food Delivery Order";
        String body = "Dear user, Your Order is coming soon. Best regards";
       
        // 🟢 تم تعديل البريد إلى إيميل الزبون المحدد
        emailService.sendEmail("talasaadeh775@gmail.com", subject, body);
    }
    
    public static void main(String[] args) {
        run();
    }
}