package com.example.ceepresta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Clases.Objeto;
import Clases.Usuario;

public class Principal_Activity extends AppCompatActivity {

    /*Cardviews clickeables del menu */
    private CardView cvBuscar, cvInventario, cvPrestamos, cvRegistrarObjeto, cvRegistrarUsuario, cvConfiguraciones;
    // Obtengo la UID desde el inicio sesión
    private String UID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Obtención de lo visual
        cvBuscar = findViewById(R.id.cardview_search);
        cvInventario = findViewById(R.id.cardview_inventario);
        cvPrestamos = findViewById(R.id.cardview_prestamo);
        cvRegistrarObjeto = findViewById(R.id.cardview_registerobjeto);
        cvRegistrarUsuario = findViewById(R.id.cardview_adduser);
        cvConfiguraciones = findViewById(R.id.cardview_settings);

        // Obtengo la UID desde el inicio sesión
        UID = getIntent().getStringExtra("UID");

        //Declaración de click de los cardview
        cvBuscar.setOnClickListener(buttonclick);
        cvInventario.setOnClickListener(buttonclick);
        cvRegistrarObjeto.setOnClickListener(buttonclick);


    }

    //Función donde cliclea dependiendo del cardview seleccionado (AÚN FALTAN ALGUNOS PERO ES LA MISMA LÓGICA)
    private View.OnClickListener buttonclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (cvInventario.equals(view)) {
                openMostrarInventarioActivity();

            } else if (cvBuscar.equals(view)) {
                openMostrarPrestatariosActivity();

            }
            else if (cvRegistrarObjeto.equals(view)) {
                openAgregarObjetoActivity();
            }
        }
    };

    //Función de abrir la actividad de "añadir objetos"
    public void openAgregarObjetoActivity(){
        Intent i = new Intent(this, AñadirObjeto_Activity.class);
        startActivity(i);
    }

    //Función de abrir la actividad "mostrar prestatarios"
    public void openMostrarPrestatariosActivity()
    {
        Intent i = new Intent(this, MostrarPrestatarios_Activity.class);
        startActivity(i);
    }


    //Función de abrir la actibidad de "Mostrar inventarior"
    public void openMostrarInventarioActivity()
    {
        Intent intent = new Intent(this, Mostrar_Inventario_Activity.class);
        startActivity(intent);
    }


}