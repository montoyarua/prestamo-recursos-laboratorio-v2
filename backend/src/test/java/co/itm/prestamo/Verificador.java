package co.itm.prestamo;

/** Mini marco de pruebas sin librerías: cada comprobación imprime [OK] o [FALLA] y se cuenta. */
public final class Verificador {
    private static int ok = 0, fallos = 0;
    private Verificador() { }
    public static void verificar(String nombre, boolean cond) {
        System.out.println((cond ? "[OK]    " : "[FALLA] ") + nombre); if (cond) ok++; else fallos++;
    }
    public static void titulo(String t) { System.out.println("\n== " + t); }
    /** ¿La acción lanza la excepción esperada? (si lanza otra o ninguna, es false) */
    public static boolean lanza(Class<? extends Exception> tipo, Runnable r) {
        try { r.run(); return false; } catch (Exception e) { return tipo.isInstance(e); }
    }
    /** Mensaje de la excepción lanzada, o null si no lanza. */
    public static String mensaje(Runnable r) { try { r.run(); return null; } catch (Exception e) { return e.getMessage(); } }
    public static int correctas() { return ok; } public static int fallidas() { return fallos; }
    public static void resumen(String nombre) { System.out.println("\n" + nombre + ": " + ok + " comprobaciones correctas, " + fallos + " fallidas"); }
}
