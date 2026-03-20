package com.example.mobile_obs_asm.ui.seller;

import android.content.Intent;
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

import com.example.mobile_obs_asm.CreateListingActivity;
import com.example.mobile_obs_asm.LoginActivity;
import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SellerProductRemoteRepository;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.model.SellerListing;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Collections;
import java.util.List;

public class SellerListingsFragment extends Fragment {

    private SellerListingAdapter adapter;
    private RecyclerView recyclerView;
    private SectionStateController stateController;
    private SellerProductRemoteRepository sellerProductRemoteRepository;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean allowResumeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_listings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sellerProductRemoteRepository = new SellerProductRemoteRepository(requireContext());
        recyclerView = view.findViewById(R.id.recyclerSellerListings);
        stateController = new SectionStateController(view, R.id.layoutSellerListingsState);
        swipeRefreshLayout = view.findViewById(R.id.swipeSellerListingsRefresh);
        MaterialButton buttonCreate = view.findViewById(R.id.buttonSellerCreateListing);
        buttonCreate.setOnClickListener(v -> openCreateListing());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new SellerListingAdapter(Collections.emptyList(), new SellerListingAdapter.OnSellerListingActionListener() {
            @Override
            public void onPrimaryAction(SellerListing listing) {
                handlePrimaryAction(listing);
            }

            @Override
            public void onEditAction(SellerListing listing) {
                openEditListing(listing);
            }

            @Override
            public void onDeleteAction(SellerListing listing) {
                confirmDelete(listing);
            }
        });
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this::loadListings);

        loadListings();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allowResumeRefresh && isAdded() && SessionManager.getInstance(requireContext()).isSellerSession()) {
            loadListings();
        }
        allowResumeRefresh = true;
    }

    private void loadListings() {
        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        if (!sessionManager.hasActiveSession()) {
            swipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            stateController.showMessage(
                    getString(R.string.state_seller_listings_signed_out_title),
                    getString(R.string.state_seller_listings_signed_out_message),
                    getString(R.string.state_action_sign_in),
                    actionView -> openSignIn()
            );
            return;
        }

        if (!sessionManager.isSellerSession()) {
            swipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            stateController.showMessage(
                    getString(R.string.state_seller_only_title),
                    getString(R.string.state_seller_only_message)
            );
            return;
        }

        adapter.replaceListings(Collections.emptyList());
        recyclerView.setVisibility(View.GONE);
        stateController.showLoading(
                getString(R.string.state_seller_listings_loading_title),
                getString(R.string.state_seller_listings_loading_message)
        );

        sellerProductRemoteRepository.fetchMyProducts(new RepositoryCallback<List<SellerListing>>() {
            @Override
            public void onSuccess(List<SellerListing> value) {
                if (!isAdded()) {
                    return;
                }
                swipeRefreshLayout.setRefreshing(false);

                adapter.replaceListings(value);
                if (value.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    stateController.showMessage(
                            getString(R.string.state_seller_listings_empty_title),
                            getString(R.string.state_seller_listings_empty_message),
                            getString(R.string.seller_listing_create_button),
                            actionView -> openCreateListing()
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
                        getString(R.string.state_seller_listings_error_title),
                        message,
                        getString(R.string.state_action_retry),
                        actionView -> loadListings()
                );
            }
        });
    }

    private void handlePrimaryAction(SellerListing listing) {
        if (listing.isLockedForTransaction()) {
            Toast.makeText(requireContext(), R.string.seller_listing_locked_message, Toast.LENGTH_LONG).show();
            return;
        }

        stateController.showLoading(
                getString(R.string.seller_listing_action_loading_title),
                listing.isHidden()
                        ? getString(R.string.seller_listing_action_loading_show)
                        : getString(R.string.seller_listing_action_loading_hide)
        );

        RepositoryCallback<SellerListing> callback = new RepositoryCallback<SellerListing>() {
            @Override
            public void onSuccess(SellerListing value) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(
                        requireContext(),
                        listing.isHidden()
                                ? R.string.seller_listing_action_show_success
                                : R.string.seller_listing_action_hide_success,
                        Toast.LENGTH_SHORT
                ).show();
                loadListings();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (!isAdded()) {
                    return;
                }
                stateController.showMessage(
                        getString(R.string.state_seller_listings_error_title),
                        message,
                        getString(R.string.state_action_retry),
                        actionView -> handlePrimaryAction(listing)
                );
            }
        };

        if (listing.isHidden()) {
            sellerProductRemoteRepository.showProduct(listing.getId(), callback);
            return;
        }
        sellerProductRemoteRepository.hideProduct(listing.getId(), callback);
    }

    private void confirmDelete(SellerListing listing) {
        if (listing.isLockedForTransaction()) {
            Toast.makeText(requireContext(), R.string.seller_listing_locked_message, Toast.LENGTH_LONG).show();
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.seller_listing_delete_confirm_title)
                .setMessage(getString(R.string.seller_listing_delete_confirm_message, listing.getTitle()))
                .setNegativeButton(R.string.seller_listing_delete_confirm_cancel, null)
                .setPositiveButton(R.string.seller_listing_delete_confirm_accept, (dialog, which) -> deleteListing(listing))
                .show();
    }

    private void deleteListing(SellerListing listing) {
        stateController.showLoading(
                getString(R.string.seller_listing_delete_loading_title),
                getString(R.string.seller_listing_delete_loading_message)
        );

        sellerProductRemoteRepository.deleteProduct(listing.getId(), new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void value) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), R.string.seller_listing_delete_success, Toast.LENGTH_SHORT).show();
                loadListings();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (!isAdded()) {
                    return;
                }
                stateController.showMessage(
                        getString(R.string.state_seller_listings_error_title),
                        message,
                        getString(R.string.state_action_retry),
                        actionView -> deleteListing(listing)
                );
            }
        });
    }

    private void openCreateListing() {
        startActivity(CreateListingActivity.createIntent(requireContext()));
    }

    private void openEditListing(SellerListing listing) {
        if (listing.isLockedForTransaction() || listing.isSold()) {
            Toast.makeText(requireContext(), R.string.seller_listing_locked_message, Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(CreateListingActivity.createEditIntent(requireContext(), listing.getId()));
    }

    private void openSignIn() {
        startActivity(LoginActivity.createIntent(requireContext(), null));
        requireActivity().finish();
    }
}
