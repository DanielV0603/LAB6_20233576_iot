package com.example.lab6;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6.databinding.ActivityGraficosBinding;
import com.example.lab6.dtos.Sesion;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class GraficosActivity extends AppCompatActivity {

    ActivityGraficosBinding binding;
    FirebaseFirestore db;
    ListenerRegistration listener;
    FirebaseUser user;

    final List<Sesion> todas = new ArrayList<>();
    String cursoFiltro; // null = todos
    String textoTodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGraficosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        db = FirebaseFirestore.getInstance();
        textoTodos = getString(R.string.todos_los_cursos);

        binding.btnVolver.setOnClickListener(v -> finish());
        binding.actvCurso.setOnItemClickListener((parent, view, pos, id) -> {
            String sel = (String) parent.getItemAtPosition(pos);
            cursoFiltro = sel.equals(textoTodos) ? null : sel;
            dibujar();
        });

        configurarGraficos();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null) return;
        listener = db.collection("sesiones")
                .whereEqualTo("uid", user.getUid())
                .addSnapshotListener((snap, error) -> {
                    if (error != null || snap == null) return;
                    todas.clear();
                    todas.addAll(snap.toObjects(Sesion.class));
                    actualizarFiltro();
                    dibujar();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }

    private void actualizarFiltro() {
        TreeSet<String> cursos = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Sesion s : todas) if (s.getCurso() != null) cursos.add(s.getCurso());
        List<String> opciones = new ArrayList<>();
        opciones.add(textoTodos);
        opciones.addAll(cursos);

        // si el curso filtrado ya no existe (p. ej. se eliminó), volver a "todos"
        if (cursoFiltro != null && !cursos.contains(cursoFiltro)) cursoFiltro = null;

        binding.actvCurso.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, opciones));
        binding.actvCurso.setText(cursoFiltro == null ? textoTodos : cursoFiltro, false);
    }

    private void configurarGraficos() {
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.getLegend().setEnabled(false);
        binding.barChart.getAxisRight().setEnabled(false);
        binding.barChart.getAxisLeft().setAxisMinimum(0f);
        binding.barChart.setNoDataText(getString(R.string.sin_datos));
        XAxis x = binding.barChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setDrawGridLines(false);

        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.setNoDataText(getString(R.string.sin_datos));
        binding.pieChart.setEntryLabelColor(Color.BLACK);
    }

    private void dibujar() {
        // Horas por mes (ordenado cronológicamente) y por tipo, aplicando el filtro de curso
        TreeMap<String, Float> porMes = new TreeMap<>();            // clave yyyy-MM
        Map<String, Float> porTipo = new LinkedHashMap<>();
        SimpleDateFormat claveFmt = new SimpleDateFormat("yyyy-MM", Locale.US);

        for (Sesion s : todas) {
            if (cursoFiltro != null && !cursoFiltro.equalsIgnoreCase(s.getCurso())) continue;
            String mes = claveFmt.format(MainActivity.parse(s.getFecha()));
            porMes.merge(mes, (float) s.getHoras(), Float::sum);
            String tipo = s.getTipo() == null ? "Otro" : s.getTipo();
            porTipo.merge(tipo, (float) s.getHoras(), Float::sum);
        }

        // ---- Barras ----
        if (porMes.isEmpty()) {
            binding.barChart.clear();
            binding.pieChart.clear();
            binding.barChart.invalidate();
            binding.pieChart.invalidate();
            return;
        }
        List<BarEntry> barras = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Float> e : porMes.entrySet()) {
            barras.add(new BarEntry(i++, e.getValue()));
            String[] p = e.getKey().split("-"); // yyyy, MM
            etiquetas.add(p[1] + "/" + p[0]);
        }
        BarDataSet bds = new BarDataSet(barras, "Horas");
        bds.setColor(Color.parseColor("#1F4FA8"));
        bds.setValueTextSize(11f);
        binding.barChart.setData(new BarData(bds));
        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        binding.barChart.getXAxis().setLabelCount(etiquetas.size());
        binding.barChart.animateY(500);
        binding.barChart.invalidate();

        // ---- Torta ----
        List<PieEntry> porciones = new ArrayList<>();
        for (Map.Entry<String, Float> e : porTipo.entrySet())
            porciones.add(new PieEntry(e.getValue(), e.getKey()));
        PieDataSet pds = new PieDataSet(porciones, "");
        pds.setColors(Color.parseColor("#6C8FE0"), Color.parseColor("#F29B6B"),
                Color.parseColor("#6CC9A5"), Color.parseColor("#B0B6C3"));
        pds.setValueTextSize(13f);
        PieData pd = new PieData(pds);
        pd.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter(binding.pieChart));
        binding.pieChart.setData(pd);
        binding.pieChart.animateY(500);
        binding.pieChart.invalidate();
    }
}
