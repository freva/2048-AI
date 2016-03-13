package AI.heuristic;

import game.Board;

public class AdvancedWeighted implements Heuristic {
    private static final double[] heuristicTable = new double[65536];

    private static final double SCORE_LOST_PENALTY = 200000.0;
    private static final double SCORE_MONOTONICITY_POWER = 4.0;
    private static final double SCORE_MONOTONICITY_WEIGHT = 47.0;
    private static final double SCORE_SUM_POWER = 3.5;
    private static final double SCORE_SUM_WEIGHT = 11.0;
    private static final double SCORE_MERGES_WEIGHT = 700.0;
    private static final double SCORE_EMPTY_WEIGHT = 270.0;


    //http://stackoverflow.com/questions/22342854/what-is-the-optimal-algorithm-for-the-game-2048
    static  {
        for (int row = 0; row < 65536; row++) {
            int[] line = {(row) & 0xf, (row >>  4) & 0xf, (row >>  8) & 0xf, (row >> 12) & 0xf};

            double sum = 0;
            int empty = 0, merges = 0, prev = 0, counter = 0;
            for (int i = 0; i < 4; ++i) {
                int rank = line[i];
                sum += Math.pow(rank, SCORE_SUM_POWER);

                if (rank == 0) {
                    empty++;
                } else {
                    if (prev == rank) {
                        counter++;
                    } else if (counter > 0) {
                        merges += 1 + counter;
                        counter = 0;
                    }
                    prev = rank;
                }
            }

            if (counter > 0) {
                merges += 1 + counter;
            }

            double monotonicity_left = 0, monotonicity_right = 0;
            for (int i = 1; i < 4; ++i) {
                if (line[i - 1] > line[i]) {
                    monotonicity_left += Math.pow(line[i - 1], SCORE_MONOTONICITY_POWER) - Math.pow(line[i], SCORE_MONOTONICITY_POWER);
                } else {
                    monotonicity_right += Math.pow(line[i], SCORE_MONOTONICITY_POWER) - Math.pow(line[i - 1], SCORE_MONOTONICITY_POWER);
                }
            }

            heuristicTable[row] = SCORE_LOST_PENALTY +
                    SCORE_EMPTY_WEIGHT * empty +
                    SCORE_MERGES_WEIGHT * merges -
                    SCORE_MONOTONICITY_WEIGHT * Math.min(monotonicity_left, monotonicity_right) -
                    SCORE_SUM_WEIGHT * sum;
        }
    }

    @Override
    public double evaluate(long board1) {
        double score = 0;
        long board2 = Board.transposeBoard(board1);

        for(int i=0; i<64; i+=16) {
            score += heuristicTable[(int) ((board1 >> i) & Board.ROW_MASK)] +
                     heuristicTable[(int) ((board2 >> i) & Board.ROW_MASK)];
        }

        return score;
    }
}
