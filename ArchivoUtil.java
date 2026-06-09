import java.io.*;

public class ArchivoUtil {

    public static String leerArchivo(File archivo){

        StringBuilder contenido = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new FileReader(archivo))){

            String linea;

            while((linea = br.readLine()) != null){
                contenido.append(linea).append("\n");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return contenido.toString();
    }
}