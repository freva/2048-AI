package AI.heuristic;

import game.Board;

public class AdvancedWeighted implements Heuristic {
    static final double[] heuristicTable = new double[65536];

    static final float SCORE_LOST_PENALTY = 200000.0f;
    static final float SCORE_MONOTONICITY_POWER = 4.0f;
    static final float SCORE_MONOTONICITY_WEIGHT = 47.0f;
    static final float SCORE_SUM_POWER = 3.5f;
    static final float SCORE_SUM_WEIGHT = 11.0f;
    static final float SCORE_MERGES_WEIGHT = 700.0f;
    static final float SCORE_EMPTY_WEIGHT = 270.0f;

    //http://stackoverflow.com/questions/22342854/what-is-the-optimal-algorithm-for-the-game-2048
    public AdvancedWeighted() {
        for (int row = 0; row < 65536; row++) {
            int[] line = {(row) & 0xf, (row >>  4) & 0xf, (row >>  8) & 0xf, (row >> 12) & 0xf};

            float sum = 0;
            int empty = 0, merges = 0, prev = 0, counter = 0;
            for (int i = 0; i < 4; ++i) {
                int rank = line[i];
                sum += Math.pow(rank, SCORE_SUM_POWER);

                if (rank == 0) empty++;
                else {
                    if (prev == rank) counter++;
                    else if (counter > 0) {
                        merges += 1 + counter;
                        counter = 0;
                    }
                    prev = rank;
                }
            }

            if (counter > 0) merges += 1 + counter;

            float monotonicity_left = 0, monotonicity_right = 0;
            for (int i = 1; i < 4; ++i) {
                if (line[i-1] > line[i])
                    monotonicity_left += Math.pow(line[i-1], SCORE_MONOTONICITY_POWER) - Math.pow(line[i], SCORE_MONOTONICITY_POWER);
                else
                    monotonicity_right += Math.pow(line[i], SCORE_MONOTONICITY_POWER) - Math.pow(line[i-1], SCORE_MONOTONICITY_POWER);
            }

            heuristicTable[row] = SCORE_LOST_PENALTY + SCORE_EMPTY_WEIGHT*empty + SCORE_MERGES_WEIGHT*merges - SCORE_MONOTONICITY_WEIGHT*Math.min(monotonicity_left, monotonicity_right) - SCORE_SUM_WEIGHT*sum;
        }
    }

    @Override
    public double evaluate(long board1) {
        double score1 = 0, score2 = 0;
        long board2 = Board.transposeBoard(board1);

        for(int i=0; i<64; i+=16) {
            int row1 = (int) ((board1 >> i) & Board.ROW_MASK), col1 = (int) ((board2 >> i) & Board.ROW_MASK);
            score1 += heuristicTable[row1];
            score2 += heuristicTable[col1];
        }

        return Math.max(score1, score2);
    }
}
