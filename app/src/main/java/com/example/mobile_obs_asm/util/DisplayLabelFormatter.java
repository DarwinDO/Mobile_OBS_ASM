package com.example.mobile_obs_asm.util;

public final class DisplayLabelFormatter {

    private DisplayLabelFormatter() {
    }

    public static String formatValue(String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) {
            return "Chưa cập nhật";
        }

        String normalized = rawValue.trim().toLowerCase().replace('-', '_');
        switch (normalized) {
            case "buyer":
                return "Người mua";
            case "seller":
                return "Người bán";
            case "guest":
                return "Khách";
            case "admin":
                return "Quản trị viên";
            case "inspector":
                return "Kiểm định viên";
            case "new_90":
                return "Mới 90%";
            case "used":
                return "Đã qua sử dụng";
            case "needs_repair":
                return "Cần sửa chữa";
            case "pending":
                return "Đang chờ";
            case "active":
                return "Đang hiển thị";
            case "hidden":
                return "Đã ẩn";
            case "sold":
                return "Đã bán";
            case "pending_inspection":
                return "Chờ kiểm định";
            case "inspected_passed":
                return "Đạt kiểm định";
            case "inspected_failed":
                return "Không đạt kiểm định";
            case "partial":
                return "Thanh toán một phần";
            case "full":
                return "Thanh toán toàn bộ";
            case "transfer":
                return "Chuyển khoản";
            case "cash":
                return "Tiền mặt";
            case "online":
                return "Thanh toán online";
            case "processing":
                return "Đang xử lý";
            case "success":
                return "Thành công";
            case "failed":
                return "Thất bại";
            case "refunded":
                return "Đã hoàn tiền";
            case "upfront":
                return "Đặt cọc";
            case "remaining":
                return "Thanh toán còn lại";
            case "sepay":
                return "SePay";
            case "manual":
                return "Thủ công";
            case "held":
                return "Đang giữ tiền";
            case "released":
                return "Đã giải ngân";
            case "awaiting_payment":
                return "Chờ thanh toán";
            case "awaiting_buyer_confirmation":
                return "Chờ người mua xác nhận";
            case "deposited":
                return "Đã đặt cọc";
            case "completed":
                return "Hoàn tất";
            case "cancelled":
                return "Đã huỷ";
            default:
                return toSentenceCase(normalized.replace('_', ' '));
        }
    }

    private static String toSentenceCase(String value) {
        String[] words = value.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
    }
}
