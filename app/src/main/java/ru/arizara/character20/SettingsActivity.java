package ru.arizara.character20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences prefs;
    SettingsFragment settingsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.settings_activity);
        // установка слушателя на предпочтения
        prefs.registerOnSharedPreferenceChangeListener(this);
        // установка фрагмента SettingsFragment
        settingsFragment = new SettingsFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, settingsFragment)
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // метод устанавливающий тему активности
    public void setTheme() {
        // берем контекст приложения
        Context context = getApplicationContext();
        // получаем предпочтения всего приложения
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // берем значение цветовой темы
        String theme = prefs.getString("view", "standart");
        // берем значение включения режима темной темы
        boolean dark = prefs.getBoolean("dark_theme", false);
        // установка нужной цветовой схемы
        if (theme.equals("costom_theme")) {
            setTheme(R.style.Theme_CUSTOM_THEME);
        } else {
            setTheme(R.style.Theme_Character20);
        }
        // установка темной темы
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // если пользователь решил изменить цветовую схему, сообщаем об этом MainActivity
        if (key.equals("view")) {
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        }
        // если пользователь переключил темную тему
        if (key.equals("dark_theme")) {
            boolean theme = sharedPreferences.getBoolean("dark_theme", false);
            if (theme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}
