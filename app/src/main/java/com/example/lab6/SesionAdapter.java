package com.example.lab6;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6.databinding.ItemSesionBinding;
import com.example.lab6.dtos.Sesion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.VH> {

    public interface Listener {
        void onEditar(Sesion sesion);

        void onEliminar(Sesion sesion);
    }

    private final List<Sesion> items = new ArrayList<>();
    private final Listener listener;

    public SesionAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Sesion> nuevos) {
        items.clear();
        items.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSesionBinding b = ItemSesionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Sesion s = items.get(position);
        h.b.tvCurso.setText(s.getCurso());
        h.b.tvFechaHoras.setText(s.getFecha() + " · " + formatHoras(s.getHoras()) + " h");
        h.b.tvTipo.setText(s.getTipo());

        int bg, tx;
        String tipo = s.getTipo() == null ? "" : s.getTipo();
        switch (tipo) {
            case "Práctica":
                bg = Color.parseColor("#FCE3D6");
                tx = Color.parseColor("#A8431A");
                break;
            case "Laboratorio":
                bg = Color.parseColor("#D5F0E6");
                tx = Color.parseColor("#1B6B4D");
                break;
            default:
                bg = Color.parseColor("#DCE6FA");
                tx = Color.parseColor("#1F4FA8");
        }
        h.b.tvTipo.getBackground().mutate().setTint(bg);
        h.b.tvTipo.setTextColor(tx);

        h.b.btnEditar.setOnClickListener(v -> listener.onEditar(s));
        h.b.btnEliminar.setOnClickListener(v -> listener.onEliminar(s));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static String formatHoras(double h) {
        if (h == Math.floor(h)) return String.format(Locale.US, "%d", (long) h);
        return String.format(Locale.US, "%.1f", h);
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemSesionBinding b;

        VH(ItemSesionBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
