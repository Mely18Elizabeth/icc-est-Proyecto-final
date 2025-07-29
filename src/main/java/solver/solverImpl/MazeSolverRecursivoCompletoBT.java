package solver.solverImpl;

import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;

import java.util.*;

public class MazeSolverRecursivoCompletoBT implements MazeSolver {
    private Set<Cell> visited = new LinkedHashSet<>();
    private List<Cell> path = new ArrayList<>();

    @Override
    public SolveResults getPath(Cell[][] maze, Cell start, Cell end) {
        visited.clear();
        path.clear();
        findPath(maze, start.getRow(), start.getCol(), end);
        Collections.reverse(path);
        return new SolveResults(path, visited);
    }

    private boolean findPath(Cell[][] maze, int r, int c, Cell end) {
        if (!isValid(maze, r, c)) return false;

        Cell cell = maze[r][c];
        if (visited.contains(cell)) return false;

        visited.add(cell);
        path.add(cell);

        if (cell.equals(end)) return true;

        if (findPath(maze, r + 1, c, end) || findPath(maze, r, c + 1, end)
                || findPath(maze, r - 1, c, end) || findPath(maze, r, c - 1, end)) {
            return true;
        }

        path.remove(path.size() - 1);
        visited.remove(cell);
        return false;
    }

    private boolean isValid(Cell[][] maze, int r, int c) {
        return r >= 0 && r < maze.length && c >= 0 && c < maze[0].length
                && maze[r][c].getState() != CellState.MURO;
    }
}