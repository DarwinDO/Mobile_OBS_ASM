package com.example.mobile_obs_asm.network.reference;

import com.example.mobile_obs_asm.network.ApiEnvelope;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ReferenceDataApiService {

    @GET("api/brake-types")
    Call<ApiEnvelope<List<RemoteReferenceValueResponse>>> getBrakeTypes();

    @GET("api/frame-materials")
    Call<ApiEnvelope<List<RemoteReferenceValueResponse>>> getFrameMaterials();
}
