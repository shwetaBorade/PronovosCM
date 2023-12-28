package com.pronovoscm.fragments;

import static android.Manifest.permission.READ_MEDIA_IMAGES;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class BaseFragment extends Fragment {
    public static String getExternalPermission() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.WRITE_EXTERNAL_STORAGE;
        }
    }

    public static int checkSelfPermission(@NotNull Context context, @NotNull String permission) {
        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return ContextCompat.checkSelfPermission(context, getExternalPermission());
            default:
                return ContextCompat.checkSelfPermission(context, permission);
        }
    }
}
