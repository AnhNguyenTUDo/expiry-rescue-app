package practice.expiry_rescue_app.enums;

import lombok.Getter;

@Getter
public enum InventoryStatus {
    AVAILABLE(0),
    NOT_AVAILABLE(1);

    private final int value;

    InventoryStatus(int value) {
        this.value = value;
    }

    public static InventoryStatus fromValue(int value) {
        for (InventoryStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid inventory status value: " + value);
    }
}
