package com.example.chatbotpsp.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatbotpsp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrarActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private Button btRegister;
    private TextInputEditText etCorreo, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        initComponents();
    }

    //Inicializacion de los componentes (Vistas)
    private void initComponents() {
        btRegister = findViewById(R.id.btRegister);
        etCorreo = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        initFirebase();
        initEvents();
    }

    //Inicializacion de los componentes Firebase
    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Inicializacion del evento del boton Register
    private void initEvents() {
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
    }

    //Checkea y realiza el proceso de registro completo, tanto comprobar que no exista el usuario, como crearlo
    private void checkLogin(){
        if (checkEmptyFields()){
            Toast.makeText(RegistrarActivity.this, getResources().getText(R.string.emptyFields),
                    Toast.LENGTH_LONG).show();
        }else{
            if(!isEmailValid()){
                Toast.makeText(this, getResources().getText(R.string.introducirCorreo), Toast.LENGTH_LONG).show();
            }else{
                crearCuenta();
            }
        }
    }

    private boolean isEmailValid() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(etCorreo.getText().toString()).matches();
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

    //Metodo que crea una nueva cuenta y checkear si existe el corre ya
    private void crearCuenta() {
        String username = etCorreo.getText().toString();
        String password = etPassword.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistrarActivity.this, getResources().getText(R.string.cuentaCreada), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(RegistrarActivity.this, getResources().getText(R.string.yaExiste), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
