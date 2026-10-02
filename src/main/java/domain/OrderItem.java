package domain;

import java.math.BigDecimal;

public class OrderItem {
    private Menu menuItem;
    private int quantity;

    public OrderItem(Menu menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public Menu getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }

    // حساب مجموع هذا العنصر (السعر × الكمية)
    public BigDecimal getItemTotal() {
        return menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}