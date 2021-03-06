package com.example.ceepresta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Clases.Objeto;
import Clases.Usuario;

public class Mostrar_Inventario_Activity extends AppCompatActivity{

    /*Preparación del recycler-adaptador*/
    private RecyclerView recyclerView;
    private AdaptadorObjeto adaptadorObjeto;

    //Ciruclo de progreso
    private ProgressBar circulo_progreso;

    //Para obtener una referencia a las imagenes en Firebase Storage
    private FirebaseStorage fbstorage_ref_almac;

    private DatabaseReference ref_db;
    private ValueEventListener db_listener;

    //Lista de objetos
    private ArrayList<Objeto> lista_de_objetos;

    //Palabra para buscar
    private String palabrafiltrar = "";

    //Obtención del searchview
    private SearchView searchview;

    //UID del usuario que inicio sesión
    private Usuario currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar__inventario_);

        //Obtención y preparación del cardview.
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//Default: vertical

        currentUser = (Usuario) getIntent().getSerializableExtra("User");

        //Circulo de progreso antes de mostrar la lista
        circulo_progreso = findViewById(R.id.progress_circle);

        //Lista con los objetos a mostrar
        lista_de_objetos = new ArrayList<>();

        //Referencias de la base de datos y el storage
        ref_db = FirebaseDatabase.getInstance().getReference().child("Inventarios").child(currentUser.getInventarioid());
        fbstorage_ref_almac = FirebaseStorage.getInstance();

        db_listener = ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                lista_de_objetos.clear();

                for(DataSnapshot postSnapshot : snapshot.getChildren())
                {   //Obtención de los datos de la BD a un objeto
                    Objeto objeto = postSnapshot.getValue(Objeto.class);

                    /*Se guarda la llave única de la base de datos para identificar la entrada correcta
                     * en la base de datos para eliminarla*/
                    objeto.setKey(postSnapshot.getKey());

                    //Añado el objeto extraido a la lista
                    lista_de_objetos.add(objeto);
                }

                //Adapatador del Recyclerview, que mostrará la lista de objetos
                adaptadorObjeto = new AdaptadorObjeto(lista_de_objetos);
                recyclerView.setAdapter(adaptadorObjeto);
                adaptadorObjeto.setUser(currentUser);

                /* Funciones de la barra de búsqueda*/
                initSearchWidgets();

                //Como ya se cargo, circulo de progreso invisible
                circulo_progreso.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                //Se muestra el error en caso de cualquier eventualidad
                Toast.makeText(Mostrar_Inventario_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                circulo_progreso.setVisibility(View.INVISIBLE);
            }
        });


    }


    private void initSearchWidgets()
    {
        //Se busca la id de la barra de búsqueda.
        searchview = (SearchView) findViewById(R.id.id_searchview);

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
            public boolean onQueryTextChange(String s)
            {
                /*Se pasa el string 's' que representa lo que el usuario
                 * escribe en la barra de búsqueda*/
                palabrafiltrar = s;
                adaptadorObjeto.getFilter().filter(s);
                return false;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ref_db.removeEventListener(db_listener);
    }

}