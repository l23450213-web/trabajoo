import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ultimo {

    static class Lexema {
        String dato;
        String tipo;

        public Lexema(String dato, String tipo) {
            this.dato = dato;
            this.tipo = tipo;
        }

        @Override
        public String toString() {
            return dato + " -> " + tipo;
        }
    }

    ArrayList<Lexema> tokens = new ArrayList<>();
    int indice = 0;
    String preanalisis;

    // lexico

    public void lexico(String codigo) {

        Pattern p = Pattern.compile(
                "int|str|print|for|in|[a-zA-Z][a-zA-Z0-9_]*|\\d+|\\{|\\}|\\(|\\)|\\[|\\]|,|;"
        );

        Matcher m = p.matcher(codigo);

        while (m.find()) {

            String tok = m.group();

            if (tok.equals("int"))
                tokens.add(new Lexema(tok, "int"));

            else if (tok.equals("str"))
                tokens.add(new Lexema(tok, "str"));

            else if (tok.equals("print"))
                tokens.add(new Lexema(tok, "print"));

            else if (tok.equals("for"))
                tokens.add(new Lexema(tok, "for"));

            else if (tok.equals("in"))
                tokens.add(new Lexema(tok, "in"));

            else if (tok.matches("\\d+"))
                tokens.add(new Lexema(tok, "num"));

            else if (tok.matches("[a-zA-Z][a-zA-Z0-9_]*"))
                tokens.add(new Lexema(tok, "id"));

            else
                tokens.add(new Lexema(tok, tok));
        }
    }
    public void match(String esperado) {

        if (preanalisis.equals(esperado)) {

            indice++;

   