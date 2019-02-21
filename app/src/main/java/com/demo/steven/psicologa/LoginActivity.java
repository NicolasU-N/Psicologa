package com.demo.steven.psicologa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText textEmail;
    private EditText textPassword;
    private Button btnRegistrar;
    private Button btnIngresar;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Referenciamos los views
        textEmail = (EditText) findViewById(R.id.txt_email);
        textPassword = (EditText) findViewById(R.id.txt_password);

        btnRegistrar = (Button) findViewById(R.id.btn_registrar);
        btnIngresar = (Button) findViewById(R.id.btn_ingresar);

        progressDialog = new ProgressDialog(this);

        //Añadiendo listeners a los botones
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loguearUsuario();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioActual = firebaseAuth.getCurrentUser();
        actualizarUsuario(usuarioActual);
    }

    private void actualizarUsuario(FirebaseUser usuarioActual) {
        if (usuarioActual != null) {
            if(usuarioActual.isEmailVerified()){
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();

            }else {
                Toast.makeText(LoginActivity.this, "Debe Verificar su email", Toast.LENGTH_SHORT).show();
            }
        }

    }



    private void registrarUsuario(){

        //Obtenemos el email y la contraseña desde las cajas de texto
        String email = textEmail.getText().toString().trim();
        String password  = textPassword.getText().toString().trim();

        //Verificamos que las cajas de texto no esten vacías
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Falta ingresar la contraseña",Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Realizando registro en linea...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){

                            Toast.makeText(LoginActivity.this,"Se ha registrado el usuario con el email: "+ textEmail.getText(),Toast.LENGTH_LONG).show();

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()==true)
                                        Toast.makeText(LoginActivity.this, "Email Enaviado", Toast.LENGTH_SHORT).show();

                                    else
                                        Toast.makeText(LoginActivity.this, "Email Enaviado", Toast.LENGTH_SHORT).show();

                                }
                            });
                            actualizarUsuario(user);

                        }else{
                            actualizarUsuario(null);
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si se presenta una colisión
                                Toast.makeText(LoginActivity.this, "Ese usuario ya existe ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();

                    }
                });

    }


    private void loguearUsuario() {
        //Obtenemos el email y la contraseña desde las cajas de texto
        final String email = textEmail.getText().toString().trim();
        String password  = textPassword.getText().toString().trim();

        //Verificamos que las cajas de texto no esten vacías
        if (TextUtils.isEmpty(email)) {//(precio.equals(""))
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Falta ingresar la contraseña", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Realizando consulta en linea...");
        progressDialog.show();

        //loguear usuario
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            int pos = email.indexOf("@");
                            String user = email.substring(0, pos);
                            Toast.makeText(LoginActivity.this, "Bienvenido: " + textEmail.getText(), Toast.LENGTH_LONG).show();
                            Intent intencion = new Intent(getApplication(), MainActivity.class);
                            intencion.putExtra(MainActivity.user, user);
                            startActivity(intencion);

                            FirebaseUser usera = firebaseAuth.getCurrentUser();
                            actualizarUsuario(usera);


                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si se presenta una colisión
                                Toast.makeText(LoginActivity.this, "Ese usuario ya existe ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });


    }

}

