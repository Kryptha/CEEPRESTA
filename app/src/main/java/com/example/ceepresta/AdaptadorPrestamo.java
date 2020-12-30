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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Clases.Objeto;
import Clases.Prestamo;
import Clases.Usuario;

/*Clase Adapatador del objeto donde ayuda a mostrar en pantalla el Recyclerview y la lista, sigue el modelo de imagen como cardview_objeto*/
public class AdaptadorPrestamo extends RecyclerView.Adapter<AdaptadorPrestamo.ImageViewHolder> implements Filterable
{
    private List<Prestamo> lista_de_objetos;
    private List<Prestamo> infoFull;
    private Usuario currentUser;

    public AdaptadorPrestamo(List<Prestamo> uploads, Usuario usuario)
    {
        lista_de_objetos = uploads;
        /*Lista identica a info pero puede usarse de manera independiente a info*/
        infoFull = new ArrayList<Prestamo>(uploads);
        currentUser = usuario;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_prestamo, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position)
    {
        DatabaseReference ref_db;
        ValueEventListener db_listener;

        ref_db = FirebaseDatabase.getInstance().getReference().child("Inventarios").child(currentUser.getInventarioid()).child(lista_de_objetos.get(position).getObjetoID());

        db_listener = ref_db.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot)
        {
                Objeto objeto= snapshot.getValue(Objeto.class);
                Picasso.get()
                .load(objeto.getUrlimage())
                .placeholder(R.mipmap.ic_launcher_cee)
                .fit()
                .centerCrop()
                .into(holder.imageView);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            //Se muestra el error en caso de cualquier eventualidad
            Log.i("error_adaptadorPrestamo", error.getMessage());
        }
        });

        Prestamo subirActual = lista_de_objetos.get(position);

        holder.prestamistaTview.setText("Prestamista: " + subirActual.getPrestamistaID());
        holder.prestatarioTview.setText("Prestatario: " + subirActual.getPrestatarioID());
        holder.fPrestamoTview.setText("Fecha Préstamo: " + subirActual.getFechaPrestamo());
        holder.fEntregaTview.setText("Fecha Entrega: " + subirActual.getFechaEntrega());
        holder.cantidadTview.setText("Cantidad: " + subirActual.getCantidad());

        if(subirActual.getFechaDevolucion().equals(""))
            holder.fDevolucionTview.setText("Sin fecha de devolución");
        else
            holder.fDevolucionTview.setText("Fecha Devolución: " + subirActual.getFechaDevolucion());

        if(subirActual.getReceptorID().equals(""))
            holder.receptorTview.setText("Sin receptor");
        else
            holder.receptorTview.setText("Receptor: " +  subirActual.getReceptorID());
    }

    @Override
    public int getItemCount() {
        return lista_de_objetos.size();
    }

    // Implementación del filtrado
    @Override
    public Filter getFilter()
    {
        return filtro;
    }

    /* Función de búsqueda real y filtrado */
    private Filter filtro = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            List<Prestamo> lista_filtrada = new ArrayList<>();

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
                for(Prestamo prestamo_search : infoFull)
                {
                    /*Si el nombre la búsqueda (patronDeFiltrado) está en lista
                     * entonces se agrega a la lista filtrada*/
                    if(prestamo_search.getPrestatarioID().toLowerCase().contains(patronDeFiltrado)
                            || prestamo_search.getPrestamistaID().toLowerCase().contains(patronDeFiltrado)
                            || prestamo_search.getObjetoID().toLowerCase().contains(patronDeFiltrado))
                    {
                        lista_filtrada.add(prestamo_search);
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


    public class ImageViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView, btn_devolver;
        public TextView prestamistaTview, prestatarioTview, fPrestamoTview, fEntregaTview, fDevolucionTview, receptorTview, cantidadTview;

        Calendar hoy = Calendar.getInstance();

        public ImageViewHolder(View itemView)
        {
            super(itemView);


            imageView = itemView.findViewById(R.id.id_imagen_prestamo);
            btn_devolver = itemView.findViewById(R.id.id_imagen_devolver);
            cantidadTview = itemView.findViewById(R.id.idCV_cantidad);
            prestamistaTview = itemView.findViewById(R.id.idCV_prestamista);
            prestatarioTview = itemView.findViewById(R.id.idCV_prestatario);
            fPrestamoTview = itemView.findViewById(R.id.idCV_fechaPrestamo);
            fEntregaTview = itemView.findViewById(R.id.idCV_fechaEntrega);
            fDevolucionTview = itemView.findViewById(R.id.idCV_fechaDevolucion);
            receptorTview = itemView.findViewById(R.id.idCV_receptor);


            String fecha_entrega = String.valueOf(hoy.get(Calendar.DAY_OF_MONTH)) + "/" +  String.valueOf(hoy.get(Calendar.MONTH)+1) + "/" + String.valueOf(hoy.get(Calendar.YEAR));
            btn_devolver.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getAdapterPosition();
                    //Para actualizar el préstamos ya que se ha devuelto, debemo agregarle la fecha y el receptor.
                    Prestamo prestamo = new Prestamo(lista_de_objetos.get(position).getPrestamistaID(), lista_de_objetos.get(position).getPrestatarioID(), lista_de_objetos.get(position).getObjetoID(),
                            lista_de_objetos.get(position).getFechaPrestamo(), lista_de_objetos.get(position).getFechaEntrega(),
                            fecha_entrega , currentUser.getNombre() + " " + currentUser.getApellido(), lista_de_objetos.get(position).getCantidad());
                    //Actualizar objeto
                     
                    try
                    {
                        DatabaseReference ref_db_set = FirebaseDatabase.getInstance().getReference().child("Prestamos").child(currentUser.getInventarioid()).child(lista_de_objetos.get(position).getKey());
                        ref_db_set.setValue(prestamo);
                        Toast.makeText(view.getContext(), "Objeto devuelto", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){

                    }
                }
            });




        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /*Función que reinicia la lista que hay que filtrar y debe mostrar en pantalla*/
    public void updateData(ArrayList<Prestamo> Update)
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