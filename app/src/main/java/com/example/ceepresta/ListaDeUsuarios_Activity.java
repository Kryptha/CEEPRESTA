package com.example.ceepresta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import Clases.Prestatario;
import Clases.Usuario;

public class ListaDeUsuarios_Activity extends AppCompatActivity {

    /*Preparación del recycler-adaptador*/
    private RecyclerView recyclerView;
    private AdaptadorUsuarios adaptadorUsuario;

    //Circulo de progreso
    private ProgressBar circulo_progreso;

    //Para obtener una referencia a las imagenes en Firebase Storage
    private DatabaseReference ref_db;
    private ValueEventListener db_listener;

    //Lista de Usuarios
    private ArrayList<Usuario> lista_de_usuarios;

    //Palabra para buscar
    private String palabrafiltrar = "";

    //Obtención del searchview
    private SearchView searchview;

    private FloatingActionButton btn_add_Usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_usuarios);

        //Obtención y preparación del cardview.
        recyclerView = findViewById(R.id.recyclerView_usuario);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//Default: vertical

        //Circulo de progreso antes de mostrar la lista
        circulo_progreso = findViewById(R.id.progress_circle_usuario);

        //Botón flotante para agregar usuarios
        btn_add_Usuario = findViewById(R.id.floatingActionButton_usuario);

        //Lista con los usuarios a mostrar
        lista_de_usuarios = new ArrayList<>();

        //Referencias de la base de datos y el storage
        ref_db = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        db_listener = ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_de_usuarios.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {   //Obtención de los datos de la BD a un Usuario
                    Usuario usuario = postSnapshot.getValue(Usuario.class);

                    /*Se guarda la llave única de la base de datos para identificar la entrada correcta
                     * en la base de datos para eliminarla*/
                    // En este caso la llave del prestario es el rut, por lo tanto es mejor identificarlo así
                    usuario.setUid(postSnapshot.getKey());

                    //Añado el prestatarios extraido a la lista
                    lista_de_usuarios.add(usuario);
                }

                //Adapatador del Recyclerview, que mostrará la lista de prestatarios
                adaptadorUsuario = new AdaptadorUsuarios(lista_de_usuarios);
                recyclerView.setAdapter(adaptadorUsuario);

                /* Funciones de la barra de búsqueda*/
                initSearchWidgets();

                //Como ya se cargo, circulo de progreso invisible
                circulo_progreso.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Se muestra el error en caso de cualquier eventualidad
                Toast.makeText(ListaDeUsuarios_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                circulo_progreso.setVisibility(View.INVISIBLE);
            }
        });

        //En caso de apretar el botón, se va a la actividad de añadir un prestatario
        btn_add_Usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openAñadirUsuarioActivity();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void initSearchWidgets() {
        //Se busca la id de la barra de búsqueda.
        searchview = (SearchView) findViewById(R.id.id_searchview_usuario);

        //Se utiliza Listener para recibir lo que se escribe en la barra de búsqueda.
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            //Este método se llama cada vez que el usuario ingresa texto en la barra.
            @Override
            /*Se filtra la lista en tiempo en real, por lo tanto, solo es necesario el
             * método onQueryTextChange*/
            public boolean onQueryTextChange(String s) {
                /*Se pasa el string 's' que representa lo que el usuario
                 * escribe en la barra de búsqueda*/
                palabrafiltrar = s;
                adaptadorUsuario.getFilter().filter(s);
                return false;
            }
        });
    }

    //Función de abrir la actividad "Añadir Usuario"
    public void openAñadirUsuarioActivity()
    {
        Intent i = new Intent(this, AñadirUsuario_Activity.class);
        startActivity(i);
    }
}