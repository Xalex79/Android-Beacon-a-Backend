package org.example.arosjim_upv.prueba_btle_alejandro_rosado;

import java.util.Arrays;

// -----------------------------------------------------------------------------------
// @author: Jordi Bataller i Mascarell
// -----------------------------------------------------------------------------------
/**
 * @brief Clase que representa una trama IBeacon.
 * @author Jordi Bataller i Mascarell
 * Esta clase se utiliza para analizar y acceder a los componentes de una trama IBeacon.
 */
public class TramaIBeacon {
    private byte[] prefijo = null; // 9 bytes
    private byte[] uuid = null; // 16 bytes
    private byte[] major = null; // 2 bytes
    private byte[] minor = null; // 2 bytes
    private byte txPower = 0; // 1 byte

    private byte[] losBytes;

    private byte[] advFlags = null; // 3 bytes
    private byte[] advHeader = null; // 2 bytes
    private byte[] companyID = new byte[2]; // 2 bytes
    private byte iBeaconType = 0; // 1 byte
    private byte iBeaconLength = 0; // 1 byte

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene el prefijo de la trama IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte[] -> Prefijo de la trama IBeacon.
     * Su flujo: Llama a este método para obtener el prefijo de la trama.
     * Devuelve el campo prefijo almacenado en la clase.
     */
    public byte[] getPrefijo() {
        return prefijo;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene el UUID de la trama IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte[] -> UUID de la trama IBeacon.
     * Su flujo: Llama a este método para obtener el UUID de la trama.
     * Devuelve el campo uuid almacenado en la clase.
     */
    public byte[] getUUID() {
        return uuid;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene el valor del campo Major de la trama IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte[] -> Valor Major de la trama IBeacon.
     * Su flujo: Llama a este método para obtener el valor Major.
     * Devuelve el campo major almacenado en la clase.
     */
    public byte[] getMajor() {
        return major;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene el valor del campo Minor de la trama IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte[] -> Valor Minor de la trama IBeacon.
     * Su flujo: Llama a este método para obtener el valor Minor.
     * Devuelve el campo minor almacenado en la clase.
     */
    public byte[] getMinor() {
        return minor;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene el valor de Tx Power de la trama IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte -> Valor de Tx Power de la trama IBeacon.
     * Su flujo: Llama a este método para obtener el valor de Tx Power.
     * Devuelve el campo txPower almacenado en la clase.
     */
    public byte getTxPower() {
        return txPower;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene los bytes de la trama IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte[] -> Bytes de la trama IBeacon.
     * Su flujo: Llama a este método para obtener los bytes de la trama.
     * Devuelve el campo losBytes almacenado en la clase.
     */
    public byte[] getLosBytes() {
        return losBytes;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene las banderas de publicidad (Adv Flags) de la trama IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte[] -> Banderas de publicidad de la trama IBeacon.
     * Su flujo: Llama a este método para obtener las banderas de publicidad.
     * Devuelve el campo advFlags almacenado en la clase.
     */
    public byte[] getAdvFlags() {
        return advFlags;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene el encabezado de publicidad (Adv Header) de la trama IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte[] -> Encabezado de publicidad de la trama IBeacon.
     * Su flujo: Llama a este método para obtener el encabezado de publicidad.
     * Devuelve el campo advHeader almacenado en la clase.
     */
    public byte[] getAdvHeader() {
        return advHeader;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene el ID de la compañía de la trama IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte[] -> ID de la compañía de la trama IBeacon.
     * Su flujo: Llama a este método para obtener el ID de la compañía.
     * Devuelve el campo companyID almacenado en la clase.
     */
    public byte[] getCompanyID() {
        return companyID;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene el tipo de IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte -> Tipo de IBeacon.
     * Su flujo: Llama a este método para obtener el tipo de IBeacon.
     * Devuelve el campo iBeaconType almacenado en la clase.
     */
    public byte getiBeaconType() {
        return iBeaconType;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Obtiene la longitud de IBeacon.
     * @author Jordi Bataller i Mascarell
     * @return byte -> Longitud de IBeacon.
     * Su flujo: Llama a este método para obtener la longitud de IBeacon.
     * Devuelve el campo iBeaconLength almacenado en la clase.
     */
    public byte getiBeaconLength() {
        return iBeaconLength;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Constructor de la clase TramaIBeacon que inicializa los campos a partir de un arreglo de bytes.
     * @author Jordi Bataller i Mascarell
     * @param bytes: byte[] -> Arreglo de bytes que contiene los datos de la trama IBeacon.
     * Su flujo: Inicializa los campos de la clase con los datos extraídos del arreglo de bytes.
     * Este constructor procesa el arreglo de bytes recibido y asigna los valores correspondientes a cada campo de la trama IBeacon.
     */
    public TramaIBeacon(byte[] bytes) {
        this.losBytes = bytes;

        prefijo = Arrays.copyOfRange(losBytes, 0, 8 + 1); // 9 bytes
        uuid = Arrays.copyOfRange(losBytes, 9, 24 + 1); // 16 bytes
        major = Arrays.copyOfRange(losBytes, 25, 26 + 1); // 2 bytes
        minor = Arrays.copyOfRange(losBytes, 27, 28 + 1); // 2 bytes
        txPower = losBytes[29]; // 1 byte

        advFlags = Arrays.copyOfRange(prefijo, 0, 2 + 1); // 3 bytes
        advHeader = Arrays.copyOfRange(prefijo, 3, 4 + 1); // 2 bytes
        companyID = Arrays.copyOfRange(prefijo, 5, 6 + 1); // 2 bytes
        iBeaconType = prefijo[7]; // 1 byte
        iBeaconLength = prefijo[8]; // 1 byte
    } // ()
} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
