package org.example.arosjim_upv.prueba_btle_alejandro_rosado;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

// -----------------------------------------------------------------------------------
// @author: Jordi Bataller i Mascarell
// -----------------------------------------------------------------------------------
/**
 * @brief Clase de utilidades para manipular datos en formato de bytes y UUID.
 * @author Jordi Bataller i Mascarell
 * Esta clase proporciona métodos estáticos para convertir entre diferentes representaciones
 * de datos, como cadenas, UUIDs y arreglos de bytes.
 */
public class Utilidades {

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Convierte una cadena de texto a un arreglo de bytes.
     * @author Jordi Bataller i Mascarell
     * @param texto: String -> La cadena que se desea convertir a bytes.
     * Su flujo: Llama a este método para obtener la representación en bytes de una cadena.
     * Este método utiliza la codificación predeterminada del sistema para convertir la cadena.
     * @return byte[] -> Arreglo de bytes que representa el texto.
     */
    public static byte[] stringToBytes(String texto) {
        return texto.getBytes();
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Convierte una cadena UUID en un objeto UUID.
     * @author Jordi Bataller i Mascarell
     * @param uuid: String -> La cadena UUID que se desea convertir.
     * Su flujo: Llama a este método para obtener un objeto UUID a partir de su representación en cadena.
     * @return UUID -> Objeto UUID resultante de la conversión.
     * @throws Error si la longitud de la cadena no es de 16 caracteres.
     * Este método divide la cadena en partes significativas y las convierte en un objeto UUID.
     */
    public static UUID stringToUUID(String uuid) {
        if (uuid.length() != 16) {
            throw new Error("stringUUID: string no tiene 16 caracteres ");
        }
        byte[] comoBytes = uuid.getBytes();

        String masSignificativo = uuid.substring(0, 8);
        String menosSignificativo = uuid.substring(8, 16);
        UUID res = new UUID(Utilidades.bytesToLong(masSignificativo.getBytes()), Utilidades.bytesToLong(menosSignificativo.getBytes()));

        return res;
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Convierte un objeto UUID a su representación en cadena.
     * @author Jordi Bataller i Mascarell
     * @param uuid: UUID -> El objeto UUID que se desea convertir.
     * Su flujo: Llama a este método para obtener la representación en cadena de un objeto UUID.
     * @return String -> Representación en cadena del UUID.
     * Este método convierte el UUID a un arreglo de bytes y luego a una cadena.
     */
    public static String uuidToString(UUID uuid) {
        return bytesToString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Convierte un objeto UUID a su representación en cadena hexadecimal.
     * @author Jordi Bataller i Mascarell
     * @param uuid: UUID -> El objeto UUID que se desea convertir.
     * Su flujo: Llama a este método para obtener la representación en cadena hexadecimal de un objeto UUID.
     * @return String -> Representación en cadena hexadecimal del UUID.
     * Este método convierte el UUID a un arreglo de bytes y luego a una cadena hexadecimal.
     */
    public static String uuidToHexString(UUID uuid) {
        return bytesToHexString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Convierte un arreglo de bytes a una cadena.
     * @author Jordi Bataller i Mascarell
     * @param bytes: byte[] -> Arreglo de bytes que se desea convertir.
     * Su flujo: Llama a este método para obtener la representación en cadena de un arreglo de bytes.
     * @return String -> Representación en cadena de los bytes.
     * Este método itera sobre cada byte y los convierte a caracteres, concatenándolos en una cadena.
     */
    public static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Convierte dos valores long en un arreglo de bytes.
     * @author Jordi Bataller i Mascarell
     * @param masSignificativos: long -> Parte más significativa.
     * @param menosSignificativos: long -> Parte menos significativa.
     * Su flujo: Llama a este método para obtener un arreglo de bytes a partir de dos long.
     * @return byte[] -> Arreglo de bytes que representa los dos long.
     * Este método utiliza ByteBuffer para almacenar ambos long en un arreglo de bytes.
     */
    public static byte[] dosLongToBytes(long masSignificativos, long menosSignificativos) {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Long.BYTES);
        buffer.putLong(masSignificativos);
        buffer.putLong(menosSignificativos);
        return buffer.array();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Convierte un arreglo de bytes a un entero.
     * @author Jordi Bataller i Mascarell
     * @param bytes: byte[] -> Arreglo de bytes que se desea convertir a int.
     * Su flujo: Llama a este método para obtener un entero a partir de un arreglo de bytes.
     * @return int -> Valor entero representado por el arreglo de bytes.
     * Este método utiliza BigInteger para convertir el arreglo de bytes a un entero.
     */
    public static int bytesToInt(byte[] bytes) {
        return new BigInteger(bytes).intValue();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Convierte un arreglo de bytes a un long.
     * @author Jordi Bataller i Mascarell
     * @param bytes: byte[] -> Arreglo de bytes que se desea convertir a long.
     * Su flujo: Llama a este método para obtener un long a partir de un arreglo de bytes.
     * @return long -> Valor long representado por el arreglo de bytes.
     * Este método utiliza BigInteger para convertir el arreglo de bytes a un long.
     */
    public static long bytesToLong(byte[] bytes) {
        return new BigInteger(bytes).longValue();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Convierte un arreglo de bytes a un entero con validación de longitud.
     * @author Jordi Bataller i Mascarell
     * @param bytes: byte[] -> Arreglo de bytes que se desea convertir a int.
     * Su flujo: Llama a este método para obtener un entero a partir de un arreglo de bytes.
     * @return int -> Valor entero representado por el arreglo de bytes.
     * @throws Error si el arreglo es null o tiene más de 4 bytes.
     * Este método valida la longitud del arreglo de bytes y convierte su contenido a int.
     */
    public static int bytesToIntOK(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }

        if (bytes.length > 4) {
            throw new Error("demasiados bytes para pasar a int ");
        }
        int res = 0;

        for (byte b : bytes) {
            res = (res << 8) // * 16
                    + (b & 0xFF); // para quedarse con 1 byte (2 cuartetos) de lo que haya en b
        } // for

        if ((bytes[0] & 0x8) != 0) {
            // si tiene signo negativo (un 1 a la izquierda del primer byte
            res = -(~(byte) res) - 1; // complemento a 2 (~) de res pero como byte, -1
        }

        return res;
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    /**
     * @brief Convierte un arreglo de bytes a una cadena hexadecimal.
     * @author Jordi Bataller i Mascarell
     * @param bytes: byte[] -> Arreglo de bytes que se desea convertir a cadena hexadecimal.
     * Su flujo: Llama a este método para obtener la representación hexadecimal de un arreglo de bytes.
     * @return String -> Representación en cadena hexadecimal de los bytes.
     * Este método itera sobre cada byte y los convierte a su representación hexadecimal, separándolos con dos puntos.
     */
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
            sb.append(':');
        }
        return sb.toString();
    } // ()
} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
