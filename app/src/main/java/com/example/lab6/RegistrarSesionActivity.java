package com.example.lab6;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6.databinding.ActivityRegistrarSesionBinding;
import com.example.lab6.dtos.Sesion;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Pantalla para agregar o editar una sesión de estudio (según llegue o no un "id"). */
public class RegistrarSesionActivity extends AppCompatActivity {

    private static final String[] TIPOS = {"Teoría", "Práctica", "Laboratorio"};

    FirebaseFirestore db;
    ActivityRegistrarSesionBinding binding;
    String idEdicion; // null = nueva sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrarSesionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        binding.actvTipo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, TIPOS));

        // Selector de fecha
        binding.etFecha.setOnClickListener(v -> mostrarCalendario());

        idEdicion = getIntent().getStringExtra("id");
        if (idEdicion != null) {
            binding.tvTitulo.setText(R.string.editar_sesion);
            binding.textFieldCurso.getEditText().setText(getIntent().getStringExtra("curso"));
            binding.etFecha.setText(getIntent().getStringExtra("fecha"));
            double h = getIntent().getDoubleExtra("horas", 0);
            binding.textFieldHoras.getEditText().setText(SesionAdapter.formatHoras(h));
            binding.actvTipo.setText(getIntent().getStringExtra("tipo"), false);
        } else {
            binding.etFecha.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date()));
        }

        binding.btnCancelar.setOnClickListener(v -> finish());
        binding.btnRegistrarSesion.setOnClickListener(v -> guardar());
    }

    private void mostrarCalendario() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona la fecha")
                .build();
        picker.addOnPositiveButtonClickListener(ms -> {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            f.setTimeZone(TimeZone.getTimeZone("UTC")); // el picker devuelve medianoche UTC
            binding.etFecha.setText(f.format(new Date(ms)));
        });
        picker.show(getSupportFragmentManager(), "fecha");
    }

    private void guardar() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Sesión expirada", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String curso = binding.textFieldCurso.getEditText().getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String horasStr = binding.textFieldHoras.getEditText().getText().toString().trim().replace(',', '.');
        String tipo = binding.actvTipo.getText().toString().trim();

        binding.textFieldCurso.setError(null);
        binding.textFieldHoras.setError(null);
        binding.textFieldTipo.setError(null);

        if (curso.isEmpty()) {
            binding.textFieldCurso.setError("Ingresa el curso");
            return;
        }
        double horas;
        try {
            horas = Double.parseDouble(horasStr);
        } catch (NumberFormatException e) {
            binding.textFieldHoras.setError("Ingresa un número válido");
            return;
        }
        if (horas <= 0 || horas > 24) {
            binding.textFieldHoras.setError("Las horas deben estar entre 0 y 24");
            return;
        }
        boolean tipoValido = false;
        for (String t : TIPOS) if (t.equals(tipo)) tipoValido = true;
        if (!tipoValido) {
            binding.textFieldTipo.setError("Selecciona un tipo de actividad");
            return;
        }

        Sesion sesion = new Sesion(user.getUid(), curso, fecha, horas, tipo);
        var tarea = (idEdicion == null)
                ? db.collection("sesiones").document().set(sesion)
                : db.collection("sesiones").document(idEdicion).set(sesion);

        tarea.addOnSuccessListener(unused -> {
            Toast.makeText(this, idEdicion == null ? "Sesión guardada" : "Sesión actualizada",
                    Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "No se pudo guardar la sesión", Toast.LENGTH_LONG).show());
    }
}
