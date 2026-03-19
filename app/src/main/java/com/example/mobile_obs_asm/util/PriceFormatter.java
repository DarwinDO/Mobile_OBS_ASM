package com.example.mobile_obs_asm.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class PriceFormatter {

    private PriceFormatter() {
    }

    public static String formatCurrency(long amount) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        return currency.format(amount);
    }
}
