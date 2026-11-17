package com.example.lab6;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

/**
 * Fuerza el idioma español en toda la app, incluidas las pantallas de FirebaseUI
 * (iniciar sesión, registrarse, recuperar contraseña).
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("es"));
    }
}
