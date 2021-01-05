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
    private List<Prestamo> listaPrestamos;
    private List<Prestamo> infoFull;
    private List<Objeto> listaObjetos = new ArrayList<Objeto>();
    private Usuario currentUser;
    private Objeto objetoAux;

    public AdaptadorPrestamo(List<Prestamo> uploads, Usuario usuario)
    {
        listaPrestamos = uploads;
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

        ref_db = FirebaseDatabase.getInstance().getReference().child("Inventarios").child(currentUser.getInventarioid()).child(listaPrestamos.get(position).getObjetoID());

        db_listener = ref_db.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot)
        {
                Objeto objeto= snapshot.getValue(Objeto.class);
                listaObjetos.add(position, objeto);
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

        Prestamo subirActual = listaPrestamos.get(position);

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

        //En caso de que ya se haya devuelto, entonces se desactiva el botón
        if(!listaPrestamos.get(position).getFechaDevolucion().equals("")){
            holder.btn_devolver.setEnabled(false);
            holder.btn_devolver.setImageResource(R.drawable.check_icon);
        }
    }

    @Override
    public int getItemCount() {
        return listaPrestamos.size();
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
            listaPrestamos.clear();
            listaPrestamos.addAll((ArrayList) filterResults.values);
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
                    //Obtenemos la posición
                    int position = getAdapterPosition();
                    //Para actualizar el préstamos ya que se ha devuelto, debemo agregarle la fecha y el receptor.
                    Prestamo prestamo = new Prestamo(listaPrestamos.get(position).getPrestamistaID(), listaPrestamos.get(position).getPrestatarioID(), listaPrestamos.get(position).getObjetoID(),
                            listaPrestamos.get(position).getFechaPrestamo(), listaPrestamos.get(position).getFechaEntrega(),
                            fecha_entrega , currentUser.getNombre() + " " + currentUser.getApellido(), listaPrestamos.get(position).getCantidad());
                    //Buscar el objeto en la BD para actualizarlo
                    objetoAux = listaObjetos.get(position);
                    //Actualizando la cantidad sumando la actual con la que se prestó
                    String cant = String.valueOf(Integer.parseInt(objetoAux.getCantidad()) + Integer.parseInt(listaPrestamos.get(position).getCantidad()));
                    //En caso de que no exista algun elemento antes del prestamos el objeto dice "Reservado", más ahora que lo devolvieron debe decir "Disponible".
                    if(objetoAux.getCantidad().equals("0")){
                        objetoAux.setEstado("Disponible");
                    }
                    //Actualización del objeto
                    objetoAux.setCantidad(cant);
                    objetoAux.setLastFechaDevolución(fecha_entrega);
                    objetoAux.setLastReceptor(currentUser.getNombre() + " " + currentUser.getApellido());

                    try
                    {
                        //Actualización de información de préstamo
                        DatabaseReference ref_db_set = FirebaseDatabase.getInstance().getReference().child("Prestamos").child(currentUser.getInventarioid()).child(listaPrestamos.get(position).getKey());
                        ref_db_set.setValue(prestamo);

                        //Se actualiza la BD del objeto
                        ref_db_set = FirebaseDatabase.getInstance().getReference().child("Inventarios").child(currentUser.getInventarioid()).child(listaPrestamos.get(position).getObjetoID());
                        ref_db_set.setValue(objetoAux);
                        Toast.makeText(view.getContext(), "Objeto devuelto", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){

                    }
                }
            });




        }
    }



    public void setUser(Usuario usuario){
        currentUser = usuario;
    }
}