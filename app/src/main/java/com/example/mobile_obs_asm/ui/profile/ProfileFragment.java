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
import com.example.mobile_obs_asm.data.FakeMarketplaceRepository;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.model.UserProfile;
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

        UserProfile userProfile = SessionManager.getInstance(requireContext()).getStoredUserProfile();
        if (userProfile == null) {
            userProfile = FakeMarketplaceRepository.getInstance().getUserProfile();
        }

        TextView textProfileAvatar = view.findViewById(R.id.textProfileAvatar);
        TextView textProfileName = view.findViewById(R.id.textProfileName);
        TextView textProfileRole = view.findViewById(R.id.textProfileRole);
        TextView textProfileEmail = view.findViewById(R.id.textProfileEmail);
        TextView textTrustOrders = view.findViewById(R.id.textTrustOrders);
        TextView textTrustSaved = view.findViewById(R.id.textTrustSaved);
        TextView textTrustRole = view.findViewById(R.id.textTrustRole);
        MaterialButton buttonSignOut = view.findViewById(R.id.buttonSignOut);

        textProfileAvatar.setText(userProfile.getInitials());
        textProfileName.setText(userProfile.getName());
        textProfileRole.setText(userProfile.getRole() + " | " + userProfile.getCity());
        textProfileEmail.setText(userProfile.getEmail());
        textTrustOrders.setText(getString(R.string.trust_panel_orders) + ": " + userProfile.getCompletedOrders());
        textTrustSaved.setText(getString(R.string.trust_panel_saved) + ": " + userProfile.getSavedListings());
        textTrustRole.setText(getString(R.string.trust_panel_role) + ": " + userProfile.getRole());

        buttonSignOut.setOnClickListener(v -> {
            SessionManager.getInstance(requireContext()).clearSession();
            Toast.makeText(requireContext(), R.string.profile_sign_out_toast, Toast.LENGTH_SHORT).show();
            startActivity(new android.content.Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }
}
