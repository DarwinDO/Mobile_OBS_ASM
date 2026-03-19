package com.example.mobile_obs_asm.util;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.OrderPreview;
import com.example.mobile_obs_asm.network.order.RemoteOrderResponse;

import java.math.BigDecimal;

public final class OrderPreviewMapper {

    private OrderPreviewMapper() {
    }

    public static OrderPreview map(RemoteOrderResponse remoteOrder) {
        String status = DisplayLabelFormatter.formatValue(remoteOrder.getStatus());
        return new OrderPreview(
                fallback(remoteOrder.getId(), "Đơn mua"),
                fallback(remoteOrder.getProductTitle(), "Sản phẩm đang cập nhật"),
                buildTimeline(remoteOrder),
                resolveDisplayAmount(remoteOrder).longValue(),
                status,
                pickStatusColor(remoteOrder.getStatus(), remoteOrder.getFundingStatus()),
                DisplayLabelFormatter.formatValue(remoteOrder.getFundingStatus()),
                DisplayLabelFormatter.formatValue(remoteOrder.getPaymentMethod()),
                emptyFallback(DateLabelFormatter.formatIsoDateTime(remoteOrder.getCreatedAt())),
                emptyFallback(DateLabelFormatter.formatIsoDateTime(remoteOrder.getPaymentDeadline())),
                buildPartiesLabel(remoteOrder),
                buildSummaryNote(remoteOrder),
                true,
                fallback(remoteOrder.getStatus(), ""),
                fallback(remoteOrder.getFundingStatus(), ""),
                fallback(remoteOrder.getPaymentMethod(), ""),
                fallback(remoteOrder.getPaymentOption(), ""),
                safeLong(remoteOrder.getTotalAmount()),
                safeLong(remoteOrder.getRequiredUpfrontAmount()),
                safeLong(remoteOrder.getRemainingAmount())
        );
    }

    private static BigDecimal resolveDisplayAmount(RemoteOrderResponse remoteOrder) {
        if (remoteOrder.getRemainingAmount() != null && remoteOrder.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            return remoteOrder.getRemainingAmount();
        }
        if (remoteOrder.getRequiredUpfrontAmount() != null && remoteOrder.getRequiredUpfrontAmount().compareTo(BigDecimal.ZERO) > 0) {
            return remoteOrder.getRequiredUpfrontAmount();
        }
        if (remoteOrder.getTotalAmount() != null) {
            return remoteOrder.getTotalAmount();
        }
        return BigDecimal.ZERO;
    }

    private static String buildTimeline(RemoteOrderResponse remoteOrder) {
        String deadline = DateLabelFormatter.formatIsoDateTime(remoteOrder.getPaymentDeadline());
        if (!deadline.isEmpty()) {
            return "Hạn thanh toán: " + deadline;
        }

        String acceptedAt = DateLabelFormatter.formatIsoDateTime(remoteOrder.getAcceptedAt());
        if (!acceptedAt.isEmpty()) {
            return "Đã được tiếp nhận lúc " + acceptedAt + " • " + DisplayLabelFormatter.formatValue(remoteOrder.getFundingStatus());
        }

        String createdAt = DateLabelFormatter.formatIsoDateTime(remoteOrder.getCreatedAt());
        if (!createdAt.isEmpty()) {
            return "Tạo lúc " + createdAt;
        }

        return "Trạng thái tiền: " + DisplayLabelFormatter.formatValue(remoteOrder.getFundingStatus());
    }

    private static String buildPartiesLabel(RemoteOrderResponse remoteOrder) {
        return "Người mua: " + fallback(remoteOrder.getBuyerName(), "Đang cập nhật")
                + " • Người bán: " + fallback(remoteOrder.getSellerName(), "Đang cập nhật");
    }

    private static String buildSummaryNote(RemoteOrderResponse remoteOrder) {
        return "Hình thức thanh toán: "
                + DisplayLabelFormatter.formatValue(remoteOrder.getPaymentOption())
                + " • Trạng thái tiền: "
                + DisplayLabelFormatter.formatValue(remoteOrder.getFundingStatus());
    }

    private static int pickStatusColor(String rawStatus, String rawFundingStatus) {
        String status = rawStatus == null ? "" : rawStatus.toLowerCase();
        String fundingStatus = rawFundingStatus == null ? "" : rawFundingStatus.toLowerCase();

        if ("completed".equals(status)) {
            return R.color.card_mint;
        }
        if ("cancelled".equals(status)) {
            return R.color.card_peach;
        }
        if ("deposited".equals(status)) {
            return R.color.primary_soft;
        }
        if ("awaiting_buyer_confirmation".equals(status)) {
            return R.color.card_blue;
        }
        if ("held".equals(fundingStatus)) {
            return R.color.primary_soft;
        }
        return R.color.card_sand;
    }

    private static String fallback(String value, String fallback) {
        return value == null || value.isEmpty() ? fallback : value;
    }

    private static String emptyFallback(String value) {
        return value == null || value.isEmpty() ? "Đang cập nhật" : value;
    }

    private static long safeLong(BigDecimal value) {
        return value == null ? 0L : value.longValue();
    }
}
