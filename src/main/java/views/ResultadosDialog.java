package views;

import controllers.MazeController;
import models.AlgorithmResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class ResultadosDialog extends JDialog {

    private final MazeController controller;
    private JTable tabla;
    private DefaultTableModel tableModel;
    private JPanel panelGrafico;
    private List<AlgorithmResult> resultados;

    public ResultadosDialog(JFrame parent, MazeController controller) {
        super(parent, "Resultados de Algoritmos", true);
        this.controller = controller;

        setLayout(new BorderLayout());
        setSize(950, 500);
        setLocationRelativeTo(parent);

        initComponents();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new String[]{"Algoritmo", "Tiempo (ns)", "camino"}, 0);
        tabla = new JTable(tableModel);
        tabla.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(700, 150));
        add(scrollPane, BorderLayout.NORTH);

        panelGrafico = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGrafico(g);
            }
        };
        panelGrafico.setPreferredSize(new Dimension(700, 250));
        add(panelGrafico, BorderLayout.CENTER);

        JButton btnLimpiar = new JButton("Limpiar Resultados");
        btnLimpiar.addActionListener(e -> {
            controller.limpiarResultados();
            actualizar();
        });

        JPanel southPanel = new JPanel();
        southPanel.add(btnLimpiar);
        add(southPanel, BorderLayout.SOUTH);

        actualizar();
    }

    private void actualizar() {
        resultados = controller.listarResultados();
        tableModel.setRowCount(0);

        for (AlgorithmResult r : resultados) {
            tableModel.addRow(new Object[]{
                    r.getAlgorithmName(),
                    r.getExecutionTimeNanos(),
                    r.getPathLength()
            });
        }

        panelGrafico.repaint();
    }

    private void dibujarGrafico(Graphics g) {
        if (resultados == null || resultados.isEmpty()) {
            g.setColor(Color.BLACK);
            g.drawString("No hay datos para mostrar", 20, 20);
            return;
        }

        int width = panelGrafico.getWidth();
        int height = panelGrafico.getHeight();

        int margin = 50;
        int topMargin = 40;
        int bottomMargin = 60;
        int graphHeight = height - topMargin - bottomMargin;

        long maxTime = resultados.stream()
                .mapToLong(AlgorithmResult::getExecutionTimeNanos)
                .max()
                .orElse(1);

        g.setColor(Color.BLACK);
        g.drawString("Gráfico", margin, 20);
        g.drawLine(margin, topMargin + graphHeight, margin, topMargin);
        g.drawLine(margin, topMargin + graphHeight, width - margin, topMargin + graphHeight);

        int pointRadius = 4;
        int prevX = -1;
        int prevY = -1;

        for (int i = 0; i < resultados.size(); i++) {
            AlgorithmResult r = resultados.get(i);
            int x = margin + (i * (width - 2 * margin)) / (resultados.size() - (resultados.size() > 1 ? 1 : 0));
            if (resultados.size() == 1) {
                x = width / 2;
            }
            int y = topMargin + graphHeight - (int) ((r.getExecutionTimeNanos() * 1.0 / maxTime) * graphHeight);

            g.setColor(Color.RED);
            g.fillOval(x - pointRadius, y - pointRadius, 2 * pointRadius, 2 * pointRadius);

            if (prevX != -1) {
                g.setColor(Color.BLUE);
                g.drawLine(prevX, prevY, x, y);
            }

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            Graphics2D g2d = (Graphics2D) g;
            g2d.rotate(Math.toRadians(45), x, topMargin + graphHeight + 10);
            g2d.drawString(r.getAlgorithmName(), x, topMargin + graphHeight + 10);
            g2d.rotate(Math.toRadians(-45), x, topMargin + graphHeight + 10);

            g.drawString(r.getExecutionTimeNanos() + " ns", x + pointRadius + 2, y - pointRadius - 2);

            prevX = x;
            prevY = y;
        }

        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(maxTime), margin - 40, topMargin + 5);
        g.drawString(String.valueOf(maxTime / 2), margin - 40, topMargin + graphHeight / 2 + 5);
        g.drawString("0", margin - 20, topMargin + graphHeight + 5);
    }
}
