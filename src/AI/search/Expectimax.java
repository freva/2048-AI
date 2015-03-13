package AI.search;

import AI.heuristic.Heuristic;
import game.Board;

import static game.Board.isLost;
import static game.Board.move;

public class Expectimax extends GameTreeSearch {
    private double probabilityCutoff;
    
    public Expectimax(Heuristic heuristic) {
        this(heuristic, 0.0001);
    }

    public Expectimax(Heuristic heuristic, double probabilityCutoff) {
        super(heuristic);
        this.probabilityCutoff = probabilityCutoff;
    }

    public double search(long board, int depth) {
        return expectimax(board, depth, false, 1.0f);
    }


    private double expectimax(long board, int depth, boolean maximizingPlayer, double cprob) {
        if (depth == 0 || cprob < probabilityCutoff) return evaluate(board);
        else if (isLost(board)) return 1;

        if (maximizingPlayer) {
            double bestValue = Double.MIN_VALUE;
            for (int direction = 0; direction < 4; direction++) {
                long temp = move(board, direction);
                if (temp == board) continue;

                double val = expectimax(temp, depth - 1, false, cprob);
                if (val > bestValue) bestValue = val;
            }
            return bestValue;
        } else {
            double totalScore = 0, totalWeight = 0;
            cprob /= Board.numFreeCells(board);

            for (int i = 0; i < 64; i += 4) {
                if (((board >> i) & Board.CELL_MASK) != 0) continue;

                for(long tile = 1; tile<3; tile++) {
                    long temp = board;
                    temp |= tile << i;

                    double prob = (tile == 1) ? 0.9f : 0.1f;
                    totalScore += expectimax(temp, depth - 1, true, cprob*prob) * prob;
                    totalWeight += prob;
                }
            }
            return (totalWeight > 0) ? totalScore / totalWeight : expectimax(board, depth, true, cprob);
        }
    }
}
