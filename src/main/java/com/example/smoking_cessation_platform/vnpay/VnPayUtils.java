package com.example.smoking_cessation_platform.vnpay;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;


@Component
public class VnPayUtils {
    /**
     * Tạo chữ ký HMAC SHA512 từ chuỗi dữ liệu và khóa bí mật.
     * Dùng để tạo `vnp_SecureHash` gửi kèm request tới VNPay nhằm đảm bảo tính toàn vẹn của dữ liệu.
     *
     * @param key  Khóa bí mật (SecretKey) do VNPay cung cấp
     * @param data Chuỗi dữ liệu cần ký (ví dụ: tất cả param của request nối lại theo thứ tự key)
     * @return Chuỗi hash HMAC SHA512 dạng hex (chữ thường). Trả về rỗng nếu có lỗi.
     */
    public static String hmacSHA512(final String key, final String data) {
        try {
            // Check null tránh lỗi runtime
            if (key == null || data == null) {
                throw new NullPointerException();
            }

            // Tạo đối tượng HMAC với thuật toán SHA-512
            final Mac hmac512 = Mac.getInstance("HmacSHA512");

            // Tạo khóa từ key truyền vào
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");

            // Khởi tạo HMAC với key
            hmac512.init(secretKey);

            // Thực hiện mã hóa dữ liệu
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);

            // Chuyển kết quả byte[] sang chuỗi hex (hexadecimal string)
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();

        } catch (Exception ex) {
            // Trả về chuỗi rỗng nếu có lỗi
            return "";
        }
    }


    /**
     * Sinh chuỗi số ngẫu nhiên chỉ chứa các chữ số từ 0 đến 9.
     * Dùng để tạo mã giao dịch duy nhất (vnp_TxnRef) gửi tới VNPay.
     *
     * @param len Độ dài mong muốn của chuỗi số
     * @return Chuỗi số ngẫu nhiên, ví dụ: "93827461"
     */
    public static String getRandomNumber(int len) {
        Random rnd = new Random(); // Khởi tạo đối tượng Random để sinh số ngẫu nhiên
        String chars = "0123456789"; // Chỉ dùng các ký tự số

        StringBuilder sb = new StringBuilder(len); // Dùng StringBuilder để xây dựng chuỗi kết quả

        // Lặp từ 0 đến len, chọn ngẫu nhiên 1 số tại mỗi bước
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }

        // Trả về chuỗi số đã sinh
        return sb.toString();
    }


    /**
     * Tạo chuỗi query string từ danh sách các tham số (key-value), dạng: key1=value1&key2=value2...
     * Được dùng để tạo phần query string trong URL gửi đến VNPay.
     *
     * @param paramsMap  Map chứa các tham số VNPay yêu cầu (ví dụ: vnp_TmnCode, vnp_Amount,...)
     * @param encodeKey  Có encode key hay không (thường là true để đảm bảo an toàn ký tự đặc biệt)
     * @return Chuỗi dạng query string, đã sắp xếp key theo alphabet và URL-encoded
     */
    public static String getPaymentURL(Map<String, String> paramsMap, boolean encodeKey) {
        return paramsMap.entrySet().stream()

                // Bỏ qua các entry có giá trị null hoặc rỗng
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())

                // Sắp xếp key theo thứ tự chữ cái (để đúng chuẩn ký chữ ký VNPay)
                .sorted(Map.Entry.comparingByKey())

                // Map mỗi entry thành key=value, có encode hoặc không encode key tùy tham số
                .map(entry ->
                        (encodeKey
                                ? URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII)
                                : entry.getKey())
                                + "=" +
                                URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))

                // Nối tất cả lại bằng dấu "&"
                .collect(Collectors.joining("&"));
    }

    /**
     * Kiểm tra tính hợp lệ của chữ ký VNPay (dùng trong vnpay-return).
     * @param vnpParams tất cả các param trả về từ VNPay (bao gồm cả vnp_SecureHash)
     * @param secretKey khóa bí mật VNPay (thường config trong application.properties)
     * @return true nếu chữ ký hợp lệ, ngược lại false
     */
    public static boolean validateSignature(Map<String, String> vnpParams, String secretKey) {
        String receivedHash = vnpParams.get("vnp_SecureHash");

        // Bỏ các param không dùng để tạo chữ ký
        Map<String, String> filteredParams = new HashMap<>(vnpParams);
        filteredParams.remove("vnp_SecureHash");
        filteredParams.remove("vnp_SecureHashType");

        // Tạo chuỗi dữ liệu để ký lại
        String data = getPaymentURL(filteredParams, false); // false: không encode key

        // Ký lại dữ liệu bằng secretKey
        String computedHash = hmacSHA512(secretKey, data);

        return receivedHash != null && receivedHash.equalsIgnoreCase(computedHash);
    }
}
