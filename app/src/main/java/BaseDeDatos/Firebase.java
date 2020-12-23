package BaseDeDatos;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import Clases.Objeto;
import Clases.Prestatario;
import Clases.Usuario;

public class Firebase {
//Clase encargada de consumir los servicios de Google Firebase
//Identificador de cuenta de Firebase
    private static String FIREBASE_KEY = "AIzaSyCUovFoUcNIsvKV2AO4om8Gd94PefWIdlw";

      /**
     * Metodo para consumir un SERVICIO REST para registrar uns nueva cuenta
     * METHOD POST
     */
    public static boolean signUpAccount(String email,String password) throws Exception {

        boolean registro = false;
        // creando String para consumir servicio signUp
        String StringURL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="+FIREBASE_KEY;
        // Crenado string del Json que espera el servicio
        String postJsonData = "{\"email\":\""+email+"\",\"password\":\""+password+"\"}";
        // Consumiendo la API de Firebase
        HttpURLConnection con = httpUrlConnection(StringURL,"POST",postJsonData);

        int responseCode = con.getResponseCode();
        // El código 200 indica que se pudo crear la cuenta del usuario
        if(responseCode == 200){
            // retorna un String con la respuesta con varios datos del consumo del servicio
            String response = responseHttp(con).toString();
            // Creando objeto Json para obtenre el localId de la cuenta
            JSONObject jsonObj = new JSONObject(response);

            String idUser = jsonObj.getString("localId");

            Log.i("ID_USER",idUser);
            //JSONObject jsonObjbody = new JSONObject(body);
            //productID = jsonObjbody.getString("productID");
            registro = true;
        }

        return registro;
    }

    /**
     * Metodo para consumir un SERVICIO REST que valida al usuario para ingresar a la App
     * METHOD POST
     */
    public static ArrayList<String> signInAccount(String email, String password) throws Exception {

        String registrado = "false";
        ArrayList<String> result = new ArrayList<>();
        result.add(registrado);
        String StringURL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="+FIREBASE_KEY;
        String postJsonData = "{\"email\":\""+email+"\",\"password\":\""+password+"\"}";

        HttpURLConnection con = httpUrlConnection(StringURL,"POST",postJsonData);

        int responseCode = con.getResponseCode();

        if(responseCode == 200){

            String response = responseHttp(con).toString();

            JSONObject jsonObj = new JSONObject(response);

            String idUser = jsonObj.getString("localId");

            registrado = jsonObj.getString("registered");

            String token = jsonObj.getString("idToken");

            /*La posición 0 es que si esta o no registrado, la posición 1 es el UID del usuario*/
            result.set(0, registrado);
            result.add(idUser);

            Log.i("ID_USER","IdUsuario: "+idUser);
            Log.i("ID_USER","Registrado: "+registrado);
            Log.i("ID_USER","Token: "+token);
            //JSONObject jsonObjbody = new JSONObject(body);
            //productID = jsonObjbody.getString("productID");
        }

        return result;

    }

    /*Función que agrega un objeto al inventario de una carrera "InventarioID" */

    public static boolean addDateInventario (String inventarioID, Objeto objeto){
        DatabaseReference dataBaseRef;
        try {
            dataBaseRef = FirebaseDatabase.getInstance().getReference().child("Inventarios").child(inventarioID);
            String UploadId = dataBaseRef.push().getKey();
            dataBaseRef.child(UploadId).setValue(objeto);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /*Función que agrega un prestatario a la BD con la llave inventarioID el cual tendrá como llave el RUN */

    public static boolean addDatePrestatario (String inventarioID, Prestatario prestatario, String run){
        DatabaseReference dataBaseRef;
        try {
            dataBaseRef = FirebaseDatabase.getInstance().getReference().child("Prestatarios").child(inventarioID);
            dataBaseRef.child(run).setValue(prestatario);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


// metood que hace la conexion con los servicios a través de una URL
    private static HttpURLConnection httpUrlConnection(String stringURL, String method, String postJsonData) throws IOException {
        URL url = new URL(stringURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Setting basic post request
        con.setRequestMethod(method);
        //con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");

        if(method.equals("POST")) {
            // Send post request
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postJsonData);
            wr.flush();
            wr.close();
        }

        return con;
    }
// lee la respuesta que entrega el servicio
    private static StringBuffer responseHttp(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

        return response;
    }
}
