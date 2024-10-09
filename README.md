# Android Beacon a Backend

Documentación del Proyecto:

Introducción:

Este proyecto es una aplicación Android que interactúa con sensores a través de Bluetooth Low Energy (BLE) y una API REST para recuperar y enviar datos sobre mediciones ambientales. La arquitectura se basa en varias clases, cada una de las cuales desempeña un papel específico en la funcionalidad de la aplicación.


Clases Principales:

1. MainActivity
La clase MainActivity es la actividad principal de la aplicación. Aquí se gestionan las interacciones del usuario y se actualizan las vistas de la interfaz de usuario.

Componentes Clave:

TextView: Para mostrar datos como la medición, la fecha, la ubicación y la temperatura.

Button: Para enviar y recibir datos de los sensores.

FusedLocationProviderClient: Para obtener la ubicación del dispositivo.

BluetoothLeScanner: Para escanear dispositivos Bluetooth.


Ejemplo de uso:

Inicialización y Configuración:


@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // Inicialización de componentes de UI
    laMedida = findViewById(R.id.elTexto);
    laFecha = findViewById(R.id.laFecha);
    elBotonEnviar = findViewById(R.id.botonEnviar);
    laLocaliz = findViewById(R.id.laLocaliz);
    laTemperatura = findViewById(R.id.laTemperatura);
    // Obtener permisos y configuración inicial
    inicializarBlueTooth();
}

Botón Recibir: 

Al pulsar el botón, se envía una solicitud GET para recuperar las mediciones del sensor.

public void boton_recibir_pulsado(View quien) {
    PeticionarioREST elPeticionario = new PeticionarioREST();
    elPeticionario.hacerPeticionREST("GET", "http://192.168.32.247:3000/api/v1/mediciones/", null,
        new PeticionarioREST.RespuestaREST() {
            @Override
            public void callback(int codigo, String cuerpo) {
                // Procesar la respuesta
            }
        }
    );
}


2. PeticionarioREST:

La clase PeticionarioREST se encarga de realizar las solicitudes HTTP a la API REST, permitiendo tanto peticiones GET como POST.

Ejemplo de uso:

Solicitud GET: Recupera datos del servidor.


elPeticionario.hacerPeticionREST("GET", "http://192.168.32.247:3000/api/v1/mediciones/", null,
    new PeticionarioREST.RespuestaREST() {
        @Override
        public void callback(int codigo, String cuerpo) {
            // Manejar la respuesta aquí
        }
    }
);

Solicitud POST: 

Envía datos al servidor. En este ejemplo, se envían mediciones.

JSONObject jsonObject = new JSONObject();
jsonObject.put("Concentracion_ppm", concentracion);
jsonObject.put("temperatura", temperatura);
elPeticionario.hacerPeticionREST("POST", "http://192.168.32.247:3000/api/v1/mediciones/", jsonObject,
    new PeticionarioREST.RespuestaREST() {
        @Override
        public void callback(int codigo, String cuerpo) {
            // Manejar la respuesta aquí
        }
    }
);


3. TramalBeacon:
   
La clase TramalBeacon se encarga de la lógica relacionada con los beacons BLE. Aunque no se ha proporcionado el código de esta clase, su función probablemente incluye la configuración y el manejo de la interacción con dispositivos Bluetooth.



4. Utilidades:

La clase Utilidades proporciona métodos estáticos para realizar conversiones entre diferentes formatos de datos, incluyendo bytes a strings y viceversa, así como manipulación de UUIDs.

Ejemplo de uso:

Convertir un UUID a un String:

UUID uuid = UUID.randomUUID();
String uuidString = Utilidades.uuidToString(uuid);

Convertir un String a un arreglo de bytes:

String texto = "Hola Mundo";
byte[] bytes = Utilidades.stringToBytes(texto);


Resumen:

Esta aplicación combina la comunicación Bluetooth y las solicitudes REST para interactuar con sensores ambientales. A través de la clase MainActivity, se gestiona la interfaz y las interacciones del usuario, mientras que PeticionarioREST maneja la comunicación con el servidor. La clase Utilidades proporciona funciones de apoyo que facilitan la manipulación de datos.
