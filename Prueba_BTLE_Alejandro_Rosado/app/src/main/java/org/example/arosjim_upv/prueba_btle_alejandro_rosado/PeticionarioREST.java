package org.example.arosjim_upv.prueba_btle_alejandro_rosado;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import android.util.Log;

// ------------------------------------------------------------------------
// ------------------------------------------------------------------------
/**
 * @brief Clase que realiza peticiones HTTP REST de manera asíncrona.
 * @author Alejandro Rosado
 */
public class PeticionarioREST extends AsyncTask<Void, Void, Boolean> {

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    /**
     * @brief Interfaz para manejar la respuesta de una petición REST.
     * @author Alejandro Rosado
     */
    public interface RespuestaREST {
        void callback (int codigo, String cuerpo);
    }

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    private String elMetodo;
    private String urlDestino;
    private String elCuerpo = null;
    private RespuestaREST laRespuesta;

    private int codigoRespuesta;
    private String cuerpoRespuesta = "";

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    /**
     * @brief Realiza una petición REST.
     * @author Alejandro Rosado
     * @param metodo: String -> Método HTTP (GET, POST, etc.)
     * @param urlDestino: String -> URL de destino para la petición
     * @param cuerpo: String -> Cuerpo de la petición (puede ser null)
     * @param laRespuesta: RespuestaREST -> Callback para manejar la respuesta
     * Su flujo: Se configura la petición y se ejecuta en un hilo separado.
     * Este método inicia la ejecución de la tarea asíncrona para realizar la petición.
     */
    public void hacerPeticionREST (String metodo, String urlDestino, String cuerpo, RespuestaREST  laRespuesta) {
        this.elMetodo = metodo;
        this.urlDestino = urlDestino;
        this.elCuerpo = cuerpo;
        this.laRespuesta = laRespuesta;

        this.execute(); // otro thread ejecutará doInBackground()
    }

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    /**
     * @brief Inicializa una nueva instancia de PeticionarioREST.
     * @author Alejandro Rosado
     */
    public PeticionarioREST() {
        Log.d("clienterestandroid", "constructor()");
    }

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    /**
     * @brief Método que ejecuta la lógica de la petición en segundo plano.
     * @author Alejandro Rosado
     * @return Boolean -> Indica si la operación fue exitosa
     * Su flujo: Conecta a la URL, envía la petición y obtiene la respuesta.
     * Este método se ejecuta en un hilo separado y maneja la conexión HTTP.
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("clienterestandroid", "doInBackground()");

        try {

            // envio la peticion

            Log.d("clienterestandroid", "doInBackground() me conecto a >" + urlDestino + "<");

            URL url = new URL(urlDestino);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );
            connection.setRequestMethod(this.elMetodo);
            // connection.setRequestProperty("Accept", "*/*);

            // connection.setUseCaches(false);
            connection.setDoInput(true);

            if (!this.elMetodo.equals("GET") && this.elCuerpo != null ) {

                Log.d("clienterestandroid", "doInBackground(): no es get, pongo cuerpo");
                connection.setDoOutput(true);
                // si no es GET, pongo el cuerpo que me den en la peticion
                DataOutputStream dos = new DataOutputStream (connection.getOutputStream());
                dos.writeBytes(this.elCuerpo);
                dos.flush();
                dos.close();
            }

            // ya he enviado la peticion
            Log.d("clienterestandroid", "doInBackground(): peticion enviada ");

            // ahora obtengo la respuesta

            int rc = connection.getResponseCode();
            String rm = connection.getResponseMessage();
            String respuesta = "" + rc + " : " + rm;
            Log.d("clienterestandroid", "doInBackground() recibo respuesta = " + respuesta);
            this.codigoRespuesta = rc;

            try {

                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                Log.d("clienterestandroid", "leyendo cuerpo");
                StringBuilder acumulador = new StringBuilder ();
                String linea;
                while ( (linea = br.readLine()) != null) {
                    Log.d("clienterestandroid", linea);
                    acumulador.append(linea);
                }
                Log.d("clienterestandroid", "FIN leyendo cuerpo");

                this.cuerpoRespuesta = acumulador.toString();
                Log.d("clienterestandroid", "cuerpo recibido=" + this.cuerpoRespuesta);

                connection.disconnect();

            } catch (IOException ex) {
                // dispara excepcion cuando la respuesta REST no tiene cuerpo y yo intento getInputStream()
                Log.d("clienterestandroid", "doInBackground() : parece que no hay cuerpo en la respuesta");
            }

            return true; // doInBackground() termina bien

        } catch (Exception ex) {
            Log.d("clienterestandroid", "doInBackground(): ocurrio alguna otra excepcion: " + ex.getMessage());
        }

        return false; // doInBackground() NO termina bien
    } // ()

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    /**
     * @brief Método que se llama tras la ejecución de doInBackground().
     * @author Alejandro Rosado
     * @param comoFue: Boolean -> Indica el resultado de doInBackground()
     * Su flujo: Invoca el callback con el código y cuerpo de respuesta.
     * Este método se ejecuta en el hilo principal después de que se completa la tarea asíncrona.
     */
    protected void onPostExecute(Boolean comoFue) {
        // llamado tras doInBackground()
        Log.d("clienterestandroid", "onPostExecute() comoFue = " + comoFue);
        this.laRespuesta.callback(this.codigoRespuesta, this.cuerpoRespuesta);
    }

} // class


