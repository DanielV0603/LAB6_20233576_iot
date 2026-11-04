package com.example.lab6;

import android.content.Intent;
import android.os.Bundle;
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
public class RegistrarSesionActivity extends AppCompatActivity {
    FirebaseFirestore db;
    ActivityMainBinding binding;
    ListenerRegistration snapshotListener;

    private static final int TOAST_DURATION = Toast.LENGTH_LONG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.btnRegistrarSesion.setOnClickListener(view -> {
            String curso = binding.textFieldCurso.getEditText().getText().toString();
            String fecha = binding.textFieldFecha.getEditText().getText().toString();
            String horasStr = binding.textFieldHoras.getEditText().getText().toString();
            String tipo = binding.textFieldTipo.getEditText().getText().toString();

            Sesion sesion = new Sesion(curso, fecha, Double.parseDouble(horasStr), tipo);

            db.collection("sesiones")
                    .document()
                    .set(sesion)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Sesion grabada", TOAST_DURATION).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Algo pasó al guardar ", TOAST_DURATION).show();
                    });
        }
}
