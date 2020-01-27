package com.example.chatbotpsp.objects;

import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.Snapshot;

import com.example.chatbotpsp.adapter.AdapterMultiType;
import com.example.chatbotpsp.objects.FirebaseMsgData;
import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

public class MyFirebase {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String userUID, ruta;

    private static final String TAG ="xyz";

    public MyFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userUID = firebaseUser.getUid();
        ruta = "user/"+userUID;
        databaseReference = firebaseDatabase.getReference(ruta);
    }

    public void saveInFirebase(FirebaseMsgData msgData){
        Map<String, Object> map = new HashMap<>();
        String key = databaseReference.child(ruta).push().getKey();
        String fecha = getFecha();
        map.put(fecha+"/" + key, msgData.toMap());
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("XYZ", "Se han subido los datos a Firebase");
                } else {
                    Log.d("XYZ", "Error al subir los datos a Firebase");
                }
            }
        });
    }

    public ArrayList<String> getFechasFromFirebase(DataSnapshot dataSnapshot){
        ArrayList<String> fechas = new ArrayList<>();
        for (DataSnapshot ds: dataSnapshot.getChildren()){
            fechas.add(stylizeDate(ds.getKey()));
        }
        return fechas;
    }

    private String stylizeDate(String key) {
        String finalDate;
        finalDate = key.substring(6,8)+"/";
        finalDate += key.substring(4,6)+"/";
        finalDate += key.substring(0,4);
        return finalDate;
    }

    public ArrayList<HashMap<String, Object>> getData(DataSnapshot dataSnapshot) {
        ArrayList<HashMap<String, Object>> myMsgs = new ArrayList<>();
        int cont = 0;

        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
        while (items.hasNext()){
            HashMap<String, Object> auxMap;
            DataSnapshot item = items.next();
            auxMap = (HashMap<String, Object>) item.getValue();
            myMsgs.add(cont, auxMap);
            cont++;
        }

        return myMsgs;
    }

    public String getFecha() {
        Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }

    //Getters y Setters
    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReferenceFecha(String fecha) {
        this.databaseReference = firebaseDatabase.getReference(ruta+"/"+fecha);
    }
}
