package com.example.moviebytes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * LoginPreference that implements SharedPreferences for accessing
 * the saved preferences in a file called "LOGIN_PREF" which will be
 * stored on the data/data/<package name>/shared_prefs
 *
 * To use this class call the
 * {@link LoginPreference#getInstance(Context)}
 * and pass in a Context of the app or an activity accessing it.
 *
 */
public class LoginPreference implements SharedPreferences {

    private final SharedPreferences pref;
    private static LoginPreference logPref;

    private LoginPreference(Context cont) {
        pref = cont.getSharedPreferences("LOGIN_PREF", Activity.MODE_PRIVATE);
    }

    public static LoginPreference getInstance(Context cont) {
        if(logPref == null){
            return new LoginPreference(cont);
        }
        return logPref;
    }

    public SharedPreferences getPref() {
        return pref;
    }

    public boolean clearPref() {
        return getPref().edit().clear().commit();
    }

    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return getPref().getString(key,defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return null;
    }

    @Override
    public int getInt(String key, int defValue) {
        return getPref().getInt(key,defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return getPref().getLong(key,defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return getPref().getFloat(key,defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return getPref().getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return getPref().contains(key);
    }

    @Override
    public Editor edit() {
        return pref.edit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
