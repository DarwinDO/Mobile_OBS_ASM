package com.example.mobile_obs_asm.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.google.android.material.textfield.TextInputEditText;

import java.text.Normalizer;
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

    private enum ProductGroup {
        ROAD,
        GRAVEL,
        CITY,
        VINTAGE,
        UNKNOWN
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
    private TextInputEditText inputSearch;
    private TextView textResultsSummary;
    private String searchQuery = "";
    private boolean includedUngroupedProducts;

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
        inputSearch = view.findViewById(R.id.inputHomeSearch);
        textResultsSummary = view.findViewById(R.id.textHomeResultsSummary);
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
        setupSearch();
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

    private void setupSearch() {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s == null ? "" : s.toString().trim();
                renderProductFeed();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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
        updateResultsSummary(filteredProducts.size(), sourceProducts.size());

        if (filteredProducts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            stateController.showMessage(
                    getString(R.string.state_home_filter_empty_title),
                    getString(R.string.state_home_filter_empty_message),
                    getString(R.string.state_action_clear_filter),
                    actionView -> clearFilters()
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
        List<Product> filteredProducts = new ArrayList<>();
        includedUngroupedProducts = false;
        for (Product product : products) {
            if (matchesFilter(product, selectedFilter) && matchesSearch(product)) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }

    private boolean matchesFilter(Product product, ProductFilter filter) {
        if (filter == null) {
            return true;
        }

        ProductGroup productGroup = inferProductGroup(product);
        if (productGroup == ProductGroup.UNKNOWN) {
            includedUngroupedProducts = true;
            return true;
        }

        switch (filter) {
            case ROAD:
                return productGroup == ProductGroup.ROAD;
            case GRAVEL:
                return productGroup == ProductGroup.GRAVEL;
            case CITY:
                return productGroup == ProductGroup.CITY;
            case VINTAGE:
                return productGroup == ProductGroup.VINTAGE;
            default:
                return true;
        }
    }

    private ProductGroup inferProductGroup(Product product) {
        String normalizedSearchSpace = buildGroupingSearchSpace(product);

        if (containsAny(
                normalizedSearchSpace,
                "gravel",
                "all road",
                "all-road",
                "adventure",
                "bikepacking",
                "dia hinh",
                "duong soi",
                "duong da",
                "off road",
                "off-road",
                "diverge"
        )) {
            return ProductGroup.GRAVEL;
        }

        if (containsAny(
                normalizedSearchSpace,
                "vintage",
                "classic",
                "co dien",
                "retro",
                "peugeot",
                "phuc dung",
                "steel",
                "khung thep"
        )) {
            return ProductGroup.VINTAGE;
        }

        if (containsAny(
                normalizedSearchSpace,
                "road",
                "duong truong",
                "endurance",
                "race",
                "racing",
                "defy",
                "aero",
                "drop bar"
        )) {
            return ProductGroup.ROAD;
        }

        if (containsAny(
                normalizedSearchSpace,
                "city",
                "di pho",
                "hybrid",
                "commute",
                "commuter",
                "di lai",
                "hang ngay",
                "di lam",
                "thanh pho",
                "fitness",
                "fx"
        )) {
            return ProductGroup.CITY;
        }

        return ProductGroup.UNKNOWN;
    }

    private String buildGroupingSearchSpace(Product product) {
        StringBuilder builder = new StringBuilder()
                .append(safe(product.getId())).append(' ')
                .append(safe(product.getTitle())).append(' ')
                .append(safe(product.getDescription())).append(' ')
                .append(safe(product.getBadge())).append(' ')
                .append(safe(product.getCondition())).append(' ')
                .append(safe(product.getGroupset())).append(' ');

        // Remote tagline is synthesized from generic copy, so it is too noisy for grouping.
        if (!product.isRemoteSource()) {
            builder.append(safe(product.getTagline())).append(' ');
        }

        return normalizeForSearch(builder.toString());
    }

    private boolean matchesSearch(Product product) {
        if (searchQuery.isEmpty()) {
            return true;
        }

        String searchSpace = (
                safe(product.getTitle()) + " "
                        + safe(product.getTagline()) + " "
                        + safe(product.getDescription()) + " "
                        + safe(product.getLocation()) + " "
                        + safe(product.getCondition())
        ).toLowerCase(Locale.ROOT);
        return normalizeForSearch(searchSpace).contains(normalizeForSearch(searchQuery));
    }

    private boolean containsAny(String searchSpace, String... keywords) {
        for (String keyword : keywords) {
            if (searchSpace.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private void clearFilters() {
        chipGroup.clearCheck();
        selectedFilter = null;
        inputSearch.setText("");
        searchQuery = "";
        renderProductFeed();
    }

    private void updateResultsSummary(int filteredCount, int totalCount) {
        if (selectedFilter == null && searchQuery.isEmpty()) {
            textResultsSummary.setText(getString(R.string.home_results_summary_all, totalCount));
            return;
        }
        if (selectedFilter != null && includedUngroupedProducts) {
            textResultsSummary.setText(getString(R.string.home_results_summary_soft_filter, filteredCount, totalCount));
            return;
        }
        textResultsSummary.setText(getString(R.string.home_results_summary_filtered, filteredCount, totalCount));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String normalizeForSearch(String value) {
        String normalized = Normalizer.normalize(safe(value), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase(Locale.ROOT);
    }

    private void openProductDetail(Product product) {
        startActivity(ProductDetailActivity.createIntent(requireContext(), product));
    }
}
