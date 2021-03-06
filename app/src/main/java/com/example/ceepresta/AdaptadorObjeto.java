package com.example.ceepresta;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Clases.Objeto;
import Clases.Usuario;

/*Clase Adapatador del objeto donde ayuda a mostrar en pantalla el Recyclerview y la lista, sigue el modelo de imagen como cardview_objeto*/
public class AdaptadorObjeto extends RecyclerView.Adapter<AdaptadorObjeto.ImageViewHolder> implements Filterable
{
    private List<Objeto> lista_de_objetos;
    private  List<Objeto> infoFull;
    private  Usuario currentUser;

    public AdaptadorObjeto(List<Objeto> uploads)
    {
        lista_de_objetos = uploads;
        /*Lista identica a info pero puede usarse de manera independiente a info*/
        infoFull = new ArrayList<Objeto>(uploads);

    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_objeto, parent, false);

        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position)
    {
        Objeto subirActual = lista_de_objetos.get(position);

        holder.textViewName.setText(subirActual.getNombre());
        holder.textViewCant.setText("Cantidad: " + subirActual.getCantidad());
        holder.textViewCateg.setText("Categoría: " + subirActual.getCategoria());
        holder.textViewEstado.setText("Estado: " + subirActual.getEstado());
        holder.textViewFecha.setText("Fecha de registro: " + subirActual.getFechaRegistro());
        Picasso.get()
                .load(subirActual.getUrlimage())
                .placeholder(R.mipmap.ic_launcher_cee)
                .fit()
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return lista_de_objetos.size();
    }

    // Implementación del filtrado
    @Override
    public Filter getFilter() {

        return filtro;
    }

    /* Función de búsqueda real y filtrado */
    private Filter filtro = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Objeto> lista_filtrada = new ArrayList<>();

            /*Si el string 'charSequence llega nulo o es de largo 0,
            entonces se muestra la lista completa'*/
            if(charSequence == null || charSequence.length() == 0)
            {
                lista_filtrada.addAll(infoFull);
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
                for(Objeto objeto_search : infoFull)
                {
                    /*Si el nombre la búsqueda (patronDeFiltrado) está en lista
                     * entonces se agrega a la lista filtrada*/
                    if(objeto_search.getNombre().toLowerCase().contains(patronDeFiltrado))
                    {
                        lista_filtrada.add(objeto_search);
                    }
                }
            }

            FilterResults resultado = new FilterResults();
            resultado.values = lista_filtrada;
            /*Se retorna los resultados del método performFiltering hacia publishResults*/
            return resultado;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            /*Se limpia la lista original porque solo se quiere mostrar los resultados filtrados*/
            lista_de_objetos.clear();
            lista_de_objetos.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView textViewName,  textViewCant, textViewCateg, textViewEstado, textViewFecha;

        public ImageViewHolder(View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(view.getContext(), DetallesObjeto_Activity.class);
                    intent.putExtra("lista_de_objetos", lista_de_objetos.get(position));
                    intent.putExtra("User", currentUser);
                    view.getContext().startActivity(intent);
                }
            });

            imageView = itemView.findViewById(R.id.id_imagen_tarjeta);
            textViewName = itemView.findViewById(R.id.id_nombre_objeto);
            textViewCant = itemView.findViewById(R.id.id_cantidad);
            textViewCateg = itemView.findViewById(R.id.id_categoria);
            textViewEstado = itemView.findViewById(R.id.id_estado);
            textViewFecha = itemView.findViewById(R.id.id_fechareg);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /*Función que reinicia la lista que hay que filtrar y debe mostrar en pantalla*/
    public void updateData(ArrayList<Objeto> Update)
    {
        lista_de_objetos.clear();
        lista_de_objetos.addAll(Update);
        infoFull.clear();
        infoFull.addAll(Update);
        notifyDataSetChanged();
    }

    public void setUser(Usuario usuario){
        currentUser = usuario;
    }
}
