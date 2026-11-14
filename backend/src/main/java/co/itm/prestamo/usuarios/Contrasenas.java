package co.itm.prestamo.usuarios;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** RNF-06: las contraseñas se guardan con PBKDF2-HMAC-SHA256 y una sal aleatoria por cuenta (solo JDK). */
public final class Contrasenas {
    private static final int ITERACIONES = 65_536, BITS = 256;
    private static final SecureRandom AZAR = new SecureRandom();
    private Contrasenas() { }

    public static String cifrar(String claro) {
        byte[] sal = new byte[16]; AZAR.nextBytes(sal);
        return ITERACIONES + ":" + Base64.getEncoder().encodeToString(sal) + ":" + Base64.getEncoder().encodeToString(resumen(claro, sal, ITERACIONES));
    }
    public static boolean coincide(String claro, String almacenada) {
        String[] p = almacenada.split(":");
        int it = Integer.parseInt(p[0]); byte[] sal = Base64.getDecoder().decode(p[1]); byte[] esperado = Base64.getDecoder().decode(p[2]);
        return MessageDigest.isEqual(esperado, resumen(claro, sal, it));
    }
    private static byte[] resumen(String claro, byte[] sal, int it) {
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(claro.toCharArray(), sal, it, BITS)).getEncoded(); }
        catch (Exception e) { throw new IllegalStateException(e); }
    }
}
