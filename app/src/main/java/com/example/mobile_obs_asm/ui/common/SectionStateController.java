package com.example.mobile_obs_asm.ui.common;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.mobile_obs_asm.R;
import com.google.android.material.button.MaterialButton;

public class SectionStateController {

    private final View rootView;
    private final ProgressBar progressBar;
    private final TextView titleView;
    private final TextView messageView;
    private final MaterialButton actionButton;

    public SectionStateController(View parentView, int stateViewId) {
        rootView = parentView.findViewById(stateViewId);
        progressBar = rootView.findViewById(R.id.progressContentState);
        titleView = rootView.findViewById(R.id.textContentStateTitle);
        messageView = rootView.findViewById(R.id.textContentStateMessage);
        actionButton = rootView.findViewById(R.id.buttonContentStateAction);
    }

    public void showLoading(CharSequence title, CharSequence message) {
        rootView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        titleView.setText(title);
        messageView.setText(message);
        actionButton.setVisibility(View.GONE);
        actionButton.setOnClickListener(null);
    }

    public void showMessage(CharSequence title, CharSequence message) {
        showMessage(title, message, null, null);
    }

    public void showMessage(
            CharSequence title,
            CharSequence message,
            @Nullable CharSequence actionLabel,
            @Nullable View.OnClickListener actionListener
    ) {
        rootView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        titleView.setText(title);
        messageView.setText(message);

        if (actionLabel == null || actionListener == null) {
            actionButton.setVisibility(View.GONE);
            actionButton.setOnClickListener(null);
            return;
        }

        actionButton.setVisibility(View.VISIBLE);
        actionButton.setText(actionLabel);
        actionButton.setOnClickListener(actionListener);
    }

    public void hide() {
        rootView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        actionButton.setOnClickListener(null);
    }
}
