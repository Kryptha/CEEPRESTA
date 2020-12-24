package com.example.ceepresta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import Clases.Prestatario;

import static BaseDeDatos.Firebase.addDateInventario;
import static BaseDeDatos.Firebase.addDatePrestatario;

public class AñadirPrestatario_Activity extends AppCompatActivity {

    //Declaración visual
    private EditText nombrePrestatario, apellidoPrestatario, correoPrestatario, runPrestatario, telefonoPrestatario, carreraPrestatario;
    private Button btn_save, btn_volver;
    //Saber en que carrera pertenece los datos
    private String inventarioID = "LCC";

    public static final Pattern
            RUN_PATTERN = Pattern.compile("[0-9+]{8,10}"),
            RUN_PATTERN_K = Pattern.compile("[0-9+]{7,9}" + "[kK]");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_prestatario);

        //Obtención de variable visuales
        nombrePrestatario = findViewById(R.id.editxt_name_prestatario);
        apellidoPrestatario = findViewById(R.id.editxt_surname_prestatario);
        correoPrestatario = findViewById(R.id.editxt_correo_prestatario);
        runPrestatario = findViewById(R.id.editxt_run_prestatario);
        telefonoPrestatario = findViewById(R.id.editxt_telefono_prestatario);
        carreraPrestatario = findViewById(R.id.editxt_carrera_prestatario);

        //Obtención de botones
        btn_save = findViewById(R.id.btn_save_añadirPrestario);
        btn_volver = findViewById(R.id.btn_verPrestarios);


        //En caso de añadir el botón para salvar la información del prestatario
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validar las entradas antes de subirlas
                if(ValidarEntrada()){
                    //Rellanar los datos del prestatario
                    Prestatario prestatario = new Prestatario(nombrePrestatario.getText().toString(), apellidoPrestatario.getText().toString(),
                            carreraPrestatario.getText().toString(), telefonoPrestatario.getText().toString(), correoPrestatario.getText().toString());
                    //Añade el prestatario a la base de datos en "Prestatario - > InventarioID" con la clave de su RUN.
                    if(addDatePrestatario(inventarioID, prestatario, runPrestatario.getText().toString())){
                        Toast.makeText(getApplicationContext(), "Prestatario añadido", Toast.LENGTH_SHORT).show();
                        clearText();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Existe un error en la base de datos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //En caso de querer cancelar la operación y volver a la lista de prestatarios
        btn_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMostrarPrestatariosActivity();
            }
        });

    }

    //Función que válida que las entradas obligatorias sean rellenadas
    private boolean ValidarEntrada(){
        boolean flag = true;
        String runInput = runPrestatario.getText().toString().toLowerCase();

        if(nombrePrestatario.getText().toString().equals("")){
            nombrePrestatario.setError("Este campo es obligatorio");
            flag = false;
        }
        if(apellidoPrestatario.getText().toString().equals("")){
            apellidoPrestatario.setError("Este campo es obligatorio");
            flag = false;
        }
        if(correoPrestatario.getText().toString().equals("")){
            correoPrestatario.setError("Este campo es obligatorio");
            flag = false;
        }

        if(runInput.equals(""))
        {
            runPrestatario.setError("Este campo es obligatorio");
            flag = false;
        }
        else if(!RUN_PATTERN.matcher(runInput).matches() &&
                !RUN_PATTERN_K.matcher(runInput).matches())
        {
            runPrestatario.setError("Ingrese un RUT válido");
            flag = false;
        }

        return flag;
    }

    //Función de abrir la actividad "mostrar prestatarios"
    public void openMostrarPrestatariosActivity()
    {
        Intent i = new Intent(this, MostrarPrestatarios_Activity.class);
        startActivity(i);
    }

    public void clearText(){
        nombrePrestatario.setText("");
        apellidoPrestatario.setText("");
        correoPrestatario.setText("");
        runPrestatario.setText("");
        carreraPrestatario.setText("");
        telefonoPrestatario.setText("");
    }

}