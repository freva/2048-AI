package AI.search;

import AI.heuristic.Heuristic;
import java.util.stream.IntStream;

import static game.Board.move;

public abstract class GameTreeSearch {
    private Heuristic heuristic;


    public GameTreeSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }


    public int getBestMove(long board, final int depth) {
        double[] scores = IntStream.range(0, 4).mapToLong(i -> move(board, i)).parallel().mapToDouble(i -> (i != board ? search(i, depth - 1) : Double.MIN_VALUE)).toArray();
        return IntStream.range(0, 4).reduce((a, b) -> scores[a] < scores[b] ? b : a).getAsInt();
    }


    public double evaluate(long board) {
        return heuristic.evaluate(board);
    }


    abstract public double search(long board, int depth);
}
