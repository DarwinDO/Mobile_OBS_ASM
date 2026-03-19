package com.example.mobile_obs_asm.ui.orders;

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
import com.example.mobile_obs_asm.MainActivity;
import com.example.mobile_obs_asm.OrderDetailActivity;
import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.data.FakeMarketplaceRepository;
import com.example.mobile_obs_asm.data.OrderRemoteRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.model.OrderPreview;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class OrdersFragment extends Fragment {

    private OrderAdapter adapter;
    private RecyclerView recyclerView;
    private SectionStateController stateController;
    private OrderRemoteRepository orderRemoteRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderRemoteRepository = new OrderRemoteRepository(requireContext());
        recyclerView = view.findViewById(R.id.recyclerOrders);
        stateController = new SectionStateController(view, R.id.layoutOrdersState);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new OrderAdapter(
                FakeMarketplaceRepository.getInstance().getOrderPreviews(),
                order -> startActivity(OrderDetailActivity.createIntent(requireContext(), order))
        );
        recyclerView.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        if (!SessionManager.getInstance(requireContext()).hasActiveSession()) {
            List<OrderPreview> demoOrders = FakeMarketplaceRepository.getInstance().getOrderPreviews();
            adapter.replaceOrders(demoOrders);
            recyclerView.setVisibility(demoOrders.isEmpty() ? View.GONE : View.VISIBLE);
            stateController.showMessage(
                    getString(R.string.state_orders_demo_title),
                    getString(R.string.state_orders_demo_message),
                    getString(R.string.state_action_sign_in),
                    actionView -> openSignIn()
            );
            return;
        }

        adapter.replaceOrders(java.util.Collections.emptyList());
        recyclerView.setVisibility(View.GONE);
        stateController.showLoading(
                getString(R.string.state_orders_loading_title),
                getString(R.string.state_orders_loading_message)
        );

        orderRemoteRepository.fetchMyOrders(new RepositoryCallback<List<OrderPreview>>() {
            @Override
            public void onSuccess(List<OrderPreview> value) {
                if (!isAdded()) {
                    return;
                }
                adapter.replaceOrders(value);
                if (value.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    stateController.showMessage(
                            getString(R.string.state_orders_empty_title),
                            getString(R.string.state_orders_empty_message),
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
                recyclerView.setVisibility(View.GONE);
                stateController.showMessage(
                        getString(R.string.state_orders_error_title),
                        message,
                        getString(R.string.state_action_retry),
                        actionView -> loadOrders()
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
}
