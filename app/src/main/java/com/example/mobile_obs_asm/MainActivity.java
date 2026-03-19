package com.example.mobile_obs_asm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobile_obs_asm.ui.home.HomeFragment;
import com.example.mobile_obs_asm.ui.orders.OrdersFragment;
import com.example.mobile_obs_asm.ui.profile.ProfileFragment;
import com.example.mobile_obs_asm.ui.wishlist.WishlistFragment;
import com.example.mobile_obs_asm.util.SystemBarInsetsHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_START_DESTINATION = "extra_start_destination";

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    public static Intent createIntent(Context context, @IdRes int startDestination) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_START_DESTINATION, startDestination);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SystemBarInsetsHelper.applyToRoot(findViewById(R.id.main));

        toolbar = findViewById(R.id.toolbarMain);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            showDestination(item.getItemId());
            return true;
        });

        if (savedInstanceState == null) {
            int startDestination = getIntent().getIntExtra(EXTRA_START_DESTINATION, R.id.navigation_home);
            bottomNavigationView.setSelectedItemId(startDestination);
        } else {
            updateToolbar(bottomNavigationView.getSelectedItemId());
        }
    }

    private void showDestination(@IdRes int destinationId) {
        Fragment fragment;
        if (destinationId == R.id.navigation_wishlist) {
            fragment = new WishlistFragment();
        } else if (destinationId == R.id.navigation_orders) {
            fragment = new OrdersFragment();
        } else if (destinationId == R.id.navigation_profile) {
            fragment = new ProfileFragment();
        } else {
            fragment = new HomeFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        updateToolbar(destinationId);
    }

    private void updateToolbar(@IdRes int destinationId) {
        if (destinationId == R.id.navigation_wishlist) {
            toolbar.setTitle(R.string.toolbar_wishlist);
            toolbar.setSubtitle(R.string.toolbar_wishlist_subtitle);
        } else if (destinationId == R.id.navigation_orders) {
            toolbar.setTitle(R.string.toolbar_orders);
            toolbar.setSubtitle(R.string.toolbar_orders_subtitle);
        } else if (destinationId == R.id.navigation_profile) {
            toolbar.setTitle(R.string.toolbar_profile);
            toolbar.setSubtitle(R.string.toolbar_profile_subtitle);
        } else {
            toolbar.setTitle(R.string.toolbar_home);
            toolbar.setSubtitle(R.string.toolbar_home_subtitle);
        }
    }
}
