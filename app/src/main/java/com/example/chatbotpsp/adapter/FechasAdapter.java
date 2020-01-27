package com.example.chatbotpsp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbotpsp.R;

import java.util.ArrayList;
import java.util.List;

public class FechasAdapter extends RecyclerView.Adapter<FechasAdapter.ViewHolder> implements Filterable {

    private List<String> fechas = new ArrayList<>(), listaAux;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public FechasAdapter (Context context, OnItemClickListener listener){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(String fecha);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_fechas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String fecha = fechas.get(position);
        holder.tvNumFecha.setText(fecha);

        holder.cvFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaAux = deformatFecha(fecha);
                listener.onItemClick(fechaAux);
            }
        });
    }

    @Override
    public int getItemCount() {
        int elementos = 0;
        if (fechas != null){
            elementos = fechas.size();
        }
        return elementos;
    }

    private Filter fechasFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(listaAux);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(String fecha : listaAux){
                    if (fecha.toLowerCase().contains(filterPattern)){
                        filteredList.add(fecha);
                    }
                }
            }
            FilterResults results =  new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            fechas.clear();
            fechas.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return fechasFilter;
    }

    public void setFechas (List<String> misFechas){
        this.fechas = misFechas;
        listaAux = new ArrayList<>(this.fechas);
        notifyDataSetChanged();
    }

    private String deformatFecha(String fecha) {
        String fechaNoEstilizada;
        fechaNoEstilizada = fecha.substring(6,10);
        fechaNoEstilizada += fecha.substring(3,5);
        fechaNoEstilizada += fecha.substring(0,2);
        return fechaNoEstilizada;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumFecha;
        private CardView cvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumFecha = itemView.findViewById(R.id.tvNumFecha);
            cvFecha = itemView.findViewById(R.id.cvFechas);
        }
    }
}
