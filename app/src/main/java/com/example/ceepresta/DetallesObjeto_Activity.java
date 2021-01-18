package com.example.ceepresta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Clases.Objeto;
import Clases.Usuario;

public class DetallesObjeto_Activity extends AppCompatActivity
{
    //Declaración de variable visuales
    private TextView nombreDetalle, estadoDetalle, fechaRegDetalle;
    private TextView lastPrestatario, lastPrestamista, lastReceptor, lastFechaPrestamo, lastFechaDevolución;
    private ImageView imgObjetoDetalle;
    private Button btnPrestar;
    //Declaración de base de datos y storage
    private FirebaseStorage fbstorage_ref_almac;
    private DatabaseReference ref_db, ref_db_delete;
    //Declaración de objeto y usuario
    private Objeto objeto_recibido, objetoShow;
    private Usuario currentUser;
    //Declaración de diálogo
    Dialog dialog;

    //Para obtener una referencia a las imagenes en Firebase Storage
    private ValueEventListener db_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles_objeto);

        //Obtención del objeto inicial
        objeto_recibido = (Objeto) getIntent().getSerializableExtra("lista_de_objetos");

        //Declaración de variable viosuales
        nombreDetalle = findViewById(R.id.tv_nombre_detalle);
        estadoDetalle = findViewById(R.id.tv_estado_detalle);
        fechaRegDetalle = findViewById(R.id.tv_fechaReg_detalle);
        imgObjetoDetalle = findViewById(R.id.iv_imgObjeto_detalle);
        btnPrestar = findViewById(R.id.btn_prestar_objeto);

        //Declaración de datos de último prestamo y devolución
        lastFechaDevolución = findViewById(R.id.tv_ultvez_dev);
        lastFechaPrestamo = findViewById(R.id.tv_ultvez_prest);
        lastReceptor = findViewById(R.id.tv_ult_receptor);
        lastPrestamista = findViewById(R.id.tv_ult_prestamista);
        lastPrestatario = findViewById(R.id.tv_ult_prest);

        //Obtención del usuario actual
        currentUser = (Usuario) getIntent().getSerializableExtra("User");

        //Referencias de la base de datos y el storage
        ref_db = FirebaseDatabase.getInstance().getReference().child("Inventarios").child(currentUser.getInventarioid()).child(objeto_recibido.getKey());
        ref_db_delete = FirebaseDatabase.getInstance().getReference().child("Inventarios").child(currentUser.getInventarioid());
        fbstorage_ref_almac = FirebaseStorage.getInstance();

        //Obtención del objeto desde la base de datos
        db_listener = ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                //Obtención de los datos de la BD a un objeto
                    Objeto objeto = snapshot.getValue(Objeto.class);
                    objetoShow = objeto;

                   /* De esta manera se va a actualizando la muestra en pantalla a medida que cambian los dados*/
                    objeto.setKey(snapshot.getKey());
                    nombreDetalle.setText(objeto.getNombre());
                    estadoDetalle.setText("Estado: " + objeto.getEstado());
                    fechaRegDetalle.setText("Fecha de registro: " + objeto.getFechaRegistro());
                    Picasso.get().load(objeto.getUrlimage()).into(imgObjetoDetalle);

                    //En caso de que no se haya registrado aún los datos de prestamos, tendran el template por defecto, de lo contrario se colocará el dato
                    String ultiFechDev = objeto.getLastFechaDevolución().equals("") ? "Sin devolución" : objeto.getLastFechaDevolución();
                    lastFechaDevolución.setText("Última vez devuelto: " + ultiFechDev);
                    String ultiPrestamista = objeto.getLastPrestamista().equals("") ? "Sin prestamista" : objeto.getLastPrestamista();
                    lastPrestamista.setText("Último prestamista: " + ultiPrestamista);
                    String ultiFechaPres = objeto.getLastFechaPrestamo().equals("") ? "No existe préstamo" : objeto.getLastFechaPrestamo();
                    lastFechaPrestamo.setText("Última vez préstado: " + ultiFechaPres);
                    String ultiPrestatario = objeto.getLastPrestatario().equals("") ? "Sin prestatario" : objeto.getLastPrestatario();
                    lastPrestatario.setText("Último prestatario: " + ultiPrestatario);
                    String ultiReceptor = objeto.getLastReceptor().equals("") ? "Sin Receptor" : objeto.getLastReceptor();
                    lastReceptor.setText("Último receptor: " + ultiReceptor);

                    //En caso de que no hayan objetos suficiente para prestar o el estado sea No disponible para prestar
                    //Entonces se inhabilita el botón
                    if(objeto.getCantidad().equals("0") || objeto.getEstado().equals("No Disponible")){
                        btnPrestar.setEnabled(false);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                //Se muestra el error en caso de cualquier eventualidad
                Toast.makeText(DetallesObjeto_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        //Se verifica si el usuario es delegado o no, para deshabilitar la edicion de objeto o no.
        if(currentUser.getRol().equals("Delegado"))
        {
            Button edit_obj;
            edit_obj = findViewById(R.id.btn_editar_detalle);
            edit_obj.setEnabled(false);
        }



    }


    public void onEditClick(View view)
    {

        Intent i = new Intent(this, EditarObjeto_Activity.class);
        i.putExtra("ObjetoSeleccionado", objeto_recibido);
        i.putExtra("User", currentUser);
        startActivity(i);

    }

    public void onDeleteClick(View view)
    {
        TextView txt_dialogo;
        dialog = new Dialog(this);
        Button btn_borrar, btn_cancelar;

        dialog.setContentView(R.layout.dialogo_eliminar_objeto);

        txt_dialogo = dialog.findViewById(R.id.dialogo_txt_small);
        btn_borrar =  dialog.findViewById(R.id.btn_dialogo_borrar);
        btn_cancelar = dialog.findViewById(R.id.btn_dialogo_cancelar);

        txt_dialogo.setText("¿Realmente quieres borrar " + objetoShow.getNombre() + "? Este proceso no es reversible");

        btn_borrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                StorageReference ref_imagen = fbstorage_ref_almac.getReferenceFromUrl(objeto_recibido.getUrlimage());
                /*Solo se quiere borrar la imagen de la base de datos SI el borrado fue exitoso en el
                 * almacenamiento, o sino se pueden tener entradas en el almacenamiento que no tienen
                 * la entrada correspondiente en la base de datos, cuando el borrado en la base de datos
                 * fue exitoso pero no asi en el almacenamiento.*/

                ref_imagen.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        ref_db_delete.child(objeto_recibido.getKey()).removeValue();
                        Toast.makeText(DetallesObjeto_Activity.this, objetoShow.getNombre() + "eliminado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), Mostrar_Inventario_Activity.class);
                        intent.putExtra("User", currentUser);
                        startActivity(intent);

                    }
                });
                //DetallesObjeto_Activity.this.finish();
                //Se puede agregar un OnFailureListener si se desea realizar un feedback cuando el borrado falla
            }
        });
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    //Función la cual clickea el botón "prestar"
    public void prestarClick(View view) {
        Intent i = new Intent(this, PrestamoActivity.class);
        i.putExtra("ObjetoSeleccionado", objeto_recibido);
        i.putExtra("User", currentUser);
        startActivity(i);

    }

}