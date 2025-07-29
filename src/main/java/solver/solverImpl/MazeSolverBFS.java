package solver.solverImpl;

import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;

import java.util.*;

public class MazeSolverBFS implements MazeSolver {
    @Override
    public SolveResults getPath(Cell[][] maze, Cell start, Cell end) {
        int rows = maze.length;
        int cols = maze[0].length;
        boolean[][] visitedMatrix = new boolean[rows][cols];
        Map<Cell, Cell> parent = new HashMap<>();
        Queue<Cell> queue = new LinkedList<>();
        List<Cell> explored = new ArrayList<>();

        Cell inicio = maze[start.getRow()][start.getCol()];
        Cell destino = maze[end.getRow()][end.getCol()];
        queue.add(inicio);
        visitedMatrix[inicio.getRow()][inicio.getCol()] = true;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            explored.add(current);

            if (current.equals(destino)) break;

            for (int[] dir : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int r = current.getRow() + dir[0];
                int c = current.getCol() + dir[1];
                if (r >= 0 && r < rows && c >= 0 && c < cols) {
                    Cell neighbor = maze[r][c];
                    if (!visitedMatrix[r][c] && neighbor.getState() != CellState.MURO) {
                        visitedMatrix[r][c] = true;
                        parent.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }

        List<Cell> path = new ArrayList<>();
        Cell current = destino;
        while (parent.containsKey(current)) {
            path.add(current);
            current = parent.get(current);
        }

        if (current.equals(inicio)) {
            path.add(inicio);
            Collections.reverse(path);
        } else {
            path.clear();
        }

        return new SolveResults(path, new LinkedHashSet<>(explored));
    }
}