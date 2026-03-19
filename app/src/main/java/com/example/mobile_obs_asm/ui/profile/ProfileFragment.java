package com.example.mobile_obs_asm.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile_obs_asm.LoginActivity;
import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.RegisterActivity;
import com.example.mobile_obs_asm.data.FakeMarketplaceRepository;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.model.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        boolean hasSession = sessionManager.hasActiveSession();
        boolean sellerSession = sessionManager.isSellerSession();
        UserProfile userProfile = hasSession ? sessionManager.getStoredUserProfile() : null;
        if (userProfile == null) {
            userProfile = hasSession ? FakeMarketplaceRepository.getInstance().getUserProfile() : buildGuestProfile();
        }

        TextView textProfileBadge = view.findViewById(R.id.textProfileBadge);
        TextView textProfileAvatar = view.findViewById(R.id.textProfileAvatar);
        TextView textProfileName = view.findViewById(R.id.textProfileName);
        TextView textProfileRole = view.findViewById(R.id.textProfileRole);
        TextView textProfileEmail = view.findViewById(R.id.textProfileEmail);
        TextView textProfileSubtitle = view.findViewById(R.id.textProfileSubtitle);
        TextView textProfileLocation = view.findViewById(R.id.textProfileLocation);
        TextView textTrustOrders = view.findViewById(R.id.textTrustOrders);
        TextView textTrustSaved = view.findViewById(R.id.textTrustSaved);
        TextView textTrustRole = view.findViewById(R.id.textTrustRole);
        MaterialButton buttonPrimary = view.findViewById(R.id.buttonProfilePrimaryAction);
        MaterialButton buttonSecondary = view.findViewById(R.id.buttonProfileSecondaryAction);
        MaterialButton buttonTertiary = view.findViewById(R.id.buttonProfileTertiaryAction);

        textProfileBadge.setText(hasSession ? R.string.profile_badge : R.string.profile_guest_badge);
        textProfileAvatar.setText(userProfile.getInitials());
        textProfileName.setText(userProfile.getName());
        textProfileRole.setText(userProfile.getRole());
        textProfileEmail.setText(userProfile.getEmail());
        textProfileSubtitle.setText(hasSession
                ? (sellerSession ? R.string.profile_seller_subtitle : R.string.profile_subtitle)
                : R.string.profile_guest_subtitle);
        textProfileLocation.setText(getString(R.string.profile_info_location_label) + ": " + userProfile.getCity());
        textTrustOrders.setText(getString(R.string.trust_panel_orders) + ": " + userProfile.getCompletedOrders());
        textTrustSaved.setText(getString(R.string.trust_panel_saved) + ": " + userProfile.getSavedListings());
        textTrustRole.setText(getString(R.string.trust_panel_role) + ": " + userProfile.getRole());

        bindActions(hasSession, sellerSession, buttonPrimary, buttonSecondary, buttonTertiary);
    }

    private void bindActions(
            boolean hasSession,
            boolean sellerSession,
            MaterialButton buttonPrimary,
            MaterialButton buttonSecondary,
            MaterialButton buttonTertiary
    ) {
        if (hasSession) {
            buttonPrimary.setText(sellerSession ? R.string.profile_action_my_listings : R.string.profile_action_orders);
            buttonPrimary.setOnClickListener(v -> navigateToSection(sellerSession ? R.id.navigation_wishlist : R.id.navigation_orders));

            buttonSecondary.setText(sellerSession ? R.string.profile_action_sales_orders : R.string.profile_action_wishlist);
            buttonSecondary.setOnClickListener(v -> navigateToSection(sellerSession ? R.id.navigation_orders : R.id.navigation_wishlist));

            buttonTertiary.setText(R.string.profile_sign_out);
            buttonTertiary.setOnClickListener(v -> {
                SessionManager.getInstance(requireContext()).clearSession();
                Toast.makeText(requireContext(), R.string.profile_sign_out_toast, Toast.LENGTH_SHORT).show();
                startActivity(LoginActivity.createIntent(requireContext(), null));
                requireActivity().finish();
            });
            return;
        }

        buttonPrimary.setText(R.string.profile_action_sign_in);
        buttonPrimary.setOnClickListener(v -> {
            startActivity(LoginActivity.createIntent(requireContext(), null));
            requireActivity().finish();
        });

        buttonSecondary.setText(R.string.profile_action_register);
        buttonSecondary.setOnClickListener(v -> startActivity(RegisterActivity.createIntent(requireContext())));

        buttonTertiary.setText(R.string.profile_action_explore);
        buttonTertiary.setOnClickListener(v -> navigateToSection(R.id.navigation_home));
    }

    private void navigateToSection(int destinationId) {
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(destinationId);
        }
    }

    private UserProfile buildGuestProfile() {
        return new UserProfile(
                getString(R.string.profile_guest_name),
                "Khách",
                getString(R.string.profile_guest_email),
                getString(R.string.profile_location_pending),
                0,
                0
        );
    }
}
