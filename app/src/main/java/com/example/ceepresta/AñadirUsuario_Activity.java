package com.example.ceepresta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

import Clases.Prestatario;
import Clases.Usuario;

import static BaseDeDatos.Firebase.addDataUser;
import static BaseDeDatos.Firebase.addDateInventario;
import static BaseDeDatos.Firebase.addDatePrestatario;
import static BaseDeDatos.Firebase.signUpAccount;

public class AñadirUsuario_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //Declaración visual
    private EditText nombreUsuario, apellidoUsuario, correoUsuario, passwordUsuario;
    private Spinner generoUsuario, rolUsuario;
    private Button btn_save_usuario, btn_volver_usuario;
    //Saber en que carrera pertenece los datos
    private String inventarioID = "LCC";
    //Inicializar la selección de los spinners
    private String rolSeleccionado, generoSeleccionado;
    //Resultado al momento de registrar el usuario
    private ArrayList<String> resultRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_usuario);

        //Obtención de variable visuales
        nombreUsuario = findViewById(R.id.editxt_name_usuario);
        apellidoUsuario = findViewById(R.id.editxt_surname_usuario);
        correoUsuario = findViewById(R.id.editxt_correo_usuario);
        generoUsuario = findViewById(R.id.spinner_genero);
        rolUsuario = findViewById(R.id.spinner_rol);
        passwordUsuario = findViewById(R.id.editxt_password_usuario);

        //Obtención de botones
        btn_save_usuario = findViewById(R.id.btn_save_añadirUsuario);
        btn_volver_usuario = findViewById(R.id.btn_verUsuarios);

        //Adapatador para utilizar el spinner de Rol (values -> string.xml -> Rol)
        ArrayAdapter<CharSequence> adapterSpinnerRol = ArrayAdapter. createFromResource(this, R.array.rol, android.R.layout.simple_spinner_item);
        adapterSpinnerRol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolUsuario.setAdapter(adapterSpinnerRol);
        rolUsuario.setOnItemSelectedListener(this);

        //Adapatador para utilizar el spinner de genero  (values -> string.xml -> genero)
        ArrayAdapter<CharSequence> adapterSpinnerGenero = ArrayAdapter. createFromResource(this, R.array.genero, android.R.layout.simple_spinner_item);
        adapterSpinnerGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generoUsuario.setAdapter(adapterSpinnerGenero);
        generoUsuario.setOnItemSelectedListener(this);


        //En caso de añadir el botón para salvar la información del usuario
        btn_save_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validar las entradas antes de subirlas
                if (ValidarEntrada()) {
                    new Thread(() -> {
                        try {
                            resultRegister = signUpAccount(correoUsuario.getText().toString(),passwordUsuario.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        boolean finalRegistro = Boolean.parseBoolean(resultRegister.get(0));
                        runOnUiThread(new Runnable() {
                            public void run() {

                                if(finalRegistro){
                                    //Rellanar los datos del Usuario
                                    Usuario usuario = new Usuario(nombreUsuario.getText().toString(), apellidoUsuario.getText().toString()
                                            , correoUsuario.getText().toString(), generoSeleccionado, rolSeleccionado, inventarioID );
                                    //Añade el prestatario a la base de datos en "Prestatario - > InventarioID" con la clave de su RUN.

                                    if (addDataUser(usuario, resultRegister.get(1))) {
                                        Toast.makeText(getApplicationContext(), "Usuario añadido", Toast.LENGTH_SHORT).show();
                                        clearText();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Existe un error en la base de datos", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Problemas al registrar email y password",Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                    }).start();

                }
            }
        });

        //En caso de querer el historial de usuarios
        btn_volver_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMostrarUsuarioActivity();
            }
        });

    }

    //Función que válida que las entradas obligatorias sean rellenadas
    private boolean ValidarEntrada() {
        boolean flag = true;

        if (nombreUsuario.getText().toString().equals("")) {
            nombreUsuario.setError("Este campo es obligatorio");
            flag = false;
        }
        if (apellidoUsuario.getText().toString().equals("")) {
            apellidoUsuario.setError("Este campo es obligatorio");
            flag = false;
        }
        if (correoUsuario.getText().toString().equals("")) {
            correoUsuario.setError("Este campo es obligatorio");
            flag = false;
        }
        if (passwordUsuario.getText().toString().equals("")) {
            passwordUsuario.setError("Este campo es obligatorio");
            flag = false;
        }


        return flag;
    }

    //Limpia los textos luego de añadir el usuario (Evitamos el doble click y agregar dos veces)
    public void clearText() {
        nombreUsuario.setText("");
        apellidoUsuario.setText("");
        correoUsuario.setText("");
        passwordUsuario.setText("");
    }

    //Función para actualizar y así colocar en la base de datos
    public void SetGender(String value){
        if(value.equals("No binario")){
            generoSeleccionado = "NB";
        }
        else if (value.equals("Masculino")){
            generoSeleccionado = "M";
        }
        else{
            generoSeleccionado = "F";
        }
    }
    //Función para actualizar y así colocar en la base de datos
    public void SetRol(String value){
        if(value.equals("Centro de estudiantes")){
            rolSeleccionado = "cee";
        }
        else if (value.equals("Delegado")){
            rolSeleccionado = "delegado";
        }
        else{
            rolSeleccionado = "admin";
        }
    }

    // Funciones para cuando seleccionan datos en el Spinner.
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        //De acá se obtienen los datos del spinner
        SetRol(rolUsuario.getSelectedItem().toString());
        SetGender(generoUsuario.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //En casa de que no selecciona nada, por cualquier motivo sea
        Toast.makeText(getApplicationContext(), "Debe seleccionar una opción", Toast.LENGTH_LONG).show();
    }

    //Función de abrir la actividad "Lista de usuarios"
    public void openMostrarUsuarioActivity()
    {
        Intent i = new Intent(this, ListaDeUsuarios_Activity.class);
        startActivity(i);
    }

}