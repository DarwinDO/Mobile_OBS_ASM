package com.example.mobile_obs_asm.data;

import android.content.Context;

import com.example.mobile_obs_asm.model.PaymentHistoryItem;
import com.example.mobile_obs_asm.model.PaymentRequestInfo;
import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.payment.PaymentApiService;
import com.example.mobile_obs_asm.network.payment.RemotePaymentRequestResponse;
import com.example.mobile_obs_asm.network.payment.RemotePaymentResponse;
import com.example.mobile_obs_asm.util.ApiErrorMessageExtractor;
import com.example.mobile_obs_asm.util.DateLabelFormatter;
import com.example.mobile_obs_asm.util.DisplayLabelFormatter;
import com.example.mobile_obs_asm.util.PriceFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRemoteRepository {

    private final PaymentApiService paymentApiService;

    public PaymentRemoteRepository(Context context) {
        paymentApiService = RetrofitClient.createPaymentApiService(context);
    }

    public void requestUpfrontPayment(String orderId, RepositoryCallback<PaymentRequestInfo> callback) {
        paymentApiService.requestUpfrontPayment(orderId).enqueue(new Callback<ApiEnvelope<RemotePaymentRequestResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<RemotePaymentRequestResponse>> call,
                    Response<ApiEnvelope<RemotePaymentRequestResponse>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(response, "Không thể lấy thông tin thanh toán lúc này."),
                            null
                    );
                    return;
                }
                callback.onSuccess(mapRequestInfo(response.body().getResult()));
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemotePaymentRequestResponse>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để lấy thông tin thanh toán.", throwable);
            }
        });
    }

    public void fetchOrderPayments(String orderId, RepositoryCallback<List<PaymentHistoryItem>> callback) {
        paymentApiService.getOrderPayments(orderId).enqueue(new Callback<ApiEnvelope<List<RemotePaymentResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<List<RemotePaymentResponse>>> call,
                    Response<ApiEnvelope<List<RemotePaymentResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(response, "Không thể đọc lịch sử thanh toán lúc này."),
                            null
                    );
                    return;
                }

                List<PaymentHistoryItem> items = new ArrayList<>();
                for (RemotePaymentResponse remotePayment : response.body().getResult()) {
                    items.add(mapHistoryItem(remotePayment));
                }
                callback.onSuccess(items);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<List<RemotePaymentResponse>>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để tải lịch sử thanh toán.", throwable);
            }
        });
    }

    private PaymentRequestInfo mapRequestInfo(RemotePaymentRequestResponse response) {
        return new PaymentRequestInfo(
                safe(response.getCheckoutUrl()),
                safe(response.getQrCodeUrl()),
                safe(response.getTransferContent()),
                safe(response.getBankAccountNumber()),
                safe(response.getBankAccountName()),
                safe(response.getBankBin()),
                safe(response.getInstructions()),
                DateLabelFormatter.formatIsoDateTime(response.getExpiresAt()),
                PriceFormatter.formatCurrency(response.getAmount() == null ? 0L : response.getAmount().longValue()),
                response.isMockMode()
        );
    }

    private PaymentHistoryItem mapHistoryItem(RemotePaymentResponse response) {
        String headline = DisplayLabelFormatter.formatValue(response.getStatus())
                + " • " + PriceFormatter.formatCurrency(response.getAmount() == null ? 0L : response.getAmount().longValue());
        String supporting = DisplayLabelFormatter.formatValue(response.getPhase())
                + " • " + DisplayLabelFormatter.formatValue(response.getMethod())
                + " • " + DisplayLabelFormatter.formatValue(response.getGateway());
        if (response.getTransactionReference() != null && !response.getTransactionReference().isEmpty()) {
            supporting = supporting + " • Ref: " + response.getTransactionReference();
        }
        String timestamp = DateLabelFormatter.formatIsoDateTime(
                response.getPaymentDate() == null || response.getPaymentDate().isEmpty()
                        ? response.getCreatedAt()
                        : response.getPaymentDate()
        );
        return new PaymentHistoryItem(headline, supporting, timestamp);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
