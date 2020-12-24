package com.example.ceepresta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Clases.Usuario;

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.ImageViewHolder> implements Filterable {

    private List<Usuario> lista_de_usuarios;
    private List<Usuario> infoFull;

    public AdaptadorUsuarios(List<Usuario> uploads)
    {
        lista_de_usuarios = uploads;
        /*Lista identica a info pero puede usarse de manera independiente a info*/
        infoFull = new ArrayList<Usuario>(uploads);

    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView textViewName, textViewCorreo, textViewRol;

        public ImageViewHolder(View itemView)
        {
            super(itemView);

            imageView = itemView.findViewById(R.id.id_foto_usuario);
            textViewName = itemView.findViewById(R.id.id_nombre_usuario);
            textViewCorreo = itemView.findViewById(R.id.correo_usuario);
            textViewRol = itemView.findViewById(R.id.id_rol_usuario);

        }
    }


    @Override
    public Filter getFilter() {
        return filtro;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuario_cardview, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Usuario subirActual = lista_de_usuarios.get(position);

        holder.textViewName.setText(subirActual.getNombre() + " "+  subirActual.getApellido());
        holder.textViewCorreo.setText(subirActual.getCorreo());
        holder.textViewRol.setText(setRol(subirActual.getRol(), subirActual.getGenero()));
        /* En caso de a futuro querer guardar la imagen o subirla, colocar esto
        Picasso.get()
                .load(Urlimage)
                .fit()
                .centerCrop()
                .into(holder.imageView);*/

    }

    @Override
    public int getItemCount() {
        return lista_de_usuarios.size();
    }

    /* Función de búsqueda real y filtrado */
    private Filter filtro = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            List<Usuario> lista_filtrada_pres = new ArrayList<>();

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
                for(Usuario usuario_search : infoFull)
                {
                    /*Si el nombre o apellido la búsqueda (patronDeFiltrado) está en lista
                     * entonces se agrega a la lista filtrada*/
                    if(usuario_search.getNombre().toLowerCase().contains(patronDeFiltrado) ||
                            usuario_search.getApellido().toLowerCase().contains(patronDeFiltrado))
                    {
                        lista_filtrada_pres.add(usuario_search);
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
            lista_de_usuarios.clear();
            lista_de_usuarios.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public String setGender(String value){
        if (value.equals("F")){
            return "Femenino";
        }
        else if (value.equals("M")){
            return "Masculino";
        }
        else {
            return "No binario";
        }
    }

    public String setRol(String value, String gender){
        if (value.equals("cee")){
            return "Centro de Estudiantes";
        }
        else if (value.equals("admin")){
            if(gender.equals("F")){
                return "Administradora";
            }
            else {
                return "Administrador";
            }
        }
        else {
            if(gender.equals("F")){
                return "Delegada";
            }
            else if (gender.equals("M")){
                return "Delegado";
            }
            else {
                return "Delegadx";
            }
        }
    }

}

