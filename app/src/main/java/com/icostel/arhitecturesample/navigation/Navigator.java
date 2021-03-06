package com.icostel.arhitecturesample.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;


public interface Navigator {
    void navigateTo(@Nullable NavigationAction navigationAction);
    @IdRes int getFragmentContainer();

    FragmentManager getSupportFragmentManager();
    void startActivity(Intent intent);
    void startActivity(Intent intent, Bundle options);
    void startActivityForResult(Intent intent, int requestCode);
    void finish();
    Context getContext();
    void setResult(int resultCode, Intent data);
}
