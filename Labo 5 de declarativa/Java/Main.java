
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class Main extends JFrame {

    private final JTextField funcion = new JTextField("x^2+x", 25);
    private final JTextField punto = new JTextField("2", 6);
    private final JTextField limiteA = new JTextField("0", 6);
    private final JTextField limiteB = new JTextField("2", 6);
    private final JTextArea resultado = new JTextArea(7, 35);
    private final JLabel estado = new JLabel("Listo");

    public Main() {
        setTitle("Laboratorio 5 - Calculo simbolico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(
                20, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(
                "Derivadas e integrales con SWI-Prolog");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(15));

        panel.add(new JLabel("Funcion f(x):"));
        panel.add(funcion);
        panel.add(Box.createVerticalStrut(12));

        JPanel campos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campos.add(new JLabel("Punto:"));
        campos.add(punto);
        campos.add(new JLabel("Limite inferior:"));
        campos.add(limiteA);
        campos.add(new JLabel("Limite superior:"));
        campos.add(limiteB);
        panel.add(campos);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton derivar = new JButton("Derivar");
        JButton integrar = new JButton("Integrar");
        JButton evaluar = new JButton("Derivada en punto");
        JButton area = new JButton("Integral definida");
        JButton copiar = new JButton("Copiar");

        derivar.addActionListener(e -> ejecutar("d"));
        integrar.addActionListener(e -> ejecutar("s"));
        evaluar.addActionListener(e -> ejecutar("punto"));
        area.addActionListener(e -> ejecutar("area"));

        copiar.addActionListener(e -> {
            String texto = resultado.getText();

            if (!texto.isBlank()) {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(texto), null);
                estado.setText("Resultado copiado");
            }
        });

        botones.add(derivar);
        botones.add(integrar);
        botones.add(evaluar);
        botones.add(area);
        botones.add(copiar);
        panel.add(botones);

        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Resultado:"));

        resultado.setEditable(false);
        resultado.setLineWrap(true);
        resultado.setWrapStyleWord(true);
        resultado.setFont(new Font("Monospaced", Font.PLAIN, 14));

        panel.add(new JScrollPane(resultado));
        panel.add(Box.createVerticalStrut(10));
        panel.add(estado);

        add(panel);
    }

    private String comoTextoProlog(String texto) {
        return "\"" + texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                + "\"";
    }

    private void ejecutar(String operacion) {
        String f = funcion.getText().trim();

        if (f.isEmpty()) {
            estado.setText("Escribe una funcion");
            return;
        }

        try {
            String expresion = comoTextoProlog(f);
            String goal;

            switch (operacion) {
                case "d":
                    goal = "read_term_from_atom(" + expresion
                            + ", F, []), d(x, F, R), "
                            + "write_canonical(R), nl";
                    break;

                case "s":
                    goal = "read_term_from_atom(" + expresion
                            + ", F, []), s(x, F, R), "
                            + "write_canonical(R), nl";
                    break;

                case "punto":
                    goal = "read_term_from_atom(" + expresion
                            + ", F, []), number_string(P, "
                            + comoTextoProlog(punto.getText().trim())
                            + "), derivada_punto(x, F, P, R), "
                            + "write(R), nl";
                    break;

                case "area":
                    goal = "read_term_from_atom(" + expresion
                            + ", F, []), number_string(A, "
                            + comoTextoProlog(limiteA.getText().trim())
                            + "), number_string(B, "
                            + comoTextoProlog(limiteB.getText().trim())
                            + "), area_definida(x, F, A, B, R), "
                            + "write(R), nl";
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Operacion desconocida");
            }

            Path archivo = Path.of("calculo.pl").toAbsolutePath();

            ProcessBuilder pb = new ProcessBuilder(
                    "swipl",
                    "-q",
                    "-s", archivo.toString(),
                    "-g", goal,
                    "-t", "halt"
            );

            pb.redirectErrorStream(true);

            Process proceso = pb.start();

            String salida = new String(
                    proceso.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8).trim();

            int codigo = proceso.waitFor();

            if (codigo == 0 && !salida.isEmpty()) {
                resultado.setText(salida);
                estado.setText("Operacion realizada");
            } else {
                resultado.setText(salida);
                estado.setText(
                        "No se pudo resolver. Revisa la funcion.");
            }

        } catch (IOException ex) {
            estado.setText(
                    "No se encontro SWI-Prolog o calculo.pl");
            resultado.setText(ex.getMessage());

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            estado.setText("Ejecucion interrumpida");

        } catch (RuntimeException ex) {
            estado.setText("Error en los datos ingresados");
            resultado.setText(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new Main().setVisible(true));
    }
}
