package model;

/**
 * Giới tính của khách lưu trú.
 */
public enum Gender {
    MALE("Nam"),
    FEMALE("Nữ"),
    OTHER("Khác");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    /** Nhãn tiếng Việt dùng để hiển thị. */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
