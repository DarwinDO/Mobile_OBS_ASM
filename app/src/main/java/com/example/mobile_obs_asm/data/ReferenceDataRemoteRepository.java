package com.example.mobile_obs_asm.data;

import android.content.Context;

import com.example.mobile_obs_asm.model.CreateListingFormOptions;
import com.example.mobile_obs_asm.model.ReferenceOption;
import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.reference.ReferenceDataApiService;
import com.example.mobile_obs_asm.network.reference.RemoteReferenceValueResponse;
import com.example.mobile_obs_asm.util.ApiErrorMessageExtractor;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferenceDataRemoteRepository {

    private final ReferenceDataApiService referenceDataApiService;

    public ReferenceDataRemoteRepository(Context context) {
        referenceDataApiService = RetrofitClient.createReferenceDataApiService(context);
    }

    public void fetchCreateListingOptions(RepositoryCallback<CreateListingFormOptions> callback) {
        referenceDataApiService.getBrakeTypes().enqueue(new Callback<ApiEnvelope<List<RemoteReferenceValueResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<List<RemoteReferenceValueResponse>>> call,
                    Response<ApiEnvelope<List<RemoteReferenceValueResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(response, "Không thể tải loại phanh để tạo tin."),
                            null
                    );
                    return;
                }

                List<ReferenceOption> brakeTypes = mapOptions(response.body().getResult());
                fetchFrameMaterials(brakeTypes, callback);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<List<RemoteReferenceValueResponse>>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để tải loại phanh.", throwable);
            }
        });
    }

    private void fetchFrameMaterials(
            List<ReferenceOption> brakeTypes,
            RepositoryCallback<CreateListingFormOptions> callback
    ) {
        referenceDataApiService.getFrameMaterials().enqueue(new Callback<ApiEnvelope<List<RemoteReferenceValueResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<List<RemoteReferenceValueResponse>>> call,
                    Response<ApiEnvelope<List<RemoteReferenceValueResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(response, "Không thể tải chất liệu khung để tạo tin."),
                            null
                    );
                    return;
                }

                callback.onSuccess(new CreateListingFormOptions(
                        brakeTypes,
                        mapOptions(response.body().getResult())
                ));
            }

            @Override
            public void onFailure(Call<ApiEnvelope<List<RemoteReferenceValueResponse>>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để tải chất liệu khung.", throwable);
            }
        });
    }

    private List<ReferenceOption> mapOptions(List<RemoteReferenceValueResponse> remoteOptions) {
        List<ReferenceOption> options = new ArrayList<>();
        for (RemoteReferenceValueResponse remoteOption : remoteOptions) {
            options.add(new ReferenceOption(remoteOption.getId(), remoteOption.getName()));
        }
        return options;
    }
}
