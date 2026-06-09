import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Maatch extends JFrame {

    private JTextArea textAreaEntrada;
    private JTextArea textAreaResultados;
    private JLabel lblArchivo;
    
    private boolean exitoLexico = false; 
    private JMenuItem itemSintactico; 

    public Maatch() {
        setTitle("Compilador Simulador");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemAbrir = new JMenuItem("Abrir");
        JMenuItem itemSalir = new JMenuItem("Salir");
        
        itemAbrir.addActionListener(e -> seleccionarArchivo());
        itemSalir.addActionListener(e -> System.exit(0)); 
        
        menuArchivo.add(itemAbrir);
        menuArchivo.add(new JSeparator()); 
        menuArchivo.add(itemSalir);

        // Menú Compilar
        JMenu menuCompilar = new JMenu("Compilar");
        JMenuItem itemLexico = new JMenuItem("Análisis Léxico");
        itemSintactico = new JMenuItem("Análisis Sintáctico");
        itemSintactico.setEnabled(false); 

        itemLexico.addActionListener(e -> ejecutarAnalisisLexico());
        itemSintactico.addActionListener(e -> ejecutarAnalisisSintactico());
        
        menuCompilar.add(itemLexico);
        menuCompilar.add(itemSintactico);

        menuBar.add(menuArchivo);
        menuBar.add(menuCompilar);
        setJMenuBar(menuBar); 

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        JLabel lblContenido = new JLabel("Contenido:"); 
        lblContenido.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
        textAreaEntrada = new JTextArea();
        panelIzquierdo.add(lblContenido, BorderLayout.NORTH);
        panelIzquierdo.add(new JScrollPane(textAreaEntrada), BorderLayout.CENTER);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        JLabel lblTokens = new JLabel("Tokens / Resultados:"); 
        lblTokens.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
        textAreaResultados = new JTextArea();
        textAreaResultados.setEditable(false);
        panelDerecho.add(lblTokens, BorderLayout.NORTH);
        panelDerecho.add(new JScrollPane(textAreaResultados), BorderLayout.CENTER);

        // Separador Horizontal
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                panelIzquierdo,
                panelDerecho);
        split.setDividerLocation(450); 
        split.setDividerSize(5); 

        // Barra de estado abajo
        lblArchivo = new JLabel("Listo. Abra un archivo o escriba código.");
        lblArchivo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        panelPrincipal.add(split, BorderLayout.CENTER);
        panelPrincipal.add(lblArchivo, BorderLayout.SOUTH); 

        add(panelPrincipal);
    }

    private void seleccionarArchivo() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter(
            "Archivos de texto", "txt", "java", "c", "cpp", "py", "html", "xml", "json");
        chooser.setFileFilter(filtro);

        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            File archivo = chooser.getSelectedFile();
            lblArchivo.setText("Archivo abierto: " + archivo.getAbsolutePath());
            
            String contenido = ArchivoUtil.leerArchivo(archivo);
            textAreaEntrada.setText(contenido);
            
            textAreaResultados.setText(""); 
            exitoLexico = false;
            itemSintactico.setEnabled(false);
        }
    }

    // análisis léxico
    private void ejecutarAnalisisLexico() {
        textAreaResultados.setText(""); 
        String contenido = textAreaEntrada.getText();
        if (contenido.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay código para analizar.");
            return;
        }

        textAreaResultados.append(" Iniciando Análisis Léxico \n\n");
        String resultado = Procesador.procesarLexico(contenido);
        textAreaResultados.append(resultado);

        // Verificamos si hubo errores léxicos. Buscamos la palabra "ERROR".
        exitoLexico = !resultado.contains("ERROR LÉXICO");

        if (exitoLexico) {
            textAreaResultados.append("\n Análisis Léxico Exitoso! \n Menú 'Análisis Sintáctico' habilitado.\n");
            itemSintactico.setEnabled(true); 
        } else {
            textAreaResultados.append("\n  Análisis Léxico Fallido. Por favor corrija los errores.\n");
            itemSintactico.setEnabled(false); 
        }
    }
             //Analisis sintatico
   private void ejecutarAnalisisSintactico() {

    if (!exitoLexico) {
        JOptionPane.showMessageDialog(this,
                "Debe pasar el Análisis Léxico primero.");
        return;
    }

    textAreaResultados.append(
            "\n\n=== INICIANDO ANÁLISIS SINTÁCTICO ===\n");

    boolean estructuraCorrecta = true;

    int begin = 0;
    int end = 0;
    int parentesis = 0;

    for (int i = 0; i < Procesador.listaTokens.size(); i++) {

        String token =
                Procesador.listaTokens.get(i).toString();

        // BEGIN - END
        if (token.contains("begin"))
            begin++;

        if (token.contains("end"))
            end++;

        // Paréntesis
        if (token.contains("(\tsimbolo"))
            parentesis++;

        if (token.contains(")\tsimbolo"))
            parentesis--;

        // IF
        if (token.contains("if\tpalabra Reservada")) {

            boolean tieneThen = false;

            for (int j = i + 1;
                 j < Procesador.listaTokens.size();
                 j++) {

                String sig =
                        Procesador.listaTokens.get(j).toString();

                if (sig.contains("then")) {
                    tieneThen = true;
                    break;
                }
            }

            if (!tieneThen) {

                textAreaResultados.append(
                        "ERROR SINTÁCTICO: Falta THEN en IF.\n");

                estructuraCorrecta = false;
            }
        }

        // WHILE
        if (token.contains("while\tpalabra Reservada")) {

            boolean tieneDo = false;

            for (int j = i + 1;
                 j < Procesador.listaTokens.size();
                 j++) {

                String sig =
                        Procesador.listaTokens.get(j).toString();

                if (sig.contains("do")) {
                    tieneDo = true;
                    break;
                }
            }

            if (!tieneDo) {

                textAreaResultados.append(
                        "ERROR SINTÁCTICO: Falta DO en WHILE.\n");

                estructuraCorrecta = false;
            }
        }

        // FOR
        if (token.contains("for\tpalabra Reservada")) {

            boolean tieneToDown = false;
            boolean tieneDo = false;

            for (int j = i + 1;
                 j < Procesador.listaTokens.size();
                 j++) {

                String sig =
                        Procesador.listaTokens.get(j).toString();

                if (sig.contains("to") ||
                    sig.contains("down")) {

                    tieneToDown = true;
                }

                if (sig.contains("do")) {

                    tieneDo = true;
                    break;
                }
            }

            if (!tieneToDown) {

                textAreaResultados.append(
                        "ERROR SINTÁCTICO: FOR requiere TO o DOWN.\n");

                estructuraCorrecta = false;
            }

            if (!tieneDo) {

                textAreaResultados.append(
                        "ERROR SINTÁCTICO: FOR requiere DO.\n");

                estructuraCorrecta = false;
            }
        }
    }

    // BEGIN-END
    if (begin != end) {

        textAreaResultados.append(
                "ERROR SINTÁCTICO: BEGIN y END desbalanceados.\n");

        estructuraCorrecta = false;
    }

    // PARÉNTESIS
    if (parentesis != 0) {

        textAreaResultados.append(
                "ERROR SINTÁCTICO: Paréntesis desbalanceados.\n");

        estructuraCorrecta = false;
    }

    // PUNTO FINAL DEL PROGRAMA
    if (!Procesador.listaTokens.isEmpty()) {

        String ultimo =
                Procesador.listaTokens.get(
                        Procesador.listaTokens.size() - 1)
                        .toString();

        if (!ultimo.contains(".\tsimbolo")) {

            textAreaResultados.append(
                    "ERROR SINTÁCTICO: El programa debe terminar con punto '.'.\n");

            estructuraCorrecta = false;
        }
    }

    if (estructuraCorrecta) {

        textAreaResultados.append(
                "\n¡Estructura Gramatical Correcta!\n");

        textAreaResultados.append(
                "El Análisis Sintáctico ha finalizado con éxito.\n");

    } else {

        textAreaResultados.append(
                "\nAnálisis Sintáctico Fallido.\n");
    }

    textAreaResultados.append(
            "=====================================\n");
}

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> new Maatch().setVisible(true));
    }
}