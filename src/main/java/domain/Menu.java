package domain;

import java.math.BigDecimal;

public class Menu {
	private String id;
    private String restaurantId;
    private String name;
    private BigDecimal price;
    private boolean isAvailable; // يحدد إذا كان المنتج متوفراً أم لا

    public Menu(String id, String restaurantId, String name, BigDecimal price, boolean isAvailable) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public String getId() { return id; }
    public String getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public boolean isAvailable() { return isAvailable; }
}

