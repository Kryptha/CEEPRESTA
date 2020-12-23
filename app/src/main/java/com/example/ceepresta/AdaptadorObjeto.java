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

import java.util.ArrayList;
import java.util.List;

import Clases.Objeto;

/*Clase Adapatador del objeto donde ayuda a mostrar en pantalla el Recyclerview y la lista, sigue el modelo de imagen como cardview_objeto*/
public class AdaptadorObjeto extends RecyclerView.Adapter<AdaptadorObjeto.ImageViewHolder> implements Filterable
{
    private List<Objeto> lista_de_objetos;
    private  List<Objeto> infoFull;
    private OnItemClickListener mListener;

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


    public class ImageViewHolder extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public ImageView imageView;
        public TextView textViewName,  textViewCant, textViewCateg, textViewEstado, textViewFecha;

        public ImageViewHolder(View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(view.getContext(), DetallesObjeto_Activity.class);
                    intent.putExtra("nombre", lista_de_objetos.get(position).getNombre());
                    intent.putExtra("estado", lista_de_objetos.get(position).getEstado());
                    intent.putExtra("fechaRegistro", lista_de_objetos.get(position).getFechaRegistro());
                    intent.putExtra("Urlimage", lista_de_objetos.get(position).getUrlimage());
                    view.getContext().startActivity(intent);

                }
            });

            imageView = itemView.findViewById(R.id.id_imagen_tarjeta);
            textViewName = itemView.findViewById(R.id.id_nombre_objeto);
            textViewCant = itemView.findViewById(R.id.id_cantidad);
            textViewCateg = itemView.findViewById(R.id.id_categoria);
            textViewEstado = itemView.findViewById(R.id.id_estado);
            textViewFecha = itemView.findViewById(R.id.id_fechareg);

            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Action");
            MenuItem doWhatever = contextMenu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(mListener != null)
            {
                int position = getAdapterPosition();

                if(position != RecyclerView.NO_POSITION)
                {
                    switch (menuItem.getItemId())
                    {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onWhatEverClick(int position);
        void onDeleteClick(int position);
    }

    /*Función que reinicia la lista que hay que filtrar y debe mostrar en pantalla*/
    public void updateData (ArrayList<Objeto> Update){
        lista_de_objetos.clear();
        lista_de_objetos.addAll(Update);
        infoFull.clear();
        infoFull.addAll(Update);
        notifyDataSetChanged();
    }
}
