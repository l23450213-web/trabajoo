import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Lexema {
    String dato;
    String tipo;
    int token;

    public Lexema(String dato, int token, String tipo) {
        this.dato = dato;
        this.token = token;
        this.tipo = tipo;
        
        // Lógica simplificada para palabras reservadas-modificacion que llevo
        if (tipo.equals("id")) {
            if (dato.matches("int|str|ID|print|for|in|read|call|if|then|while|do|for|to|down"))
{
    this.token = 500;
    this.tipo = "palabra Reservada";
}
        }
    }

    @Override
    public String toString() {
        return "[" + dato + "\t" + tipo + "\t" + token + "]";
    }
}
public class Ultimoo {
     public static ArrayList<Lexema> listaTokens = new ArrayList<>();

    public static String procesarLexico(String contenido) {
        StringBuilder resultados = new StringBuilder();
        listaTokens.clear();

        // Grupos:ID,NUM,SIMBOLOS,ESPACIOS,ERROR
        String regex = "([a-zA-Z][a-zA-Z0-9_]*)|" + 
                       "(0|[1-9][0-9]*)|" + 
                       "(==|!=|<=|>=|<|>|=|\\+|-|\\*|/|;|\\(|\\)|\\.|,)|" + 
                       "(\\s+)|" + 
                       "(.)";

        Pattern patron = Pattern.compile(regex);
        String[] lineas = contenido.split("\n");

        for (int i = 0; i < lineas.length; i++) {
            String lineaTexto = lineas[i];
            if (lineaTexto.trim().isEmpty()) continue;

            resultados.append("Línea ").append(i + 1).append(": ").append(lineaTexto).append("\n");
            Matcher matcher = patron.matcher(lineaTexto);

            while (matcher.find()) {
                String dato = matcher.group();

                if (matcher.group(1) != null) { // ID o Reservada
                    Lexema l = new Lexema(dato, 2, "id");
                    listaTokens.add(l);
                    resultados.append("   ").append(l.toString()).append("\n");
                } 
                else if (matcher.group(2) != null) { // Números
                    Lexema l = new Lexema(dato, 1, "num");
                    listaTokens.add(l);
                    resultados.append("   ").append(l.toString()).append("\n");
                } 
                else if (matcher.group(3) != null) { // Símbolos
                    int codigo = obtenerCodigo(dato);
                    Lexema l = new Lexema(dato, codigo, "simbolo");
                    listaTokens.add(l);
                    resultados.append("   ").append(l.toString()).append("\n");
                } 
                else if (matcher.group(5) != null && !dato.trim().isEmpty()) { // Errores
                    Lexema l = new Lexema(dato, 0, "ERROR LÉXICO");
                    listaTokens.add(l);
                    resultados.append("   ").append(l.toString()).append("\n");
                

                }
            }
            resultados.append("\n");
        }
        return resultados.length() == 0 ? "Sin contenido." : resultados.toString();
    }

    //numeros asignados
    private static int obtenerCodigo(String dato) {
        switch (dato) {
            case "==": return 11; case "!=": return 12;
            case "<":  return 13; case "<=": return 14;
            case ">":  return 15; case ">=": return 16;
            case "=":  return 11; case ";":  return 21;
            case "(":  return 22; case ")":  return 23;
            case ",":  return 24; case ".":  return 25;
            case "+":  return 31; case "-":  return 32;
            case "*":  return 33; case "/":  return 34;
            default:   return 0;
        }
    }
} 

