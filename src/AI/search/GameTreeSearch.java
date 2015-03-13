package AI.search;

import AI.heuristic.Heuristic;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static game.Board.move;

public abstract class GameTreeSearch {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static FutureTask[] tasks = new FutureTask[4];
    private Heuristic heuristic;


    public GameTreeSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }


    public int getBestMove(long board, final int depth) {
        double bestValue = Double.MIN_VALUE;
        int bestAction = -1;

        for(int direction=0; direction<4; direction++) {
            final long temp = move(board, direction);
            if (temp == board) {
                tasks[direction] = null;
                continue;
            }

            tasks[direction] = new FutureTask<>(() -> search(temp, depth - 1));

            executor.execute(tasks[direction]);
        }

        for (int j = 0; j < 4; j++) {
            if(tasks[j] == null) continue;
            try {
                double val = (Double) tasks[j].get();

                if (val >= bestValue) {
                    bestValue = val;
                    bestAction = j;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return bestAction;
    }


    public double evaluate(long board) {
        return heuristic.evaluate(board);
    }


    abstract public double search(long board, int depth);
}
