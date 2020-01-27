package com.example.chatbotpsp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.chatbotpsp.adapter.FechasAdapter;
import com.example.chatbotpsp.objects.MyFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {
    //Objeto firebase
    private MyFirebase myFirebase;

    //Recycler
    private RecyclerView rvFechas;
    private FechasAdapter fechasAdapter;
    private GridLayoutManager layoutManager;
    private SearchView svFechas;

    //Variables finales
    private static final String FECHA ="fechaConversacion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        initComponents();
    }

    private void initComponents() {
        initFirebase();

        //Vistas
        svFechas = findViewById(R.id.svFechas);
        svFechas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fechasAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fechasAdapter.getFilter().filter(newText);
                return false;
            }
        });

        //Recycler
        rvFechas = findViewById(R.id.rvFechas);
        fechasAdapter = new FechasAdapter(this, new FechasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String fecha) {
                Intent i = new Intent(HistorialActivity.this, ConversacionDiaActivity.class);
                i.putExtra(FECHA, fecha);
                startActivity(i);
            }
        });

        layoutManager = new GridLayoutManager(this, 3);
        rvFechas.setLayoutManager(layoutManager);
        rvFechas.setAdapter(fechasAdapter);
        rvFechas.setHasFixedSize(true);

        consultaDiasHablados();
    }

    private void initFirebase() {
        myFirebase = new MyFirebase();
    }

    //Va a ver todos los dias que un usuario ha hablado y los va a enviar al adapter
    private void consultaDiasHablados() {
        myFirebase.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> misFechas;
                misFechas = myFirebase.getFechasFromFirebase(dataSnapshot);
                addFechasRecycler(misFechas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Pasamos el ArrayList conseguido mediante la clase Firebase para añadirlo al Recycler
    private void addFechasRecycler(ArrayList<String> misFechas) {
        fechasAdapter.setFechas(misFechas);
    }

}
