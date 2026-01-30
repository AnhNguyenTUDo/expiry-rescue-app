package practice.expiry_rescue_app.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CONFIRMED(0),
    CANCELLED(1),
    DELETED(2);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public static OrderStatus fromValue(int value) {
        for (OrderStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status value: " + value);
    }
}