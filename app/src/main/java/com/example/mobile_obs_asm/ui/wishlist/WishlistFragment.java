package com.example.mobile_obs_asm.ui.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mobile_obs_asm.LoginActivity;
import com.example.mobile_obs_asm.MainActivity;
import com.example.mobile_obs_asm.ProductDetailActivity;
import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.data.FakeMarketplaceRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.data.WishlistRemoteRepository;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.example.mobile_obs_asm.ui.home.ProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class WishlistFragment extends Fragment {

    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    private SectionStateController stateController;
    private WishlistRemoteRepository wishlistRemoteRepository;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean allowResumeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wishlistRemoteRepository = new WishlistRemoteRepository(requireContext());
        recyclerView = view.findViewById(R.id.recyclerWishlistProducts);
        stateController = new SectionStateController(view, R.id.layoutWishlistState);
        swipeRefreshLayout = view.findViewById(R.id.swipeWishlistRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new ProductAdapter(
                FakeMarketplaceRepository.getInstance().getWishlistProducts(),
                product -> startActivity(ProductDetailActivity.createIntent(requireContext(), product)),
                R.string.product_action_remove,
                this::handleRemoveAction
        );
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this::loadWishlist);

        loadWishlist();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allowResumeRefresh) {
            loadWishlist();
        }
        allowResumeRefresh = true;
    }

    private void loadWishlist() {
        if (!SessionManager.getInstance(requireContext()).hasActiveSession()) {
            swipeRefreshLayout.setRefreshing(false);
            List<Product> demoProducts = FakeMarketplaceRepository.getInstance().getWishlistProducts();
            adapter.replaceProducts(demoProducts);
            recyclerView.setVisibility(demoProducts.isEmpty() ? View.GONE : View.VISIBLE);
            stateController.showMessage(
                    getString(R.string.state_wishlist_demo_title),
                    getString(R.string.state_wishlist_demo_message),
                    getString(R.string.state_action_sign_in),
                    actionView -> openSignIn()
            );
            return;
        }

        adapter.replaceProducts(java.util.Collections.emptyList());
        recyclerView.setVisibility(View.GONE);
        stateController.showLoading(
                getString(R.string.state_wishlist_loading_title),
                getString(R.string.state_wishlist_loading_message)
        );

        wishlistRemoteRepository.fetchWishlist(new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> value) {
                if (!isAdded()) {
                    return;
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.replaceProducts(value);
                if (value.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    stateController.showMessage(
                            getString(R.string.state_wishlist_empty_title),
                            getString(R.string.state_wishlist_empty_message),
                            getString(R.string.state_action_browse),
                            actionView -> navigateToMainSection(R.id.navigation_home)
                    );
                    return;
                }
                recyclerView.setVisibility(View.VISIBLE);
                stateController.hide();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (!isAdded()) {
                    return;
                }
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                stateController.showMessage(
                        getString(R.string.state_wishlist_error_title),
                        message,
                        getString(R.string.state_action_retry),
                        actionView -> loadWishlist()
                );
            }
        });
    }

    private void openSignIn() {
        startActivity(LoginActivity.createIntent(requireContext(), null));
        requireActivity().finish();
    }

    private void navigateToMainSection(int destinationId) {
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(destinationId);
            return;
        }
        startActivity(MainActivity.createIntent(requireContext(), destinationId));
    }

    private void handleRemoveAction(Product product) {
        if (!SessionManager.getInstance(requireContext()).hasActiveSession()) {
            FakeMarketplaceRepository.getInstance().removeWishlistProduct(product.getId());
            adapter.removeProduct(product);
            Toast.makeText(requireContext(), R.string.wishlist_remove_demo_toast, Toast.LENGTH_SHORT).show();
            updateWishlistStateAfterRemoval(true);
            return;
        }

        stateController.showLoading(
                getString(R.string.wishlist_remove_loading_title),
                getString(R.string.wishlist_remove_loading_message)
        );

        wishlistRemoteRepository.removeProduct(product.getId(), new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void value) {
                if (!isAdded()) {
                    return;
                }
                adapter.removeProduct(product);
                Toast.makeText(requireContext(), R.string.wishlist_remove_success_toast, Toast.LENGTH_SHORT).show();
                updateWishlistStateAfterRemoval(false);
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (!isAdded()) {
                    return;
                }
                stateController.showMessage(
                        getString(R.string.state_wishlist_error_title),
                        message,
                        getString(R.string.state_action_retry),
                        actionView -> handleRemoveAction(product)
                );
            }
        });
    }

    private void updateWishlistStateAfterRemoval(boolean demoMode) {
        swipeRefreshLayout.setRefreshing(false);
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            stateController.showMessage(
                    getString(R.string.state_wishlist_empty_title),
                    getString(R.string.state_wishlist_empty_message),
                    getString(R.string.state_action_browse),
                    actionView -> navigateToMainSection(R.id.navigation_home)
            );
            return;
        }

        recyclerView.setVisibility(View.VISIBLE);
        if (demoMode) {
            stateController.showMessage(
                    getString(R.string.state_wishlist_demo_title),
                    getString(R.string.state_wishlist_demo_message),
                    getString(R.string.state_action_sign_in),
                    actionView -> openSignIn()
            );
            return;
        }
        stateController.hide();
    }
}
