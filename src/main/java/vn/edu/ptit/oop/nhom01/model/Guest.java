package model;

import java.util.Objects;
import java.util.regex.Pattern;

import exception.InvalidBookingDataException;

/**
 * Khách lưu trú.
 *
 * Lớp bất biến (immutable): dữ liệu được kiểm tra chặt chẽ ngay khi khởi tạo,
 * nếu sai định dạng sẽ ném InvalidBookingDataException. Nhờ vậy một đối tượng
 * Guest đã tạo ra thì luôn hợp lệ.
 */
public class Guest {

    // CCCD: 9–12 chữ số
    private static final Pattern CCCD_PATTERN = Pattern.compile("[0-9]{9,12}");

    // SĐT: đúng 10 chữ số, bắt đầu bằng số 0
    private static final Pattern PHONE_PATTERN = Pattern.compile("0[0-9]{9}");

    // Họ tên: chỉ gồm chữ cái (kể cả có dấu tiếng Việt), các từ cách nhau 1 khoảng trắng
    private static final Pattern NAME_PATTERN =
            Pattern.compile("[\\p{L}\\p{M}]+( [\\p{L}\\p{M}]+)*");
    private static final int NAME_MIN_LENGTH = 2;
    private static final int NAME_MAX_LENGTH = 50;

    private final String fullName;
    private final String cccd;
    private final String phone;
    private final Gender gender;

    public Guest(String fullName, String cccd, String phone, Gender gender)
            throws InvalidBookingDataException {
        this.fullName = validateFullName(fullName);
        this.cccd = validateCccd(cccd);
        this.phone = validatePhone(phone);
        if (gender == null) {
            throw new InvalidBookingDataException("Giới tính không được để trống.");
        }
        this.gender = gender;
    }

    private static String validateFullName(String fullName) throws InvalidBookingDataException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new InvalidBookingDataException("Họ tên không được để trống.");
        }
        // Chuẩn hoá: bỏ khoảng trắng đầu/cuối, gộp nhiều khoảng trắng liên tiếp thành một
        String normalized = fullName.trim().replaceAll("\\s+", " ");
        if (normalized.length() < NAME_MIN_LENGTH
                || normalized.length() > NAME_MAX_LENGTH
                || !NAME_PATTERN.matcher(normalized).matches()) {
            throw new InvalidBookingDataException("Họ tên không hợp lệ: chỉ gồm chữ cái và khoảng trắng, dài "
                    + NAME_MIN_LENGTH + "–" + NAME_MAX_LENGTH + " ký tự.");
        }
        return normalized;
    }

    private static String validateCccd(String cccd) throws InvalidBookingDataException {
        if (cccd == null || !CCCD_PATTERN.matcher(cccd).matches()) {
            throw new InvalidBookingDataException("CCCD không hợp lệ: phải gồm 9–12 chữ số.");
        }
        return cccd;
    }

    private static String validatePhone(String phone) throws InvalidBookingDataException {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidBookingDataException(
                    "Số điện thoại không hợp lệ: phải gồm 10 chữ số và bắt đầu bằng số 0.");
        }
        return phone;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCccd() {
        return cccd;
    }

    public String getPhone() {
        return phone;
    }

    public Gender getGender() {
        return gender;
    }

    /** Hai khách được coi là cùng một người nếu trùng CCCD. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Guest)) {
            return false;
        }
        return cccd.equals(((Guest) o).cccd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cccd);
    }

    @Override
    public String toString() {
        return fullName + " (CCCD: " + cccd + ", SĐT: " + phone + ", " + gender + ")";
    }
}
