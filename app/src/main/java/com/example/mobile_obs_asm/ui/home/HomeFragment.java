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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mobile_obs_asm.ProductDetailActivity;
import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.data.FakeMarketplaceRepository;
import com.example.mobile_obs_asm.data.ProductRemoteRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private enum FeedState {
        LIVE,
        EMPTY_FALLBACK,
        ERROR_FALLBACK
    }

    private enum ProductFilter {
        ROAD,
        GRAVEL,
        CITY,
        VINTAGE
    }

    private ProductAdapter adapter;
    private ProductRemoteRepository productRemoteRepository;
    private RecyclerView recyclerView;
    private SectionStateController stateController;
    private List<Product> fallbackProducts;
    private List<Product> sourceProducts;
    private FeedState currentFeedState = FeedState.LIVE;
    private ProductFilter selectedFilter;
    private ChipGroup chipGroup;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean allowResumeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fallbackProducts = FakeMarketplaceRepository.getInstance().getFeaturedProducts();
        sourceProducts = new ArrayList<>(fallbackProducts);
        productRemoteRepository = new ProductRemoteRepository(requireContext());
        recyclerView = view.findViewById(R.id.recyclerHomeProducts);
        stateController = new SectionStateController(view, R.id.layoutHomeState);
        chipGroup = view.findViewById(R.id.chipGroupHomeFilters);
        swipeRefreshLayout = view.findViewById(R.id.swipeHomeRefresh);
        MaterialButton heroButton = view.findViewById(R.id.buttonHeroExplore);
        adapter = new ProductAdapter(sourceProducts, this::openProductDetail);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this::loadProducts);

        heroButton.setOnClickListener(v -> {
            Product firstProduct = adapter.getFirstProduct();
            if (firstProduct != null) {
                openProductDetail(firstProduct);
            }
        });

        setupFilters();
        renderProductFeed();
        loadProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allowResumeRefresh) {
            loadProducts();
        }
        allowResumeRefresh = true;
    }

    private void setupFilters() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedFilter = null;
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipFilterRoad) {
                    selectedFilter = ProductFilter.ROAD;
                } else if (checkedId == R.id.chipFilterGravel) {
                    selectedFilter = ProductFilter.GRAVEL;
                } else if (checkedId == R.id.chipFilterCity) {
                    selectedFilter = ProductFilter.CITY;
                } else if (checkedId == R.id.chipFilterVintage) {
                    selectedFilter = ProductFilter.VINTAGE;
                }
            }
            renderProductFeed();
        });
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
                swipeRefreshLayout.setRefreshing(false);
                if (value == null || value.isEmpty()) {
                    currentFeedState = FeedState.EMPTY_FALLBACK;
                    sourceProducts = new ArrayList<>(fallbackProducts);
                    renderProductFeed();
                    return;
                }
                currentFeedState = FeedState.LIVE;
                sourceProducts = new ArrayList<>(value);
                renderProductFeed();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (!isAdded()) {
                    return;
                }
                swipeRefreshLayout.setRefreshing(false);
                currentFeedState = FeedState.ERROR_FALLBACK;
                sourceProducts = new ArrayList<>(fallbackProducts);
                renderProductFeed();
            }
        });
    }

    private void renderProductFeed() {
        List<Product> filteredProducts = filterProducts(sourceProducts);
        adapter.replaceProducts(filteredProducts);

        if (filteredProducts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            stateController.showMessage(
                    getString(R.string.state_home_filter_empty_title),
                    getString(R.string.state_home_filter_empty_message),
                    getString(R.string.state_action_clear_filter),
                    actionView -> {
                        chipGroup.clearCheck();
                        selectedFilter = null;
                        renderProductFeed();
                    }
            );
            return;
        }

        recyclerView.setVisibility(View.VISIBLE);
        if (currentFeedState == FeedState.EMPTY_FALLBACK) {
            stateController.showMessage(
                    getString(R.string.state_home_empty_title),
                    getString(R.string.state_home_empty_message),
                    getString(R.string.state_action_retry),
                    retryView -> loadProducts()
            );
            return;
        }

        if (currentFeedState == FeedState.ERROR_FALLBACK) {
            stateController.showMessage(
                    getString(R.string.state_home_error_title),
                    getString(R.string.state_home_error_message),
                    getString(R.string.state_action_retry),
                    retryView -> loadProducts()
            );
            return;
        }

        stateController.hide();
    }

    private List<Product> filterProducts(List<Product> products) {
        if (selectedFilter == null) {
            return new ArrayList<>(products);
        }

        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (matchesFilter(product, selectedFilter)) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }

    private boolean matchesFilter(Product product, ProductFilter filter) {
        String searchSpace = (
                safe(product.getId()) + " "
                        + safe(product.getTitle()) + " "
                        + safe(product.getTagline()) + " "
                        + safe(product.getDescription())
        ).toLowerCase(Locale.ROOT);

        switch (filter) {
            case ROAD:
                return containsAny(searchSpace, "road", "đường trường", "endurance", "race");
            case GRAVEL:
                return containsAny(searchSpace, "gravel", "địa hình", "diverge");
            case CITY:
                return containsAny(searchSpace, "city", "đi phố", "hybrid", "commute", "trek fx");
            case VINTAGE:
                return containsAny(searchSpace, "vintage", "classic", "cổ điển", "peugeot");
            default:
                return true;
        }
    }

    private boolean containsAny(String searchSpace, String... keywords) {
        for (String keyword : keywords) {
            if (searchSpace.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void openProductDetail(Product product) {
        startActivity(ProductDetailActivity.createIntent(requireContext(), product));
    }
}
