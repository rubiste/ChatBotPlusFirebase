package com.example.chatbotpsp.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbotpsp.MainActivity;
import com.example.chatbotpsp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    //VISTAS
    private Button btLogin;
    private TextInputEditText etCorreo, etPassword;
    private TextView tvRegistrarse;
    private CheckBox cbRecordar;

    //VARIABLES FINALES
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String CORREO = "correo";
    private static final String PASSWORD = "password";
    private static final String RECORDAR = "recordar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
    }

    //Inicializacion de los componentes (Vistas)
    private void initComponents() {
        btLogin = findViewById(R.id.btLogin);
        etCorreo = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvRegistrarse = findViewById(R.id.tvRegistrarLogin);
        cbRecordar = findViewById(R.id.cbRecordar);

        initSharedPreferences();
        initFirebase();
        initEvents();
    }

    private void initSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String correo = sharedPref.getString(CORREO, "");
        String password = sharedPref.getString(PASSWORD, "");
        String remember = sharedPref.getString(RECORDAR, "");

        if(remember.compareTo("Checked") == 0){
            cbRecordar.setChecked(true);
        }
        etCorreo.setText(correo);
        etPassword.setText(password);
    }

    //Inicializacion de los componentes Firebase
    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Inicializacion del evento del boton Login y textView
    private void initEvents() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        tvRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrarActivity.class);
                startActivity(i);
            }
        });
    }

    //Hace el proceso de logueo completo
    private void checkLogin(){
        if (checkEmptyFields()){
            Toast.makeText(LoginActivity.this, getResources().getText(R.string.emptyFields),
                    Toast.LENGTH_LONG).show();
        }else{
            String username = etCorreo.getText().toString();
            String password = etPassword.getText().toString();
            firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        checkRemember();
                        Intent i = new Intent (LoginActivity.this, MainActivity.class);
                        startActivity(i);
                    }else{
                        Toast.makeText(LoginActivity.this, getResources().getText(R.string.noMatches), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkRemember() {
        boolean checked = false;
        if(cbRecordar.isChecked()){
            checked = true;
        }

        writeShareds(checked);
    }

    private void writeShareds(boolean checked) {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(checked) {
            editor.putString(CORREO, etCorreo.getText().toString());
            editor.putString(PASSWORD, etPassword.getText().toString());
            editor.putString(RECORDAR, "Checked");
        }else{
            editor.putString(CORREO, "");
            editor.putString(PASSWORD, "");
            editor.putString(RECORDAR, "Unchecked");
        }

        editor.apply();
    }

    //Metodo que comprueba si los campos estan vacios
    private boolean checkEmptyFields() {
        boolean empty = false;
        if (etCorreo.getText().toString().equalsIgnoreCase("") ||
                etPassword.getText().toString().equalsIgnoreCase("")){
            empty = true;
        }

        return empty;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle guardarEstado) {
        super.onSaveInstanceState(guardarEstado);
        guardarEstado.putString("correo", etCorreo.getText().toString());
        guardarEstado.putString("password", etPassword.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etCorreo.setText(savedInstanceState.getString("correo"));
        etPassword.setText(savedInstanceState.getString("password"));
    }
}
