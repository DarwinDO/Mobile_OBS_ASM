package com.example.mobile_obs_asm.ui.wishlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_obs_asm.LoginActivity;
import com.example.mobile_obs_asm.ProductDetailActivity;
import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.data.FakeMarketplaceRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.data.WishlistRemoteRepository;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.example.mobile_obs_asm.ui.home.ProductAdapter;

import java.util.List;

public class WishlistFragment extends Fragment {

    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    private SectionStateController stateController;
    private WishlistRemoteRepository wishlistRemoteRepository;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new ProductAdapter(
                FakeMarketplaceRepository.getInstance().getWishlistProducts(),
                product -> startActivity(ProductDetailActivity.createIntent(requireContext(), product))
        );
        recyclerView.setAdapter(adapter);

        loadWishlist();
    }

    private void loadWishlist() {
        if (!SessionManager.getInstance(requireContext()).hasActiveSession()) {
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
                adapter.replaceProducts(value);
                if (value.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    stateController.showMessage(
                            getString(R.string.state_wishlist_empty_title),
                            getString(R.string.state_wishlist_empty_message)
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
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }
}
