package com.example.ceepresta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Clases.Objeto;

public class DetallesObjeto_Activity extends AppCompatActivity
{
    private TextView nombreDetalle, estadoDetalle, fechaRegDetalle;
    private ImageView imgObjetoDetalle;
    private FirebaseStorage fbstorage_ref_almac;
    private DatabaseReference ref_db;
    private Objeto objeto_recibido;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles_objeto);

        objeto_recibido = (Objeto) getIntent().getSerializableExtra("lista_de_objetos");

        nombreDetalle = findViewById(R.id.tv_nombre_detalle);
        estadoDetalle = findViewById(R.id.tv_estado_detalle);
        fechaRegDetalle = findViewById(R.id.tv_fechaReg_detalle);

        imgObjetoDetalle = findViewById(R.id.iv_imgObjeto_detalle);

        nombreDetalle.setText(objeto_recibido.getNombre());
        estadoDetalle.setText("Estado: " + objeto_recibido.getEstado());
        fechaRegDetalle.setText("Fecha de registro: " + objeto_recibido.getFechaRegistro());
        Picasso.get().load(objeto_recibido.getUrlimage()).into(imgObjetoDetalle);

        //Referencias de la base de datos y el storage
        ref_db = FirebaseDatabase.getInstance().getReference().child("Inventarios").child("LCC");
        fbstorage_ref_almac = FirebaseStorage.getInstance();
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

        txt_dialogo.setText("¿Realmente quieres borrar " + objeto_recibido.getNombre() + "? Este proceso no es reversible");

        btn_borrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String llave_seleccionada = objeto_recibido.getKey();
                StorageReference ref_imagen = fbstorage_ref_almac.getReferenceFromUrl(objeto_recibido.getUrlimage());

                /*Solo se quiere borrar la imagen de la base de datos SI el borrado fue exitoso en el
                 * almacenamiento, o sino se pueden tener entradas en el almacenamiento que no tienen
                 * la entrada correspondiente en la base de datos, cuando el borrado en la base de datos
                 * fue exitoso pero no asi en el almacenamiento.*/
                ref_imagen.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        ref_db.child(llave_seleccionada).removeValue();
                        Toast.makeText(DetallesObjeto_Activity.this, objeto_recibido.getNombre() + "eliminado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });//Se puede agregar un OnFailureListener si se desea realizar un feedback cuando el borrado falla
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
}