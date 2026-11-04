package com.example.lab6;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.lab6.databinding.ActivityMainBinding;
import com.example.lab6.dtos.Sesion;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ActivityMainBinding binding;
    ListenerRegistration snapshotListener;

    private static final int TOAST_DURATION = Toast.LENGTH_LONG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Si no hay sesion, volver al login
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        db = FirebaseFirestore.getInstance();

        binding.btnTiempoReal.setOnClickListener(view -> {

            snapshotListener = db.collection("usuarios")
                    .limit(6)
                    .addSnapshotListener((collection, error) -> {

                        if (error != null) {
                            Log.w("msg-test", "Listen failed.", error);
                            return;
                        }

                        Log.d("msg-test", "---- Datos en tiempo real ----");

                        String resultado ="";
                        for (QueryDocumentSnapshot doc : collection) {
                            Sesion sesion = doc.toObject(Sesion.class);
                            resultado += "id: " + doc.getId() +
                                    " | Curso: " + sesion.getCurso() +
                                    " | Fecha: " + sesion.getFecha() +
                                    " | Horas: " + sesion.getHoras() +
                                    " | Tipo: " + sesion.getTipo() + "\n" +
                                    "-----------------------------------------\n";
                        }
                        mostrarResultado(resultado);
                    });
        });
    }
}