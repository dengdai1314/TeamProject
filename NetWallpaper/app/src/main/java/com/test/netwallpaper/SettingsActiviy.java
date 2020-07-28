package com.test.netwallpaper;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.annotation.Nullable;

/**
 * @author DoubleTick
 * @description
 * @date 2020/7/27
 */
public class SettingsActiviy extends PreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_layout);
    }
}
