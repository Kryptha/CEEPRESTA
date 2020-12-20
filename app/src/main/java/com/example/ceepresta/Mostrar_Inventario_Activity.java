package com.example.ceepresta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import Clases.Objeto;

public class Mostrar_Inventario_Activity extends AppCompatActivity implements AdaptadorObjeto.OnItemClickListener{

    /*Preparación del recycler-adaptador*/
    private RecyclerView recyclerView;
    private AdaptadorObjeto adaptadorObjeto;

    //Ciruclo de progreso
    private ProgressBar circulo_progreso;

    //Para obtener una referencia a las imagenes en Firebase Storage
    private FirebaseStorage ref_almac;
    private DatabaseReference ref_db;
    private ValueEventListener db_listener;

    //Lista de objetos
    private List<Objeto> lista_de_objetos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar__inventario_);

        //Obtención y preparación del cardview.
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//Default: vertical

        //Circulo de progreso antes de mostrar la lista
        circulo_progreso = findViewById(R.id.progress_circle);

        //Lista con los objetos a mostrar
        lista_de_objetos = new ArrayList<>();

        //Adapatador del Recyclerview, que mostrará la lista de objetos
        adaptadorObjeto = new AdaptadorObjeto(Mostrar_Inventario_Activity.this, lista_de_objetos);
        recyclerView.setAdapter(adaptadorObjeto);

        //Si selecciona algun item de la lista
        adaptadorObjeto.setOnItemClickListener(Mostrar_Inventario_Activity.this);

        //Referencias de la base de datos y el storage
        ref_db = FirebaseDatabase.getInstance().getReference().child("Inventarios").child("LCC");
        ref_almac = FirebaseStorage.getInstance();

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

                //Le aviso al adaptador del cambio
                adaptadorObjeto.notifyDataSetChanged();
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

    //Función que muestra algo al momento de clickear un objeto
    @Override
    public void onItemClick(int position)
    {
        Intent intent = new Intent(getApplicationContext(), DetallesObjeto_Activity.class);
        intent.putExtra("nombre", lista_de_objetos.get(position).getNombre());
        intent.putExtra("estado", lista_de_objetos.get(position).getEstado());
        intent.putExtra("fechaRegistro", lista_de_objetos.get(position).getFechaRegistro());
        intent.putExtra("Urlimage", lista_de_objetos.get(position).getUrlimage());
        startActivity(intent);
    }

    //En caso de mantener apretado un item de la lista de objetos
    @Override
    public void onWhatEverClick(int position)
    {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    //Remueve el objeto de la base de datos y la imagen del Storage
    @Override
    public void onDeleteClick(int position)
    {
        Objeto objeto_seleccionado = lista_de_objetos.get(position);
        String llave_seleccionada = objeto_seleccionado.getKey();
        StorageReference ref_imagen = ref_almac.getReferenceFromUrl(objeto_seleccionado.getUrlimage());

        /*Solo se quiere borrar la imagen de la base de datos SI el borrado fue exitoso en el
         * almacenamiento, o sino se pueden tener entradas en el almacenamiento que no tienen
         * la entrada correspondiente en la base de datos, cuando el borrado en la base de datos
         * fue exitoso pero no asi en el almacenamiento.*/
        ref_imagen.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                ref_db.child(llave_seleccionada).removeValue();
                Toast.makeText(Mostrar_Inventario_Activity.this, "Objeto borrado", Toast.LENGTH_SHORT).show();

            }
        });/*Se puede agregar un OnFailureListener si se desea realizar un feedback cuando el borrado falla*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ref_db.removeEventListener(db_listener);
    }
}