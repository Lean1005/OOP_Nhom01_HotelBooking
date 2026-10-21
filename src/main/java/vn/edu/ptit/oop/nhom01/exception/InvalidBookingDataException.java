package exception;

/**
 * Ném ra khi dữ liệu đặt phòng hoặc thông tin khách không hợp lệ,
 * ví dụ: CCCD, số điện thoại, họ tên sai định dạng; ngày nhận/trả phòng sai.
 *
 * Kế thừa Exception (checked) để buộc lớp gọi phải xử lý lỗi dữ liệu.
 */
public class InvalidBookingDataException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidBookingDataException(String message) {
        super(message);
    }

    public InvalidBookingDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
