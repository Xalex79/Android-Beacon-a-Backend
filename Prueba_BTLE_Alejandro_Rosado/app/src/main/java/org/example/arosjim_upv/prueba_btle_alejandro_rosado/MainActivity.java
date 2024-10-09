package org.example.arosjim_upv.prueba_btle_alejandro_rosado;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.example.arosjim_upv.prueba_btle_alejandro_rosado.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    private TextView laMedida;
    private TextView laFecha;
    private Button elBotonEnviar;
    private FusedLocationProviderClient fusedLocationClient;
    private Location localizacion;
    private TextView laLocaliz;
    private TextView laTemperatura;

    private int major;
    private int minor;
    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.laMedida = (TextView) findViewById(R.id.elTexto);
        this.laFecha = (TextView) findViewById(R.id.laFecha);
        this.elBotonEnviar = (Button) findViewById(R.id.botonEnviar);
        this.laLocaliz = (TextView) findViewById(R.id.laLocaliz);
        this.laTemperatura = (TextView) findViewById(R.id.laTemperatura);

        inicializarBlueTooth();
        // -------------------------------------------------------------------------------
        // OBTENER LOCALIZACIÓN
        // -------------------------------------------------------------------------------
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, (OnSuccessListener<Location>) location -> {
                    // Got last known location. In some rare situations this can be null.
                    localizacion = location;
                    if (location != null) {
                        // Logic to handle location object
                    }
                });
        Log.d("clienterestandroid", "fin onCreate()");
        // -------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------
    }

    //----------------------------------------------------------------------------------------------
    // API REST
    //----------------------------------------------------------------------------------------------

    /**
     * @brief Función de devolución de llamada para el clic del botón "Recibir".
     * @author Alejandro Rosado
     * @param quien La vista que activó el evento de clic. (No se utiliza)
     * @return none
     *
     * Esta función se llama cuando se hace clic en el botón "Recibir".
     * Crea un nuevo objeto PeticionarioREST y realiza una solicitud GET
     * a la URL especificada para recuperar los datos de las mediciones del sensor.
     * Luego, la respuesta se procesa para actualizar la IU con los últimos
     * valores de concentración, temperatura, fecha, ubicación y barra de progreso.
     */
    public void boton_recibir_pulsado(View quien) {
        Log.d("clienterestandroid", "boton_enviar_pulsado");
        this.laMedida.setText("pulsado");

        // Hay que crear uno nuevo cada vez
        PeticionarioREST elPeticionario = new PeticionarioREST();

        elPeticionario.hacerPeticionREST("GET", "http://192.168.32.247:3000/api/v1/mediciones/", null,
                new PeticionarioREST.RespuestaREST() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        try {
                            JSONArray jsonArray = new JSONArray(cuerpo);

                            // Obtener el último objeto del array (la medición más reciente)
                            JSONObject jobj = jsonArray.getJSONObject(jsonArray.length() - 1);

                            // Obtener concentración en ppm
                            int concentracion = jobj.getInt("Concrentracion_ppm");
                            laMedida.setText(String.valueOf(concentracion));

                            // Obtener temperatura
                            int temperatura = jobj.getInt("temperatura");
                            laTemperatura.setText(temperatura + "ºC");

                            // Obtener fecha createdAt
                            String fechaHoraStr = jobj.getString("createdAt");
                            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                            iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date parsedDate = iso8601Format.parse(fechaHoraStr);

                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", Locale.US);
                            String formattedDate = outputFormat.format(parsedDate);
                            laFecha.setText(formattedDate);

                            // Obtener localización (latitud y longitud)
                            double latitud = jobj.getDouble("latitud");
                            double longitud = jobj.getDouble("longitud");
                            String localizacion = String.format(Locale.US, "%.6f, %.6f", latitud, longitud);
                            laLocaliz.setText(localizacion);

                            // Actualizar ProgressBar
                            ProgressBar progress = findViewById(R.id.progressBar);
                            progress.setProgress(concentracion);
                            if (concentracion < 249) {
                                progress.getProgressDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
                            } else if (concentracion < 499) {
                                progress.getProgressDrawable().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
                            } else {
                                progress.getProgressDrawable().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                            }

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Log.e("clienterestandroid", "Error al procesar JSON: " + e.getMessage());
                        }
                    }
                }
        );
    }

    /**
     * @brief Maneja el evento del botón enviar, realizando una petición REST con los datos de medición.
     * @author Alejandro Rosado
     * @param quien: View -> boton_enviar_pulsado() -> void
     * Su flujo: La función se activa al pulsar un botón en la interfaz de usuario.
     * Esta función obtiene valores de concentración y temperatura, junto con las coordenadas geográficas,
     * crea un cuerpo JSON para la solicitud, y realiza una petición REST a un servidor especificado.
     */
    public void boton_enviar_pulsado(View quien) {
        Log.d("clienterestandroid", "boton_enviar_pulsado");
        this.laMedida.setText("pulsado");

        // Hay que crear uno nuevo cada vez
        PeticionarioREST elPeticionario = new PeticionarioREST();

        try {
            // Crear el cuerpo de la petición con los nombres de clave correctos
            String cuerpecito = "{\"Concrentracion_ppm\":" + major + "," +
                    "\"temperatura\":" + minor + "," +
                    "\"latitud\":" + localizacion.getLatitude() + "," +
                    "\"longitud\":" + localizacion.getLongitude() + "}";

            Log.d("Cuerpo del string", cuerpecito);

            // Hacer la petición REST a la URL correspondiente
            elPeticionario.hacerPeticionREST("POST", "http://192.168.32.247:3000/api/v1/mediciones/", cuerpecito,
                    new PeticionarioREST.RespuestaREST() {
                        @Override
                        public void callback(int codigo, String cuerpo) {
                            // Procesar la respuesta aquí si es necesario
                            Log.d("Respuesta REST", "Código: " + codigo + ", Cuerpo: " + cuerpo);
                        }
                    }
            );
        } catch (Exception e) {
            // Bloque catch para manejar excepciones generales
            Log.e("Error", "Se produjo una excepción: " + e.getMessage());
        }
    }


    // ---------------------------------------------------------------------------------------------
    // BLUETOOTH
    // ---------------------------------------------------------------------------------------------

    /**
     * @brief Maneja la respuesta a la solicitud de permisos del usuario.
     * @author Alejandro Rosado
     * @param requestCode: int -> onRequestPermissionsResult() -> void
     * @param permissions: String[] -> las peticiones de permisos que se han solicitado.
     * @param grantResults: int[] -> los resultados de la petición de permisos.
     * Su flujo: Esta función se activa cuando se solicita permisos y se obtiene la respuesta del usuario.
     * Se comprueba si se han concedido los permisos solicitados y se logea el resultado.
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): permisos concedidos  !!!!");
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * @brief Inicia el escaneo de dispositivos Bluetooth LE y establece un callback para manejar los resultados.
     * @author Alejandro Rosado
     * @param Ninguno -> buscarTodosLosDispositivosBTLE() -> void
     * Su flujo: Esta función comienza el proceso de escaneo para detectar dispositivos Bluetooth de baja energía.
     * Se crea un callback que manejará los resultados de la búsqueda, incluyendo la respuesta cuando se detecta un dispositivo.
     */
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanResult() ");

                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");

            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo);

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * @brief Muestra la información del dispositivo Bluetooth LE detectado.
     * @author Alejandro Rosado
     * @param resultado: ScanResult -> mostrarInformacionDispositivoBTLE() -> void
     * Su flujo: Esta función extrae y muestra información detallada sobre el dispositivo detectado,
     * incluyendo nombre, dirección, RSSI y datos del escaneo. También crea un objeto `TramaIBeacon`
     * para analizar los bytes recibidos y mostrar información específica del iBeacon.
     */
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        /*
        ParcelUuid[] puuids = bluetoothDevice.getUuids();
        if ( puuids.length >= 1 ) {
            //Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].getUuid());
           // Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].toString());
        }*/

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");


        major = Utilidades.bytesToInt(tib.getMajor());

        minor = Utilidades.bytesToInt(tib.getMinor());
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * @brief Inicia el escaneo de un dispositivo Bluetooth LE específico por nombre.
     * @author Alejandro Rosado
     * @param dispositivoBuscado: String -> buscarEsteDispositivoBTLE(String dispositivoBuscado) -> void
     * Su flujo: Esta función comienza el escaneo para un dispositivo específico, configurando un callback para manejar
     * los resultados de la búsqueda. El callback mostrará la información del dispositivo encontrado.
     */
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");

                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };

        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(sf);

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.elEscanner.startScan(filters, scanSettings, this.callbackDelEscaneo);

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * @brief Detiene la búsqueda de dispositivos Bluetooth LE.
     * @author Alejandro Rosado
     * @param Ninguno -> detenerBusquedaDispositivosBTLE() -> void
     * Su flujo: Esta función detiene el escaneo de dispositivos y libera el callback utilizado para el escaneo.
     */
    private void detenerBusquedaDispositivosBTLE() {

        if (this.callbackDelEscaneo == null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * @brief Método que se ejecuta cuando se presiona el botón para buscar dispositivos Bluetooth LE.
     * @param v: View -> botón presionado.
     * Su flujo: Llama a la función buscarTodosLosDispositivosBTLE() para iniciar el escaneo.
     */
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado");
        this.buscarTodosLosDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    /**
     * @brief Método que se ejecuta cuando se presiona el botón para buscar un dispositivo Bluetooth LE específico.
     * @param v: View -> botón presionado.
     * Su flujo: Llama a la función buscarEsteDispositivoBTLE() pasando el nombre del dispositivo a buscar.
     */
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton nuestro dispositivo BTLE Pulsado");
        //this.buscarEsteDispositivoBTLE( Utilidades.stringToUUID( "EPSG-GTI-PROY-3A" ) );

        //this.buscarEsteDispositivoBTLE( "EPSG-GTI-PROY-3A" );
        this.buscarEsteDispositivoBTLE("SoyBACON");

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    /**
     * @brief Método que se ejecuta cuando se presiona el botón para detener la búsqueda de dispositivos Bluetooth LE.
     * @param v: View -> botón presionado.
     * Su flujo: Llama a la función detenerBusquedaDispositivosBTLE() para detener el escaneo.
     */
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado");
        this.detenerBusquedaDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    /**
     * @brief Inicializa el Bluetooth y solicita permisos necesarios.
     * @requires Api >= S (Android 12)
     * Su flujo: Obtiene el adaptador Bluetooth, habilita el Bluetooth y configura el escáner LE.
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bta.enable();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if ( this.elEscanner == null ) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");

        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): voy a perdir permisos (si no los tuviera) !!!!");

        if (
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    CODIGO_PETICION_PERMISOS);
        }
        else {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");

        }
    } // ()
}