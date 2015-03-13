package AI.search;

import AI.heuristic.Heuristic;
import game.Board;

import static game.Board.isLost;
import static game.Board.move;

public class AlphaBeta extends GameTreeSearch {
    public AlphaBeta(Heuristic heuristic) {
        super(heuristic);
    }

    @Override
    public double search(long board, int depth) {
        return alphabeta(board, depth, false, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    private double alphabeta(long board, int depth, boolean maximizingPlayer, double alpha, double beta) {
        if (depth == 0) return evaluate(board);
        else if (isLost(board)) return 1;

        if (maximizingPlayer) {
            for (int direction = 0; direction < 4; direction++) {
                long temp = move(board, direction);
                if (temp == board) continue;

                alpha = Math.max(alpha, alphabeta(temp, depth - 1, false, alpha, beta));
                if (beta <= alpha) break;
            }
            return alpha;
        } else {
            for (int i = 0; i < 64; i += 4) {
                if (((board >> i) & Board.CELL_MASK) != 0) continue;
                long temp = board;
                temp |= 1L << i;

                beta = Math.min(beta, alphabeta(temp, depth - 1, true, alpha, beta));
                if (beta <= alpha) break;
            }
            return beta;
        }
    }
}
