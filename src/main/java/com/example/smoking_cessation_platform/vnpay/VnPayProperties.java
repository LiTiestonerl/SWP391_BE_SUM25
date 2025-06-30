package com.example.smoking_cessation_platform.vnpay;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

// Annotation giúp generate các phương thức getter/setter, toString, equals,... từ Lombok
@Data
@Configuration
// Đánh dấu đây là một class chứa cấu hình (properties) sẽ được Spring quản lý
@ConfigurationProperties(
        prefix = "integration.vnpay", // Prefix dùng để ánh xạ với cấu hình trong application.yml
        ignoreUnknownFields = false   // Không bỏ qua nếu có field lạ trong file cấu hình
)
public class VnPayProperties {

    // URL gọi đến cổng thanh toán VNPay (sandbox hoặc production)
    private String url;

    // URL trả về sau khi người dùng hoàn tất thanh toán
    private String returnUrl;

    // Mã TMN Code do VNPay cung cấp cho bạn
    private String tmnCode;

    // Khóa bí mật để tạo chữ ký HMAC (vnp_SecureHash)
    private String secretKey;

    // Phiên bản API của VNPay, thường là "2.1.0"
    private String version;

    // Lệnh gửi lên VNPay, mặc định là "pay" để tạo thanh toán
    private String command;

    /**
     * Phương thức tiện lợi để tạo sẵn Map chứa các tham số cơ bản cần gửi lên VNPay
     * Các tham số này tuân theo tài liệu tích hợp của VNPay
     *
     * @return Map<String, String> gồm các key-value chuẩn bị sẵn để tạo URL thanh toán
     */
    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();

        // Phiên bản API VNPay sử dụng
        vnpParamsMap.put("vnp_Version", this.version);

        // Lệnh thực thi gửi lên VNPay (thường là "pay")
        vnpParamsMap.put("vnp_Command", this.command);

        // Mã Terminal ID do VNPay cung cấp
        vnpParamsMap.put("vnp_TmnCode", this.tmnCode);

        // Đơn vị tiền tệ sử dụng trong giao dịch, mặc định là "VND"
        vnpParamsMap.put("vnp_CurrCode", "VND");

        // Mã giao dịch (sinh ngẫu nhiên 8 chữ số)
        vnpParamsMap.put("vnp_TxnRef", VnPayUtils.getRandomNumber(8));

        // Loại đơn hàng, ví dụ: "Clothing", "BillPayment", "Topup",...
        vnpParamsMap.put("vnp_OrderType", "BillPayment");

        // Ngôn ngữ hiển thị trang thanh toán (vn hoặc en)
        vnpParamsMap.put("vnp_Locale", "vn");

        // URL callback khi giao dịch kết thúc (được định nghĩa trong config)
        vnpParamsMap.put("vnp_ReturnUrl", this.returnUrl);

        // Thời gian tạo giao dịch, định dạng: yyyyMMddHHmmss
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);

        // Thời gian hết hạn giao dịch (sau 15 phút)
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);

        // Trả về Map chứa toàn bộ cấu hình đã được tạo
        return vnpParamsMap;
    }
}
