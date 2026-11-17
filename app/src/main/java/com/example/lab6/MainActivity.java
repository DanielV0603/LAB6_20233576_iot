package com.example.lab6;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab6.databinding.ActivityMainBinding;
import com.example.lab6.dtos.Sesion;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ActivityMainBinding binding;
    ListenerRegistration snapshotListener;
    SesionAdapter adapter;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        // Si no hay sesión, volver al login
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        adapter = new SesionAdapter(new SesionAdapter.Listener() {
            @Override
            public void onEditar(Sesion s) {
                Intent i = new Intent(MainActivity.this, RegistrarSesionActivity.class);
                i.putExtra("id", s.getId());
                i.putExtra("curso", s.getCurso());
                i.putExtra("fecha", s.getFecha());
                i.putExtra("horas", s.getHoras());
                i.putExtra("tipo", s.getTipo());
                startActivity(i);
            }

            @Override
            public void onEliminar(Sesion s) {
                confirmarEliminar(s);
            }
        });
        binding.rvSesiones.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSesiones.setAdapter(adapter);

        binding.fabAgregar.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrarSesionActivity.class)));
        binding.btnVerGraficos.setOnClickListener(v ->
                startActivity(new Intent(this, GraficosActivity.class)));
        binding.btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null) return;
        // Escucha en tiempo real: solo las sesiones del usuario autenticado
        snapshotListener = db.collection("sesiones")
                .whereEqualTo("uid", user.getUid())
                .addSnapshotListener((snap, error) -> {
                    if (error != null) {
                        Log.w("msg-test", "Listen failed.", error);
                        Toast.makeText(this, "No se pudieron cargar las sesiones", Toast.LENGTH_LONG).show();
                        return;
                    }
                    List<Sesion> lista = new ArrayList<>(snap.toObjects(Sesion.class));
                    Collections.sort(lista, (a, b) -> parse(b.getFecha()).compareTo(parse(a.getFecha())));
                    adapter.setItems(lista);
                    int n = lista.size();
                    binding.tvContador.setText(n + (n == 1 ? " sesión registrada" : " sesiones registradas"));
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (snapshotListener != null) {
            snapshotListener.remove();
            snapshotListener = null;
        }
    }

    private void confirmarEliminar(Sesion s) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar sesión")
                .setMessage("¿Deseas eliminar la sesión de " + s.getCurso() + " del " + s.getFecha() + "?")
                .setPositiveButton("Eliminar", (d, w) ->
                        db.collection("sesiones").document(s.getId()).delete()
                                .addOnSuccessListener(unused ->
                                        Toast.makeText(this, "Sesión eliminada", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_LONG).show()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cerrarSesion() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    static Date parse(String fecha) {
        try {
            Date d = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(fecha);
            return d != null ? d : new Date(0);
        } catch (ParseException | NullPointerException e) {
            return new Date(0);
        }
    }
}
