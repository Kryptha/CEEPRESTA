package com.example.ceepresta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import static BaseDeDatos.Firebase.signInAccount;

public class InicioSesion_Activity extends AppCompatActivity {

    //Declaración de variables
    private EditText email,password;
    private Button login_normal;
    private ArrayList<String> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciosesion);

        //Toma de datos en pantalla
        email = findViewById(R.id.edittext_username);
        password = findViewById(R.id.edittext_password);
        login_normal = findViewById(R.id.btn_iniciosesion);

        // Click en el botón de inicio de sesión normal (no-google)
        login_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Es necesario que al momento de utilizar un servicios de firebase utilizar un hilo.
                new Thread(() -> {

                    boolean existe = false; //Mientras no inicie sesión el usuario no existe
                    try {
                        //Se utiliza el servicio "signInAccount" de firebase para iniciar sesion (Devuelve true o false, dependiendo si esta registrado o no)
                        result = signInAccount(email.getText().toString(),password.getText().toString());
                        /*La posición 0 de result es si existe o no el usuario y la posición 1 es la UID*/
                        existe = Boolean.parseBoolean(result.get(0));
                    } catch (Exception e) {
                        //Ante cualquier excepción muestra el mensaje de seguimiento
                        e.printStackTrace();
                    }
                    //En caso de que existiera el usuaro entonces es true
                    boolean finalExiste = existe;
                    //Se utiliza un hilo para mostrar más eficientemente un mensaje en pantalla
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(finalExiste){ //En caso de que exista, se inicia sesión satisfactoriamente
                                Toast.makeText(getApplicationContext(),"Inicio sesión satisfactorio",Toast.LENGTH_SHORT).show();
                                //Se inicia la actividad principal del software
                                Intent i = new Intent(getApplicationContext(),Principal_Activity.class);
                                i.putExtra("UID", result.get(1));
                                startActivity(i);
                                //finish();
                                return;
                            }
                            else{
                                //De caso contrario se avisa al usuario.
                                Toast.makeText(getApplicationContext(),"Email o password son incorrectos",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }).start();

            }
        });

    }
}