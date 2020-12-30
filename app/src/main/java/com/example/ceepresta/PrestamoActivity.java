package com.example.ceepresta;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Clases.Objeto;
import Clases.Prestamo;
import Clases.Prestatario;
import Clases.Usuario;

import static BaseDeDatos.Firebase.SetDataInventario;
import static BaseDeDatos.Firebase.addDataPrestamo;

public class PrestamoActivity extends AppCompatActivity {
    //Declaración de variables
    private TextView text_prestatario, fecha_entrega;
    private EditText editxtCantidadPrestamo;
    private ArrayList<Prestatario> listaPrestarios;
    private ArrayList<String> listaNombreDePrestatarios;
    private Dialog dialog_prestatario;
    private DatabaseReference ref_db;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ValueEventListener db_listener;
    //Obtención de fecha
    private String fecha_plazo_entrega;
    private String fecha_prestamo;
    //Obtención del prestatario
    private String prestatarioID;
    //Obtención del objeto
    private Objeto objeto;
    //Declaración del usuario prestamista
    private Usuario currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamo);

        //Recibo el objeto seleccionado
        objeto = (Objeto) getIntent().getSerializableExtra("ObjetoSeleccionado");
        //Obtención de la UID del prestamista
        //Obtención del usuario actual
        currentUser = (Usuario) getIntent().getSerializableExtra("User");


        //Asignación de variables
        text_prestatario = findViewById(R.id.txtview_prestatario_seleccion);
        fecha_entrega = findViewById(R.id.txtview_fecha_seleccion);
        editxtCantidadPrestamo = findViewById(R.id.editxt_cantidad_prestamo);

        //Obtención de la fecha actual de prestamo
        Calendar hoy = Calendar.getInstance();
        fecha_prestamo = String.valueOf(hoy.get(Calendar.DAY_OF_MONTH)) + "/" +  String.valueOf(hoy.get(Calendar.MONTH)+1) + "/" + String.valueOf(hoy.get(Calendar.YEAR));

        //Inicializar variable
        listaPrestarios = new ArrayList<>();
        listaNombreDePrestatarios = new ArrayList<>();

        //Acá buscar en la base de datos y añadir a la lista.
        //Referencias de la base de datos y el storage
        ref_db = FirebaseDatabase.getInstance().getReference().child("Prestatarios").child("LCC");

        db_listener = ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPrestarios.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {   //Obtención de los datos de la BD a un prestatario
                    Prestatario prestatario = postSnapshot.getValue(Prestatario.class);

                    /*Se guarda la llave única de la base de datos para identificar la entrada correcta
                     * en la base de datos para eliminarla*/
                    // En este caso la llave del prestario es el rut, por lo tanto es mejor identificarlo así
                    prestatario.setRut(postSnapshot.getKey());

                    //Añado el prestatarios extraido a la lista
                    listaPrestarios.add(prestatario);
                    //Añado el nombre a la lista a mostrar
                    listaNombreDePrestatarios.add(prestatario.getNombre() + " " + prestatario.getApellido());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Se muestra el error en caso de cualquier eventualidad
                Toast.makeText(PrestamoActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //Seleccionar un prestatario existenten
        text_prestatario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inicializar diálogo
                dialog_prestatario= new Dialog(PrestamoActivity.this);
                //Actualizar el diálogo
                dialog_prestatario.setContentView(R.layout.dialog_buscador_spinner);
                //Actualizar el largo y el ancho
                dialog_prestatario.getWindow().setLayout(800,800);
                //Actualizar el fondo transparente
                dialog_prestatario.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //Mostrar diálogo
                dialog_prestatario.show();

                //Inicializar variables del diálogo
                EditText buscador_prestatario = dialog_prestatario.findViewById(R.id.editxt_buscador_prestatario);
                ListView listaViewDePrestatarios = dialog_prestatario.findViewById(R.id.lista_prestatarios_existentes);

                //Inicializar el adaptador del arreglo
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PrestamoActivity.this,
                        android.R.layout.simple_list_item_1, listaNombreDePrestatarios);
                //Actualizar adaptador
                listaViewDePrestatarios.setAdapter(adapter);

                buscador_prestatario.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //Filtrar la lista de prestatarios
                            adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                listaViewDePrestatarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        //Cuando seleccione un item de la lista
                        //Se muestra en el textview
                        text_prestatario.setText(adapter.getItem(position));
                        //Salimos del diálogo
                        dialog_prestatario.dismiss();
                    }
                });

            }
        });

        //En caso de seleccionar una fecha de entrega
        fecha_entrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inicializamos el calendario
                Calendar calendar = Calendar.getInstance();
                int año = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                //Inicializamos el diálogo para la fecha
                DatePickerDialog dialog_fecha = new DatePickerDialog(PrestamoActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, año, mes, dia);
                //Actualizamos su fondo transparente
                dialog_fecha.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //Mostramos el diálogo
                dialog_fecha.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                fecha_plazo_entrega = dia + "/" + (mes + 1) + "/" + año;
                fecha_entrega.setText(fecha_plazo_entrega);
            }
        };

    }

    //Validacación de la entrada colocada por el usuario
    public boolean ValidarEntrada(){
        String cantidad = editxtCantidadPrestamo.getText().toString();
        boolean flag = true;
        if (fecha_entrega.getText().equals("")){
            fecha_entrega.setError("Debe elegir la fecha de entrega");
            flag = false;
        }
        if (text_prestatario.getText().equals("")){
            fecha_entrega.setError("Debe elegir un prestatario existente");
            flag = false;
        }
        if(cantidad.equals("")){
            editxtCantidadPrestamo.setError("Debe ingresar la cantidad a prestar");
            flag = false;
        }
        if(Integer.parseInt(objeto.getCantidad()) - Integer.parseInt(cantidad) < 0  ){
            editxtCantidadPrestamo.setError("La cantidad ingresada supera su inventario.");
            flag = false;
        }

        return flag;
    }


    public void ConfirmarPrestamoClick(View view) {
        if (ValidarEntrada()){
            //En este caso aún no se ha devuelto el objeto por ende no se puede colocar fecha de devolución, ni el receptor
            Prestamo prestamo = new Prestamo(currentUser.getNombre() +" "+ currentUser.getApellido(), text_prestatario.getText().toString(), objeto.getKey(), fecha_prestamo, fecha_plazo_entrega, "", "");
            //Se añade el prestamo a la base de datos
            addDataPrestamo(prestamo, "LCC");
            //Se actualiza el objeto
            objeto.setEstado("Reservado");
            objeto.setLastFechaPrestamo(fecha_prestamo);
            objeto.setLastPrestatario(text_prestatario.getText().toString());
            objeto.setLastPrestamista(currentUser.getNombre() + " " + currentUser.getApellido());
            objeto.setCantidad(editxtCantidadPrestamo.getText().toString());
            //Se actualiza la base de datos
            SetDataInventario("LCC", objeto, objeto.getKey());
            Toast.makeText(getApplicationContext(), "Prestamo realizado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}