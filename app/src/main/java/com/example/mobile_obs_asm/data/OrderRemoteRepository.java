package com.example.mobile_obs_asm.data;

import android.content.Context;

import com.example.mobile_obs_asm.model.OrderPreview;
import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.order.OrderApiService;
import com.example.mobile_obs_asm.network.order.OrderCreateRequestBody;
import com.example.mobile_obs_asm.network.order.RemoteOrderResponse;
import com.example.mobile_obs_asm.util.ApiErrorMessageExtractor;
import com.example.mobile_obs_asm.util.OrderPreviewMapper;

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
                                    resolveErrorMessage(response.code(), "Không thể đọc danh sách đơn từ máy chủ.")
                            ),
                            null
                    );
                    return;
                }

                List<OrderPreview> mappedOrders = new ArrayList<>();
                for (RemoteOrderResponse remoteOrder : response.body().getResult()) {
                    mappedOrders.add(OrderPreviewMapper.map(remoteOrder));
                }
                callback.onSuccess(mappedOrders);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<List<RemoteOrderResponse>>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để tải danh sách đơn.", throwable);
            }
        });
    }

    public void fetchMyOrderById(String orderId, RepositoryCallback<OrderPreview> callback) {
        fetchMyOrders(new RepositoryCallback<List<OrderPreview>>() {
            @Override
            public void onSuccess(List<OrderPreview> value) {
                for (OrderPreview orderPreview : value) {
                    if (orderPreview.getId().equals(orderId)) {
                        callback.onSuccess(orderPreview);
                        return;
                    }
                }
                callback.onError("Không tìm thấy đơn hàng này trong danh sách của bạn.", null);
            }

            @Override
            public void onError(String message, Throwable throwable) {
                callback.onError(message, throwable);
            }
        });
    }

    public void acceptOrder(String orderId, RepositoryCallback<OrderPreview> callback) {
        orderApiService.acceptOrder(orderId).enqueue(createActionCallback(callback, "Không thể tiếp nhận đơn này lúc này."));
    }

    public void confirmCashDeposit(String orderId, RepositoryCallback<OrderPreview> callback) {
        orderApiService.confirmDeposit(orderId).enqueue(createActionCallback(callback, "Không thể xác nhận tiền cọc lúc này."));
    }

    public void completeOrder(String orderId, RepositoryCallback<OrderPreview> callback) {
        orderApiService.completeOrder(orderId).enqueue(createActionCallback(callback, "Không thể cập nhật bước giao xe lúc này."));
    }

    public void confirmReceived(String orderId, RepositoryCallback<OrderPreview> callback) {
        orderApiService.confirmReceived(orderId).enqueue(createActionCallback(callback, "Không thể xác nhận đã nhận xe lúc này."));
    }

    public void cancelOrder(String orderId, RepositoryCallback<OrderPreview> callback) {
        orderApiService.cancelOrder(orderId).enqueue(createActionCallback(callback, "Không thể huỷ đơn lúc này."));
    }

    private Callback<ApiEnvelope<RemoteOrderResponse>> createActionCallback(
            RepositoryCallback<OrderPreview> callback,
            String fallbackError
    ) {
        return new Callback<ApiEnvelope<RemoteOrderResponse>>() {
            @Override
            public void onResponse(Call<ApiEnvelope<RemoteOrderResponse>> call, Response<ApiEnvelope<RemoteOrderResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(ApiErrorMessageExtractor.extract(response, fallbackError), null);
                    return;
                }
                callback.onSuccess(OrderPreviewMapper.map(response.body().getResult()));
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemoteOrderResponse>> call, Throwable throwable) {
                callback.onError("Không thể kết nối tới máy chủ để cập nhật đơn hàng.", throwable);
            }
        };
    }

    private String resolveErrorMessage(int statusCode, String fallbackMessage) {
        if (statusCode == 401 || statusCode == 403) {
            return "Hãy đăng nhập để xem đơn của bạn.";
        }
        return fallbackMessage;
    }
}
