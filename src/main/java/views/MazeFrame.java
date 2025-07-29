package views;

import controllers.MazeController;
import controllers.MazeController.Mode;
import models.Cell;
import models.SolveResults;
import models.CellState;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;

public class MazeFrame extends JFrame {

    private MazeController controller;
    private MazePanel mazePanel;

    private JComboBox<String> comboAlgoritmos;
    private JButton btnEjecutar, btnPasoAPaso, btnLimpiar, btnResultados;
    private JButton btnInicio, btnFin, btnPared;

    private List<Cell> pasoVisitados;
    private List<Cell> pasoCamino;
    private int pasoIndex = 0;


    public MazeFrame(MazeController controller, MazePanel mazePanel) {
        this.controller = controller;
        this.mazePanel = mazePanel;
        initComponents(controller.obtenerNombresAlgoritmos());
    }

    private void initComponents(Set<String> algoritmosDisponibles) {
        setTitle("Laberinto - Resolución de Algoritmos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(mazePanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout());

        btnInicio = new JButton("Inicio");
        btnFin = new JButton("Final");
        btnPared = new JButton("Muro-Vacio");

        btnInicio.addActionListener(e -> controller.setMode(Mode.START));
        btnFin.addActionListener(e -> controller.setMode(Mode.END));
        btnPared.addActionListener(e -> controller.setMode(Mode.WALL));

        topPanel.add(btnInicio);
        topPanel.add(btnFin);
        topPanel.add(btnPared);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();

        comboAlgoritmos = new JComboBox<>();
        for (String alg : algoritmosDisponibles) {
            comboAlgoritmos.addItem(alg);
        }

        btnEjecutar = new JButton("Resolver");
        btnPasoAPaso = new JButton("Paso a paso");
        btnLimpiar = new JButton("Limpiar");
        btnResultados = new JButton("Ver Resultados");

        btnEjecutar.addActionListener(this::ejecutarAlgoritmo);
        btnPasoAPaso.addActionListener(this::mostrarPasoAPaso);
        btnLimpiar.addActionListener(e -> {
            mazePanel.resetear();
            limpiarPasoAPaso();
        });
        btnResultados.addActionListener(e -> {
            ResultadosDialog dialog = new ResultadosDialog(this, controller);
            dialog.setVisible(true);
        });

        bottomPanel.add(new JLabel("Algoritmo:"));
        bottomPanel.add(comboAlgoritmos);
        bottomPanel.add(btnEjecutar);
        bottomPanel.add(btnPasoAPaso);
        bottomPanel.add(btnLimpiar);
        bottomPanel.add(btnResultados);

        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void ejecutarAlgoritmo(ActionEvent e) {
        SolveResults resultado = resolver();
        if (resultado != null) {
            mazePanel.mostrarResultado(resultado);
        }
    }

    private SolveResults resolver() {
        String algoritmo = (String) comboAlgoritmos.getSelectedItem();
        boolean[][] matriz = controller.getMatriz();
        Cell inicio = controller.getStartCell();
        Cell fin = controller.getEndCell();

        if (inicio == null || fin == null) {
            JOptionPane.showMessageDialog(this, "Establece punto de inicio y fin.");
            return null;
        }

        return controller.ejecutar(algoritmo, matriz, inicio, fin);
    }

    private void mostrarPasoAPaso(ActionEvent e) {
        if (pasoCamino == null) {
            SolveResults resultado = resolver();
            if (resultado != null) {
                pasoVisitados = resultado.getVisited().stream().toList();
                pasoCamino = resultado.getPath();
                pasoIndex = 0;
            }
        } else if (pasoIndex < pasoVisitados.size()) {
            Cell c = pasoVisitados.get(pasoIndex++);
            mazePanel.getCells()[c.getRow()][c.getCol()].setState(CellState.VISITA);
            mazePanel.actualizarVisual();
        } else if (pasoIndex - pasoVisitados.size() < pasoCamino.size()) {
            int idx = pasoIndex - pasoVisitados.size();
            Cell c = pasoCamino.get(idx);
            pasoIndex++;
            mazePanel.getCells()[c.getRow()][c.getCol()].setState(CellState.CAMINO);
            mazePanel.actualizarVisual();

        } else {
            JOptionPane.showMessageDialog(this, "Paso a paso finalizado.");
            limpiarPasoAPaso();
        }
    }

    private void limpiarPasoAPaso() {
        pasoVisitados = null;
        pasoCamino = null;
        pasoIndex = 0;
    }
}