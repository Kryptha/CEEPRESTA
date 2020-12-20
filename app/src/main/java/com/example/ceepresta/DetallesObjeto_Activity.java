package com.example.ceepresta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class DetallesObjeto_Activity extends AppCompatActivity
{
    private String nombreObjeto, estado, fechaRegistro, Urlimage;
    private TextView nombreDetalle, estadoDetalle, fechaRegDetalle;
    private ImageView imgObjetoDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles_objeto);

        nombreObjeto = getIntent().getStringExtra("nombre");
        estado = getIntent().getStringExtra("estado");
        fechaRegistro = getIntent().getStringExtra("fechaRegistro");
        Urlimage = getIntent().getStringExtra("Urlimage");

        nombreDetalle = findViewById(R.id.tv_nombre_detalle);
        estadoDetalle = findViewById(R.id.tv_estado_detalle);
        fechaRegDetalle = findViewById(R.id.tv_fechaReg_detalle);

        imgObjetoDetalle = findViewById(R.id.iv_imgObjeto_detalle);

        nombreDetalle.setText(nombreObjeto);
        estadoDetalle.setText("Estado: " + estado);
        fechaRegDetalle.setText("Fecha de registro: " + fechaRegistro);
        Picasso.get().load(Urlimage).into(imgObjetoDetalle);
    }
}