package com.example.ceepresta;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Clases.Objeto;
import Clases.Prestatario;

/*Clase Adapatador del objeto donde ayuda a mostrar en pantalla el Recyclerview y la lista, sigue el modelo de imagen como cardview_objeto*/
public class AdaptadorPrestatario extends RecyclerView.Adapter<AdaptadorPrestatario.ImageViewHolder> implements Filterable
{
    private List<Prestatario> lista_de_prestatarios;
    private List<Prestatario> infoFull;

    public AdaptadorPrestatario(List<Prestatario> uploads)
    {
        lista_de_prestatarios = uploads;
        /*Lista identica a info pero puede usarse de manera independiente a info*/
        infoFull = new ArrayList<Prestatario>(uploads);

    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.prestatario_cardview, parent, false);

        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position)
    {
        Prestatario subirActual = lista_de_prestatarios.get(position);

        holder.textViewName.setText(subirActual.getNombre() + " "+  subirActual.getApellido());
        holder.textViewRut.setText(subirActual.getRut());
        if(subirActual.getCarrera().equals("")){
            holder.textViewCarrera.setText("Carrera sin definir");
        }
        else{
            holder.textViewCarrera.setText(subirActual.getCarrera());

        }

        if(subirActual.getTelefono().equals("")){
            holder.textViewTel.setText("Teléfono sin definir");
        }
        else{
            holder.textViewTel.setText(subirActual.getTelefono());

        }
        holder.textViewCorreo.setText(subirActual.getCorreo());
        /* En caso de a futuro querer guardar la imagen o subirla, colocar esto
        Picasso.get()
                .load(Urlimage)
                .fit()
                .centerCrop()
                .into(holder.imageView);*/

    }

    @Override
    public int getItemCount() {
        return lista_de_prestatarios.size();
    }

    // Implementación del filtrado
    @Override
    public Filter getFilter() {
        return filtro;
    }

    /* Función de búsqueda real y filtrado */
    private Filter filtro = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            List<Prestatario> lista_filtrada_pres = new ArrayList<>();

            /*Si el string 'charSequence llega nulo o es de largo 0,
            entonces se muestra la lista completa'*/
            if(charSequence == null || charSequence.length() == 0)
            {
                lista_filtrada_pres.addAll(infoFull);
            }
            else
            {
                /*Se pasa lo ingresado en la barra de búsqueda a minúsculas y se quitan
                 * los espacios vacíos*/
                String patronDeFiltrado = charSequence.toString().toLowerCase().trim();
                /*Equivalencia a la (Producto producto : infoFull)
                 * for(int i = 0; i < Info.size(); i++)
                 * {
                 *       Producto producto_search = Info.get(i);
                 * }
                 * */
                for(Prestatario prestatario_search : infoFull)
                {
                    /*Si el nombre o apellido la búsqueda (patronDeFiltrado) está en lista
                     * entonces se agrega a la lista filtrada*/
                    if(prestatario_search.getNombre().toLowerCase().contains(patronDeFiltrado) ||
                            prestatario_search.getApellido().toLowerCase().contains(patronDeFiltrado))
                    {
                        lista_filtrada_pres.add(prestatario_search);
                    }
                }
            }

            FilterResults resultado = new FilterResults();
            resultado.values = lista_filtrada_pres;
            /*Se retorna los resultados del método performFiltering hacia publishResults*/
            return resultado;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            /*Se limpia la lista original porque solo se quiere mostrar los resultados filtrados*/
            lista_de_prestatarios.clear();
            lista_de_prestatarios.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView textViewName,  textViewRut, textViewCarrera, textViewTel, textViewCorreo;
        public ImageButton deleteUser;
        public ImageViewHolder(View itemView)
        {
            super(itemView);

            //Botón para eliminar un usuario
            deleteUser = itemView.findViewById(R.id.btn_delete_prestatario);

            //En caso de seleccionarr el botón
            deleteUser.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    //Se inicializa el diálogo
                    TextView txt_dialogo;
                    //Se obtiene la posición donde se hizo click
                    int position = getAdapterPosition();
                    //Se declará el diálogo
                    Dialog dialog = new Dialog(view.getContext());
                    //Obtenemos los botones
                    Button btn_borrar, btn_cancelar;
                    //Obtenemos la referencia de la base de datos
                    DatabaseReference ref_db = FirebaseDatabase.getInstance().getReference().child("Prestatarios").child("LCC");
                    //Actualizamos el diálogo
                    dialog.setContentView(R.layout.dialogo_eliminar_objeto);
                    txt_dialogo = dialog.findViewById(R.id.dialogo_txt_small);
                    btn_borrar =  dialog.findViewById(R.id.btn_dialogo_borrar);
                    btn_cancelar = dialog.findViewById(R.id.btn_dialogo_cancelar);
                    //Advertimos al usuario de eliminar al usuario
                    txt_dialogo.setText("¿Realmente quieres borrar " + lista_de_prestatarios.get(position).getNombre() +
                            " "+ lista_de_prestatarios.get(position).getApellido() + "? Este proceso no es reversible");
                    //En caso de borrar
                    btn_borrar.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {  //Obtenelo la llave para borrar
                            String llave_seleccionada = lista_de_prestatarios.get(position).getRut();
                            Log.i("RUT", llave_seleccionada);

                            //Removemos el valor de la base de datos
                            ref_db.child(llave_seleccionada).removeValue();
                            //Advertimos al usuario
                            Toast.makeText(view.getContext(), lista_de_prestatarios.get(position).getNombre()
                                    +" "+ lista_de_prestatarios.get(position).getApellido() + " ha sido eliminado", Toast.LENGTH_SHORT).show();
                            //Cerramos el diálogo
                            dialog.dismiss();
                        }
                    });
                    //En caso de cancelar, simplemente cerramos el diálogo
                    btn_cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    //Configuración visual del diálogo
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });

            imageView = itemView.findViewById(R.id.id_foto_prestatario);
            textViewName = itemView.findViewById(R.id.id_nombre_prestatario);
            textViewRut = itemView.findViewById(R.id.rut_prestatario);
            textViewCarrera = itemView.findViewById(R.id.id_carrera_prestatario);
            textViewTel = itemView.findViewById(R.id.id_telefono_prestatario);
            textViewCorreo = itemView.findViewById(R.id.id_correo_prestatario);
        }
    }

    /*Función que reinicia la lista que hay que filtrar y debe mostrar en pantalla*/
    public void updateData (ArrayList<Prestatario> Update)
    {
        lista_de_prestatarios.clear();
        lista_de_prestatarios.addAll(Update);
        infoFull.clear();
        infoFull.addAll(Update);
        notifyDataSetChanged();
    }
}
