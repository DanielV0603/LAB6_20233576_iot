package com.example.lab6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Correos de Firebase (verificación / recuperación) en español
        FirebaseAuth.getInstance().setLanguageCode("es-419");

        // Si ya inició sesión, ir directo a la pantalla principal
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            irAMain();
            return;
        }

        // Layout personalizado del selector de métodos (logo + textos en español)
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_login_custom)
                .setGoogleButtonId(R.id.btnGoogle)
                .setEmailButtonId(R.id.btnEmail)
                .build();

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                .setAuthMethodPickerLayout(customLayout)
                // Evita que el registro se quede en pantalla gris al intentar guardar la
                // credencial en el Credential Manager (falla en emuladores sin cuenta Google)
                .setCredentialManagerEnabled(false)
                .build();

        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            irAMain();
        } else {
            // El usuario presionó "atrás" en el selector: volver a mostrarlo
            Toast.makeText(this, "Debes iniciar sesión para continuar", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void irAMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
