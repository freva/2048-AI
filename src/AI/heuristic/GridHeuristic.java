package AI.heuristic;

import game.Board;

public enum GridHeuristic implements Heuristic {
    CONTINUOUS_SNAKE(new long[]{0xfedc89ab7654012L, 0x078f169e25ad34bcL, 0x32104567ba98cdefL, 0xcb43da52e961f870L}),
    DISCONTINUOUS_SNAKE(new long[]{0xfedcba9876543210L, 0x37bf26ae159d048cL, 0x0123456789abcdefL, 0xc840d951ea62fb73L}),
    CORNER_GRADIENT(new long[]{0x7654654354324321L, 0x4567345623451234L, 0x1234234534564567L, 0x4321543265437654L}),
    CORNER_GRADIENT_EDGE_PRIORITY(new long[]{0xa987965485327421L, 0x789a456923581247L, 0x124723584569789aL, 0x742185329654a987L});


    private long[] paths;
    GridHeuristic(long[] paths) {
        this.paths = paths;
    }


    public double evaluate(long board) {
        int points = 0;
        for (long path : paths) {
            int temp = multiplyMask(board, path);
            if (temp > points) points = temp;
        }
        return points;
    }


    private static int multiplyMask(long board, long mask) {
        int sum = 0;
        for(int i=0; i<64; i+=4)
            sum += (1 << ((board>>i) & Board.CELL_MASK)) * ((mask>>i) & Board.CELL_MASK);

        return sum;
    }
}
