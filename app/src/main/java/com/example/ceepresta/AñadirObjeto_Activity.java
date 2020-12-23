package com.example.ceepresta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import static BaseDeDatos.Firebase.addDateInventario;
import java.util.Calendar;

import Clases.Objeto;

/*
Actividad para añadir un objeto
 */

public class AñadirObjeto_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /* Declaración de variable "flag" para la elegir la imagen en galeria */
    public static final int PICK_IMAGE_REQUEST = 1;

    /* Declaración de variables visuales */
    private EditText nameObjeto, cantidadObjeto;
    private Uri imageUrlObjeto;
    private ImageView imageViewObjeto;
    private Spinner spinnerEstado, spinnerCategoria;
    private ProgressBar progressBarUpload;
    private Button btnSave, btnVerinventario, btnUploadImage;

    // Aplicado a LCC, pero prontó debería ser recibido desde el momento en que ingreso el usuario un dato
    private String inventarioID = "LCC"; //TESTING

    // Variable para tomar la referencia del Storage (Aquí se guardaran las imágenes que se suban)
    private StorageReference storagefeRef;
    // Flag para averiguar que si se esta subiendo la imágen o no (Cuando hacen multiples click, sin esto se sube muchas veces)
    private StorageTask uploadTask;

    // Obtener datos esenciales para guardar el objeto
    private String categoriaObjeto = "", estadoObjeto = "";
    private String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_objeto);

        //Obtención de variables de lo visual
        nameObjeto = findViewById(R.id.editxt_name_object);
        cantidadObjeto = findViewById(R.id.editxt_cant_object);
        spinnerEstado = findViewById(R.id.spinner_estado);
        spinnerCategoria = findViewById(R.id.spinner_categoria);
        imageViewObjeto = findViewById(R.id.imgvw_image_object);
        progressBarUpload = findViewById(R.id.progressbar_image);
        btnUploadImage = findViewById(R.id.btn_choose_image);
        btnSave = findViewById(R.id.btn_save_añadirObjeto);
        btnVerinventario = findViewById(R.id.btn_verInventario);

        //Obtención de la fecha
        Calendar hoy = Calendar.getInstance();
        fecha = String.valueOf(hoy.get(Calendar.DAY_OF_MONTH)) + String.valueOf(hoy.get(Calendar.MONTH)+1) + String.valueOf(hoy.get(Calendar.YEAR));

        //Declaración del storage de imagenes
        storagefeRef = FirebaseStorage.getInstance().getReference("imagenObjetos");

        //En caso de que aprete el botón para subir imagen (o elegir imágen)
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Función para abrir la galeria y elegir una imágen
                openFileChooser();
            }
        });

        //En caso de subir la imágen obtenida a la BD - Storage.
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //En caso de que haga multiples click.
                if (uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(AñadirObjeto_Activity.this, "Se esta subiendo el objeto", Toast.LENGTH_LONG).show();
                }
                else {
                    //Validación de entrada y subida de objeto
                    if(validarEntrada()){
                        uploadObject();
                    }
                }
            }
        });

        btnVerinventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMostrarInventarioActivity();
            }
        });

        //Adapatador para utilizar el spinner de categoria (Aún no se usa de la BD Categoria esta predeterminado en values -> string.xml -> categorias
        ArrayAdapter<CharSequence> adapterSpinnerCategory = ArrayAdapter. createFromResource(this, R.array.categorias, android.R.layout.simple_spinner_item);
        adapterSpinnerCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterSpinnerCategory);
        spinnerCategoria.setOnItemSelectedListener(this);

        //Adapatador para utilizar el spinner de Estados esta predeterminado en values -> string.xml -> estadosRegister
        ArrayAdapter<CharSequence> adapterSpinnerState = ArrayAdapter. createFromResource(this, R.array.estadosRegister, android.R.layout.simple_spinner_item);
        adapterSpinnerCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterSpinnerState);
        spinnerEstado.setOnItemSelectedListener(this);



    }

    //Función que valida el nombre y cantidad del objeto. Muestra un mensaje en caso de que no haya ingresado nada
    private boolean validarEntrada(){
        boolean flag = true;
        if(nameObjeto.getText().toString().equals("")){
            nameObjeto.setError("Este campo es obligatorio");
            flag = false;
        }
        if (cantidadObjeto.getText().toString().equals("")){
            cantidadObjeto.setError("Este campo es obligatorio");
            flag = false;
        }
        return  flag;
    }

    //Abre la intención de galeria para optar por una imagen
    private void openFileChooser(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Cuando ya haya legido una imagen se obtiene su URL.
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData()!= null) {
            imageUrlObjeto = data.getData();
            //Se updatte el imageView para ver la imágen seleccionada
            Picasso.get().load(imageUrlObjeto).into(imageViewObjeto);
        }

    }


    /*Obtener la extensión de la imagen */
    private  String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    // Función que sube la imagen a la BD y Storage de Firebase
    private void uploadObject(){
        //El URLimage no debe ser nulo (Debe elegir una imágen)
        if(imageUrlObjeto != null){
            //Se obtiene una ID de milisegundos parra guardarla con la extensión (ej: 1234663.png)
            StorageReference fileReference = storagefeRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUrlObjeto));
            uploadTask = fileReference.putFile(imageUrlObjeto)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    //Inicializar que de se esta subiendo la imágen
                                    progressBarUpload.setProgress(0);
                                }
                            };

                            //Se obtiene la Url exacta del Storage para agregarla a la BD
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();

                            //Mientrás seleccione una categoria
                            if (!categoriaObjeto.equals("") && !estadoObjeto.equals("")){
                                //Se crea el objeto con los datos necesarios
                                Objeto objeto = new Objeto(nameObjeto.getText().toString().trim(), estadoObjeto , fecha
                                        , downloadUrl.toString(), cantidadObjeto.getText().toString().trim(), categoriaObjeto);

                                //Se añade el objeto al inventarioID, si la base de datos falla manda un "false"
                                if(addDateInventario(inventarioID, objeto)){
                                    Toast.makeText(getApplicationContext(), "Objeto añadido", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Existe un error en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Debe seleccionar una opción válida", Toast.LENGTH_LONG).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //En caso de fallo en el Storage, avisa en pantalla.
                            Toast.makeText(AñadirObjeto_Activity.this, e.getMessage(), Toast.LENGTH_LONG). show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            //Nos otorga el progreso de la barra de tareas y su carga
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBarUpload.setProgress((int) progress);
                        }
                    });

        }
        else {
            Toast.makeText(this, "Sin imagen seleccionada", Toast.LENGTH_LONG).show();
        }
    }

    // Funciones para cuando seleccionan datos en el Spinner.
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            //De acá se obtienen los datos del spinner
            categoriaObjeto = spinnerCategoria.getSelectedItem().toString();
            estadoObjeto = spinnerEstado.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //En casa de que no selecciona nada, por cualquier motivo sea
        Toast.makeText(getApplicationContext(), "Debe seleccionar una opción", Toast.LENGTH_LONG).show();
    }

    //Función de abrir la actibidad de "Mostrar inventarior"
    public void openMostrarInventarioActivity()
    {
        Intent intent = new Intent(this, Mostrar_Inventario_Activity.class);
        startActivity(intent);
    }
}