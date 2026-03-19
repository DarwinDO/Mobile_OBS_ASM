package com.example.mobile_obs_asm.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_obs_asm.ProductDetailActivity;
import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.data.FakeMarketplaceRepository;
import com.example.mobile_obs_asm.data.ProductRemoteRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HomeFragment extends Fragment {

    private ProductAdapter adapter;
    private ProductRemoteRepository productRemoteRepository;
    private RecyclerView recyclerView;
    private SectionStateController stateController;
    private List<Product> fallbackProducts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fallbackProducts = FakeMarketplaceRepository.getInstance().getFeaturedProducts();
        productRemoteRepository = new ProductRemoteRepository(requireContext());
        recyclerView = view.findViewById(R.id.recyclerHomeProducts);
        stateController = new SectionStateController(view, R.id.layoutHomeState);
        MaterialButton heroButton = view.findViewById(R.id.buttonHeroExplore);
        adapter = new ProductAdapter(fallbackProducts, this::openProductDetail);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        heroButton.setOnClickListener(v -> {
            Product firstProduct = adapter.getFirstProduct();
            if (firstProduct != null) {
                openProductDetail(firstProduct);
            }
        });

        loadProducts();
    }

    private void loadProducts() {
        recyclerView.setVisibility(View.VISIBLE);
        stateController.showLoading(
                getString(R.string.state_home_loading_title),
                getString(R.string.state_home_loading_message)
        );

        productRemoteRepository.fetchProducts(new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> value) {
                if (!isAdded()) {
                    return;
                }
                if (value.isEmpty()) {
                    adapter.replaceProducts(fallbackProducts);
                    recyclerView.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
                    stateController.showMessage(
                            getString(R.string.state_home_empty_title),
                            getString(R.string.state_home_empty_message),
                            getString(R.string.state_action_retry),
                            retryView -> loadProducts()
                    );
                    return;
                }
                adapter.replaceProducts(value);
                recyclerView.setVisibility(View.VISIBLE);
                stateController.hide();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (!isAdded()) {
                    return;
                }
                adapter.replaceProducts(fallbackProducts);
                recyclerView.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
                stateController.showMessage(
                        getString(R.string.state_home_error_title),
                        getString(R.string.state_home_error_message),
                        getString(R.string.state_action_retry),
                        retryView -> loadProducts()
                );
            }
        });
    }

    private void openProductDetail(Product product) {
        startActivity(ProductDetailActivity.createIntent(requireContext(), product));
    }
}
