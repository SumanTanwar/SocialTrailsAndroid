package com.example.socialtrailsapp.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.socialtrailsapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String getCurrentDatetime() {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return datetimeFormat.format(new Date());
    }
    public static void togglePasswordVisibility(EditText editText, ImageView imageView) {
        if (editText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            // Show Password
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageView.setImageResource(R.drawable.hidden);
        } else {
            // Hide Password
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imageView.setImageResource(R.drawable.eye);
        }
        // Move the cursor to the end of the text
        editText.setSelection(editText.getText().length());
    }
    public static boolean isValidPassword(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLetter(password.charAt(i))) {
                hasLetter = true;
            } else if (Character.isDigit(password.charAt(i))) {
                hasDigit = true;
            }
            if (hasLetter && hasDigit) {
                return true;
            }
        }
        return false;
    }
    public static void saveCredentials(Context context, String email, boolean rememberMe) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.putBoolean("rememberMe", rememberMe);
        editor.apply();
    }
    public static String getRelativeTime(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = format.parse(dateString);
            if (date != null) {
                long currentTime = System.currentTimeMillis();
                long postTime = date.getTime();
                long diff = currentTime - postTime;

                if (diff < DateUtils.MINUTE_IN_MILLIS) {
                    return "posted recently";
                } else if (diff < DateUtils.DAY_IN_MILLIS) {
                    return DateUtils.getRelativeTimeSpanString(postTime, currentTime, DateUtils.MINUTE_IN_MILLIS).toString();
                } else {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
                    return "on " + outputFormat.format(date);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
