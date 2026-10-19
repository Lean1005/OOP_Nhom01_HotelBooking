package exception;

/**
 * Ném ra khi phòng được yêu cầu không còn trống trong khoảng thời gian đặt.
 *
 * Kế thừa Exception (checked) để buộc lớp gọi phải xử lý, ví dụ: gợi ý
 * khách chọn phòng khác hoặc đổi ngày.
 */
public class RoomNotAvailableException extends Exception {

    private static final long serialVersionUID = 1L;

    public RoomNotAvailableException(String message) {
        super(message);
    }

    public RoomNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
