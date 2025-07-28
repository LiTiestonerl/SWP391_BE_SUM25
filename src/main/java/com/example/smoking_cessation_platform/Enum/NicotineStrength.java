package com.example.smoking_cessation_platform.Enum;  // Đặt trong thư mục enum

public enum NicotineStrength {
    ZERO, LOW, MEDIUM, HIGH;

    public static NicotineStrength fromString(String value) {
        for (NicotineStrength strength : values()) {
            if (strength.name().equalsIgnoreCase(value)) {
                return strength;
            }
        }
        throw new IllegalArgumentException("Unknown nicotine strength: " + value);
    }
}
