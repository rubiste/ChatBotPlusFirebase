package com.example.chatbotpsp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.chatbotpsp.adapter.AdapterMultiType;
import com.example.chatbotpsp.objects.FirebaseMsgData;
import com.example.chatbotpsp.objects.MyFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConversacionDiaActivity extends AppCompatActivity {
    //Firebase
    private MyFirebase myFirebase;

    //Recycler
    private RecyclerView rvEspecifico;
    private AdapterMultiType adapter;

    //Otros
    private static final String FECHA ="fechaConversacion";
    private String diaElegido = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversacion_dia);

        initComponents();
    }

    private void initComponents() {
        diaElegido = getIntent().getStringExtra(FECHA);
        myFirebase = new MyFirebase();

        rvEspecifico = findViewById(R.id.rvEspecifico);
        adapter = new AdapterMultiType(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvEspecifico.setLayoutManager(layoutManager);
        rvEspecifico.setAdapter(adapter);

        getChat();
    }

    private void getChat() {
        myFirebase.setDatabaseReferenceFecha(diaElegido);
        myFirebase.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<HashMap<String, Object>> msgArray;
                msgArray = myFirebase.getData(dataSnapshot);
                adapter.setMensajes(msgArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
