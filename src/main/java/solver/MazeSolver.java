package solver;

import models.Cell;
import models.SolveResults;

public interface MazeSolver {
    SolveResults getPath(Cell[][] maze, Cell start, Cell end);
}