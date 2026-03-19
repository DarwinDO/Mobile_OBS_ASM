package com.example.mobile_obs_asm.data;

import android.content.Context;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.OrderPreview;
import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.order.OrderCreateRequestBody;
import com.example.mobile_obs_asm.network.order.OrderApiService;
import com.example.mobile_obs_asm.network.order.RemoteOrderResponse;
import com.example.mobile_obs_asm.util.ApiErrorMessageExtractor;
import com.example.mobile_obs_asm.util.DateLabelFormatter;
import com.example.mobile_obs_asm.util.DisplayLabelFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRemoteRepository {

    private final OrderApiService orderApiService;

    public OrderRemoteRepository(Context context) {
        orderApiService = RetrofitClient.createOrderApiService(context);
    }

    public void createOrder(
            String productId,
            String paymentOption,
            String paymentMethod,
            BigDecimal upfrontAmount,
            RepositoryCallback<RemoteOrderResponse> callback
    ) {
        BigDecimal normalizedUpfrontAmount = "full".equals(paymentOption) ? null : upfrontAmount;
        OrderCreateRequestBody requestBody = new OrderCreateRequestBody(
                productId,
                normalizedUpfrontAmount,
                null,
                BigDecimal.ZERO,
                paymentOption,
                paymentMethod
        );

        orderApiService.createOrder(requestBody).enqueue(new Callback<ApiEnvelope<RemoteOrderResponse>>() {
            @Override
            public void onResponse(Call<ApiEnvelope<RemoteOrderResponse>> call, Response<ApiEnvelope<RemoteOrderResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(response, "Không thể tạo yêu cầu mua lúc này."),
                            null
                    );
                    return;
                }
                callback.onSuccess(response.body().getResult());
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemoteOrderResponse>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để tạo yêu cầu mua.", throwable);
            }
        });
    }

    public void fetchMyOrders(RepositoryCallback<List<OrderPreview>> callback) {
        orderApiService.getMyOrders().enqueue(new Callback<ApiEnvelope<List<RemoteOrderResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<List<RemoteOrderResponse>>> call,
                    Response<ApiEnvelope<List<RemoteOrderResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(
                                    response,
                                    resolveErrorMessage(response.code(), "Không thể đọc danh sách đơn mua từ máy chủ.")
                            ),
                            null
                    );
                    return;
                }

                List<OrderPreview> mappedOrders = new ArrayList<>();
                for (RemoteOrderResponse remoteOrder : response.body().getResult()) {
                    mappedOrders.add(mapOrder(remoteOrder));
                }
                callback.onSuccess(mappedOrders);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<List<RemoteOrderResponse>>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để tải danh sách đơn mua.", throwable);
            }
        });
    }

    private OrderPreview mapOrder(RemoteOrderResponse remoteOrder) {
        String status = formatValue(remoteOrder.getStatus());
        return new OrderPreview(
                fallback(remoteOrder.getId(), "Đơn mua"),
                fallback(remoteOrder.getProductTitle(), "Sản phẩm đang cập nhật"),
                buildTimeline(remoteOrder),
                resolveDisplayAmount(remoteOrder).longValue(),
                status,
                pickStatusColor(remoteOrder.getStatus(), remoteOrder.getFundingStatus()),
                formatValue(remoteOrder.getFundingStatus()),
                formatValue(remoteOrder.getPaymentMethod()),
                emptyFallback(DateLabelFormatter.formatIsoDateTime(remoteOrder.getCreatedAt())),
                emptyFallback(DateLabelFormatter.formatIsoDateTime(remoteOrder.getPaymentDeadline())),
                buildPartiesLabel(remoteOrder),
                buildSummaryNote(remoteOrder),
                true
        );
    }

    private BigDecimal resolveDisplayAmount(RemoteOrderResponse remoteOrder) {
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

    private String buildTimeline(RemoteOrderResponse remoteOrder) {
        String deadline = DateLabelFormatter.formatIsoDateTime(remoteOrder.getPaymentDeadline());
        if (!deadline.isEmpty()) {
            return "Hạn thanh toán: " + deadline;
        }

        String acceptedAt = DateLabelFormatter.formatIsoDateTime(remoteOrder.getAcceptedAt());
        if (!acceptedAt.isEmpty()) {
            return "Đã được tiếp nhận lúc " + acceptedAt + " • " + formatValue(remoteOrder.getFundingStatus());
        }

        String createdAt = DateLabelFormatter.formatIsoDateTime(remoteOrder.getCreatedAt());
        if (!createdAt.isEmpty()) {
            return "Tạo lúc " + createdAt;
        }

        return "Trạng thái tiền: " + formatValue(remoteOrder.getFundingStatus());
    }

    private String buildPartiesLabel(RemoteOrderResponse remoteOrder) {
        return "Người mua: " + fallback(remoteOrder.getBuyerName(), "Đang cập nhật")
                + " • Người bán: " + fallback(remoteOrder.getSellerName(), "Đang cập nhật");
    }

    private String buildSummaryNote(RemoteOrderResponse remoteOrder) {
        return "Hình thức thanh toán: "
                + formatValue(remoteOrder.getPaymentOption())
                + " • Trạng thái tiền: "
                + formatValue(remoteOrder.getFundingStatus());
    }

    private int pickStatusColor(String rawStatus, String rawFundingStatus) {
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

    private String formatValue(String rawValue) {
        return DisplayLabelFormatter.formatValue(rawValue);
    }

    private String fallback(String value, String fallback) {
        return value == null || value.isEmpty() ? fallback : value;
    }

    private String emptyFallback(String value) {
        return value == null || value.isEmpty() ? "Đang cập nhật" : value;
    }

    private String resolveErrorMessage(int statusCode, String fallbackMessage) {
        if (statusCode == 401 || statusCode == 403) {
            return "Hãy đăng nhập để xem đơn mua của bạn.";
        }
        return fallbackMessage;
    }
}
