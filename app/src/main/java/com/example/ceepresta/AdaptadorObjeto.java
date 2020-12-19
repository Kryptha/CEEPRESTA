package com.example.ceepresta;

import android.content.Context;
import android.media.Image;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import Clases.Objeto;

/*Clase Adapatador del objeto donde ayuda a mostrar en pantalla el Recyclerview y la lista, sigue el modelo de imagen como cardview_objeto*/
public class AdaptadorObjeto extends RecyclerView.Adapter<AdaptadorObjeto.ImageViewHolder>
{
    private Context mContext;
    private List<Objeto> lista_de_objetos;
    private OnItemClickListener mListener;

    public AdaptadorObjeto(Context context, List<Objeto> uploads)
    {
        mContext = context;
        lista_de_objetos = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_objeto, parent, false);

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

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public ImageView imageView;
        public TextView textViewName,  textViewCant, textViewCateg, textViewEstado, textViewFecha;

        public ImageViewHolder(View itemView)
        {
            super(itemView);

            imageView = itemView.findViewById(R.id.id_imagen_tarjeta);
            textViewName = itemView.findViewById(R.id.id_nombre_objeto);
            textViewCant = itemView.findViewById(R.id.id_cantidad);
            textViewCateg = itemView.findViewById(R.id.id_categoria);
            textViewEstado = itemView.findViewById(R.id.id_estado);
            textViewFecha = itemView.findViewById(R.id.id_fechareg);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if(mListener != null)
            {
                int position = getAdapterPosition();

                if(position != RecyclerView.NO_POSITION)
                {
                    mListener.onItemClick(position);
                }
            }

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

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }
}
