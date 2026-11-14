package co.itm.prestamo.contrato;

import java.util.*;

/** Utilidad mínima de JSON (sin librerías): escribe objetos simples y lee objetos planos clave/valor. */
public final class Json {
    private Json() { }
    public static String escribir(Object o) {
        StringBuilder sb = new StringBuilder(); escribir(o, sb); return sb.toString();
    }
    private static void escribir(Object o, StringBuilder sb) {
        if (o == null) sb.append("null");
        else if (o instanceof Number || o instanceof Boolean) sb.append(o);
        else if (o instanceof Map<?, ?> m) {
            sb.append('{'); boolean primero = true;
            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (!primero) sb.append(','); primero = false;
                cadena(String.valueOf(e.getKey()), sb); sb.append(':'); escribir(e.getValue(), sb);
            }
            sb.append('}');
        } else if (o instanceof Collection<?> c) {
            sb.append('['); boolean primero = true;
            for (Object x : c) { if (!primero) sb.append(','); primero = false; escribir(x, sb); }
            sb.append(']');
        } else cadena(o.toString(), sb);
    }
    private static void cadena(String s, StringBuilder sb) {
        sb.append('"');
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\""); case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n"); case '\r' -> sb.append("\\r"); case '\t' -> sb.append("\\t");
                default -> { if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c); }
            }
        }
        sb.append('"');
    }
    /** Lee un objeto JSON plano: {"a":"x","b":3}. Todos los valores se devuelven como texto. */
    public static Map<String, String> leerPlano(String json) {
        Map<String, String> r = new LinkedHashMap<>();
        if (json == null) return r;
        int[] i = {0}; String s = json.trim();
        if (s.isEmpty() || s.charAt(0) != '{') return r;
        i[0] = 1;
        while (i[0] < s.length()) {
            saltar(s, i);
            if (s.charAt(i[0]) == '}') break;
            String clave = leerCadena(s, i); saltar(s, i); i[0]++; saltar(s, i);   // ':'
            String valor;
            if (s.charAt(i[0]) == '"') valor = leerCadena(s, i);
            else { int ini = i[0]; while (i[0] < s.length() && ",}".indexOf(s.charAt(i[0])) < 0) i[0]++; valor = s.substring(ini, i[0]).trim(); if (valor.equals("null")) valor = null; }
            r.put(clave, valor); saltar(s, i);
            if (s.charAt(i[0]) == ',') i[0]++;
        }
        return r;
    }
    private static void saltar(String s, int[] i) { while (i[0] < s.length() && Character.isWhitespace(s.charAt(i[0]))) i[0]++; }
    private static String leerCadena(String s, int[] i) {
        StringBuilder sb = new StringBuilder(); i[0]++;
        while (s.charAt(i[0]) != '"') {
            char c = s.charAt(i[0]++);
            if (c == '\\') {
                char n = s.charAt(i[0]++);
                switch (n) { case 'n' -> sb.append('\n'); case 't' -> sb.append('\t'); case 'r' -> sb.append('\r');
                    case 'u' -> { sb.append((char) Integer.parseInt(s.substring(i[0], i[0] + 4), 16)); i[0] += 4; }
                    default -> sb.append(n); }
            } else sb.append(c);
        }
        i[0]++; return sb.toString();
    }
}
