package views;

import controllers.MazeController;
import models.Cell;
import models.CellState;
import models.SolveResults;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class MazePanel extends JPanel {

    private static final int SIZE = 40;
    private int filas = 10;
    private int columnas = 10;

    private Cell[][] cells;
    private JButton[][] buttons;
    private MazeController controller;

    private static final Color PURPLE_TAUPE = new Color(80, 64, 77);
    private static final Color EGGPLANT_PURPLE = new Color(48, 25, 52);

    public MazePanel() {
        solicitarTamanioLaberinto();
        inicializarCeldas();
        setLayout(new GridLayout(filas, columnas));
        setPreferredSize(new Dimension(columnas * SIZE, filas * SIZE));
        inicializarBotones();
    }

    private void solicitarTamanioLaberinto() {
        try {
            String inputFilas = JOptionPane.showInputDialog(null, "Ingrese número de filas:", "Tamaño del laberinto", JOptionPane.QUESTION_MESSAGE);
            String inputCols = JOptionPane.showInputDialog(null, "Ingrese número de columnas:", "Tamaño del laberinto", JOptionPane.QUESTION_MESSAGE);
            filas = Math.max(5, Integer.parseInt(inputFilas.trim()));
            columnas = Math.max(5, Integer.parseInt(inputCols.trim()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Entrada inválida. Usando tamaño por defecto 10x10.");
            filas = columnas = 10;
        }
    }

    private void inicializarCeldas() {
        cells = new Cell[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                cells[i][j] = new Cell(i, j);
                cells[i][j].setState(CellState.VACIO);
            }
        }
    }

    private void inicializarBotones() {
        buttons = new JButton[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                JButton btn = new JButton();
                btn.setBackground(Color.WHITE);
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                final int fila = i;
                final int col = j;

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (controller != null) {
                            controller.onCellClicked(fila, col);
                            actualizarVisual();
                        }
                    }
                });

                buttons[i][j] = btn;
                add(btn);
            }
        }
    }

    public void setController(MazeController controller) {
        this.controller = controller;
        controller.setMatriz(cells);
    }

    public Cell[][] getCells() {
        return cells;
    }

    public JButton getButton(int fila, int col) {
        return buttons[fila][col];
    }

    public void limpiarCeldasVisitadas() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Cell cell = cells[i][j];
                if (cell.getState() == CellState.VISITA || cell.getState() == CellState.CAMINO) {
                    cell.setState(CellState.VACIO);
                    buttons[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }

    public void mostrarResultado(SolveResults resultado) {
        limpiarCeldasVisitadas();

        new Thread(() -> {
            for (Cell cell : resultado.getVisited()) {
                if (cell == null || cell.equals(controller.getStartCell()) || cell.equals(controller.getEndCell()))
                    continue;

                SwingUtilities.invokeLater(() -> {
                    cells[cell.getRow()][cell.getCol()].setState(CellState.VISITA);
                    buttons[cell.getRow()][cell.getCol()].setBackground(PURPLE_TAUPE);
                    buttons[cell.getRow()][cell.getCol()].repaint();
                });

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            for (Cell cell : resultado.getPath()) {
                if (cell == null || cell.equals(controller.getStartCell()) || cell.equals(controller.getEndCell()))
                    continue;

                SwingUtilities.invokeLater(() -> {
                    cells[cell.getRow()][cell.getCol()].setState(CellState.CAMINO);
                    buttons[cell.getRow()][cell.getCol()].setBackground(Color.PINK);
                    buttons[cell.getRow()][cell.getCol()].repaint();
                });

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            SwingUtilities.invokeLater(this::actualizarInicioYFin);
        }).start();
    }

    public void resetear() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (cells[i][j].getState() != CellState.MURO) {
                    cells[i][j].setState(CellState.VACIO);
                }
            }
        }
        actualizarVisual();
    }


    public void actualizarVisual() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                Cell cell = cells[i][j];
                JButton button = buttons[i][j];

                switch (cell.getState()) {
                    case VACIO -> button.setBackground(Color.WHITE);
                    case MURO -> button.setBackground(Color.BLACK);
                    case INICIO -> button.setBackground(Color.GREEN);
                    case FINAL -> button.setBackground(Color.RED);
                    case VISITA -> button.setBackground(PURPLE_TAUPE);
                    case CAMINO -> button.setBackground(Color.PINK);
                }
            }
        }
    }


    private void actualizarInicioYFin() {
        if (controller.getStartCell() != null) {
            Cell s = controller.getStartCell();
            cells[s.getRow()][s.getCol()].setState(CellState.INICIO);
            JButton b = buttons[s.getRow()][s.getCol()];
            b.setBackground(Color.GREEN);
            b.repaint();
        }
        if (controller.getEndCell() != null) {
            Cell e = controller.getEndCell();
            cells[e.getRow()][e.getCol()].setState(CellState.FINAL);
            JButton b = buttons[e.getRow()][e.getCol()];
            b.setBackground(Color.RED);
            b.repaint();
        }
    }
}
