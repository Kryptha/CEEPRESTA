package com.example.ceepresta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    //Declaración de textos
    private TextView msgBienvenida, msgRol;

    /*Cardviews clickeables del menu */
    private CardView cvPrestatarios, cvInventario, cvPrestamos, cvRegistrarObjeto, cvRegistrarUsuario, cvConfiguraciones;
    // Declaración de UID
    private Usuario usuario;
    private String UID;
    //Autenticación para obtener la UID
    FirebaseAuth mAuth;
    DatabaseReference ref;
    //Declaración de circulo de progreso
    private ProgressBar circulo_progreso;
    //Declaración de imagenes y textos para deshabilitarr según rango
    private TextView txtAddUser, txtAddObject;
    private ImageView imgAddUser, imgAddObject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //Obtención de la UID desde la autenticación
        UID = getIntent().getStringExtra("UID");

        //Declaración de la referencia
        ref = FirebaseDatabase.getInstance().getReference();

        // Obtención de lo visual
        cvPrestatarios = findViewById(R.id.cardview_search);
        cvInventario = findViewById(R.id.cardview_inventario);
        cvPrestamos = findViewById(R.id.cardview_prestamo);
        cvRegistrarObjeto = findViewById(R.id.cardview_registerobjeto);
        cvRegistrarUsuario = findViewById(R.id.cardview_adduser);
        cvConfiguraciones = findViewById(R.id.cardview_settings);
        msgBienvenida = findViewById(R.id.txv_msg_bienvenida);
        msgRol = findViewById(R.id.txv_rol_bienvenida);
        //Aspecto visual  de cardviews que se pueden o no desahbilitar
        txtAddObject = findViewById(R.id.txtvAddObject);
        txtAddUser = findViewById(R.id.txtvAddUser);
        imgAddObject = findViewById(R.id.imgvwAddObject);
        imgAddUser = findViewById(R.id.imgvwAddUser);
        //Circulo de progreso antes de mostrar el mensaje de bienvenida
        circulo_progreso = findViewById(R.id.progress_circle_home);


        //Obtención del usuario para actualizar los mensajes de bienvenida (Se debe colocar acá, ya que trabaja en hilos)
        ref.child("Usuarios").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    usuario = dataSnapshot.getValue(Usuario.class);
                    usuario.setUid(UID);
                    setRolText(usuario);
                    setWelcomeText(usuario);
                    setBotton(usuario);
                    circulo_progreso.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
                circulo_progreso.setVisibility(View.INVISIBLE);
            }
        });

        //Declaración de click de los cardview
        cvPrestatarios.setOnClickListener(buttonclick);
        cvInventario.setOnClickListener(buttonclick);
        cvRegistrarObjeto.setOnClickListener(buttonclick);
        cvRegistrarUsuario.setOnClickListener(buttonclick);

        //TESTING
        //testBD();

    }

    //Función donde cliclea dependiendo del cardview seleccionado (AÚN FALTAN ALGUNOS PERO ES LA MISMA LÓGICA)
    private View.OnClickListener buttonclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (cvInventario.equals(view)) {
                openMostrarInventarioActivity();

            } else if (cvPrestatarios.equals(view)) {
                openMostrarPrestatariosActivity();

            }
            else if (cvRegistrarObjeto.equals(view)) {
                openAgregarObjetoActivity();
            }
            else if (cvRegistrarUsuario.equals(view)){
                openUsuarioActivity();
            }
        }
    };

    //Función de abrir la actividad de "añadir objetos"
    public void openAgregarObjetoActivity(){
        Intent intent = new Intent(this, AñadirObjeto_Activity.class);
        intent.putExtra("User", usuario);
        startActivity(intent);
    }

    //Función de abrir la actividad "mostrar prestatarios"
    public void openMostrarPrestatariosActivity()
    {
        Intent intent = new Intent(this, MostrarPrestatarios_Activity.class);
        intent.putExtra("User", usuario);
        startActivity(intent);
    }


    //Función de abrir la actibidad de "Mostrar inventarior"
    public void openMostrarInventarioActivity()
    {
        Intent intent = new Intent(this, Mostrar_Inventario_Activity.class);
        intent.putExtra("User", usuario);
        startActivity(intent);
    }

    //Función de abrir la actibidad de "Mostrar inventarior"
    public void openUsuarioActivity()
    {
        Intent intent = new Intent(this, ListaDeUsuarios_Activity.class);
        intent.putExtra("User", usuario);
        startActivity(intent);
    }


    public void setWelcomeText (Usuario usuario){

        if(usuario.getGenero().equals("F")){
            msgBienvenida.setText("Bienvenida " + usuario.getNombre());
        }
        else if(usuario.getGenero().equals("M")){
            msgBienvenida.setText("Bienvenido " + usuario.getNombre());
        }
        else{
            msgBienvenida.setText("Bienvenide " + usuario.getNombre());
        }

    }

    public void setRolText (Usuario usuario){

        if(usuario.getRol().equals("cee")){
            msgRol.setText("Centro de estudiantes");
        }
        else if(usuario.getRol().equals("admin")){
            msgRol.setText("Administrador");
        }
        else{
            msgRol.setText("Delegado");
        }

    }

    public void setBotton(Usuario usuario){
        if(usuario.getRol().equals("cee")){
            //Desahbilitir el botón de "registrar usuario"
            cvRegistrarUsuario.setEnabled(false);
            imgAddUser.setImageResource(R.drawable.adduserenable);
            txtAddUser.setText("Sin autorización");

        }
        if(usuario.getRol().equals("delegado")){
            //Desahbilitir el botón de "registrar usuario"
            cvRegistrarUsuario.setEnabled(false);
            imgAddUser.setImageResource(R.drawable.adduserenable);
            txtAddUser.setText("Sin autorización");
            //Desabilitar botón de "registrar objeto"
            cvRegistrarObjeto.setEnabled(false);
            imgAddObject.setImageResource(R.drawable.registerobjetoenable);
            txtAddObject.setText("Sin autorización");


        }
    }

}