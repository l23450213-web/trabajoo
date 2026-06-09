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
                "int|str|print|for|in|[a-zA-Z][a-zA-Z0-9_]*|\\+|\\{|\\}|\\(|\\)|\\[|\\]|,|;"
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

            else if (tok.matches("num"))
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

   if (indice < tokens.size()) {
                preanalisis = tokens.get(indice).tipo;
            }

        } else {

            throw new RuntimeException(
                    "Error sintáctico. Se esperaba "  + esperado + " y llegó "+ preanalisis
            );
        }
    }//gramatica nueva
    public void programa() {
        bloque();
    }

    public void bloque() {
        ints();
        strs();
        sentencia();
    }

    public void ints() {

        if (preanalisis.equals("int")) {

            match("int");
            ciclos_id();
        }
    }

    public void strs() {

        if (preanalisis.equals("str")) {

            match("str");
            ciclos_id();
        }
    }
    public void ciclos_id() {

        match("id");
        nuevo_id();
    }

    public void nuevo_id() {

        if (preanalisis.equals(",")) {

            match(",");
            ciclos_id();
        }
    }

    public void id_num() {

        if (preanalisis.equals("id")) {

            match("id");

        } else if (preanalisis.equals("num")) {

            match("num");

        } else {

            throw new RuntimeException("Se esperaba ID o NUM");
        }
    }
public void lista() {

        match("[");
        elementos();
        match("]");
    }

    public void elementos() {

        id_num();
        ciclo_idnums();
    }

    public void ciclo_idnums() {

        if (preanalisis.equals(",")) {

            match(",");
            id_num();
            ciclo_idnums();
        }
    }

    public void ciclo_sentencias() {

        sentencia();
        nueva_sentencia();
    }

    public void nueva_sentencia() {

        if (preanalisis.equals(";")) {

            match(";");
            ciclo_sentencias();
        }
    }

    public void sentencia() {

        if (preanalisis.equals("{")) {

            match("{");
            ciclo_sentencias();
            match("}");

        } else if (preanalisis.equals("print")) {

            match("print");
            match("(");
            id_num();
            match(")");

        } else if (preanalisis.equals("for")) {

            match("for");
            match("(");
            match("id");
            match("in");
            lista();
            match(")");
            sentencia();

        } else {

            throw new RuntimeException("Sentencia inválida");
        }
    }
    public static void main(String[] args) {

        String codigo =
                "int a,b "
              + "str nombre "
              + "{ "
              + "print(a); "
              + "for(i in [1,2,3]) "
              + "print(i)"
              + "}";

        Ultimo u = new Ultimo();

        u.lexico(codigo);

        System.out.println("TOKENS:");
        for(Lexema l : u.tokens){
            System.out.println(l);
        }

       