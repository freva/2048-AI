package game;

public class Board {
    private long board, time;
    private int moves;

    private static final int[] table_lookup = new int[65536];
    public static final int ROW_MASK = 0xFFFF;
    public static final int CELL_MASK = 0xF;

    static {
        for (int row = 0; row < 65536; row++) {
            int[] line = new int[]{row & CELL_MASK, (row >> 4) & CELL_MASK, (row >> 8) & CELL_MASK, (row >> 12) & CELL_MASK};

            for (int i = 0, j; i < 3; i++) {
                for (j = i + 1; j < 4; j++)
                    if (line[j] != 0) break;
                if (j == 4) break;

                if (line[i] == 0) {
                    line[i--] = line[j];
                    line[j] = 0;
                } else if (line[i] == line[j] && line[i] != CELL_MASK) {
                    line[i]++;
                    line[j] = 0;
                }
            }

            table_lookup[row] = line[0] | (line[1] << 4) | (line[2] << 8) | (line[3] << 12);
        }
    }


    public Board() {
        this.board = ((Math.random() < 0.9) ? 1L : 2L) << ((int) (Math.random()*16))*4;
        this.time = System.currentTimeMillis();
    }


    public boolean makeMove(int direction) {
        long temp = move(this.board, direction);

        boolean moved = this.board != temp;
        if (moved) {
            this.board = temp;
            this.board = addRandomTile(this.board);
            this.moves++;
        }

        return moved;
    }
    

    private static long addRandomTile(long board) {
        long tile = (Math.random() < 0.9) ? 1 : 2;
        int numCells = numFreeCells(board);
        if(numCells == 0) return board;
        int index = (int) (Math.random() * numCells);

        for (int i=0; i<64; i+=4) {
            if (((board>>i) & CELL_MASK) != 0) continue;
            if(index-- == 0) return board | tile<<i;
        }

        return board;
    }


    public static int numFreeCells(long x) {
        x |= (x >> 2) & 0x3333333333333333L;
        x |= (x >> 1);
        x = ~x & 0x1111111111111111L;

        x += x >> 32;
        x += x >> 16;
        x += x >> 8;
        x += x >> 4;
        return (int) x & CELL_MASK;
    }


    public static short countDistinctTiles(long board) {
        short tileValues=0, counter=0;
        for(int i=0; i<64; i+=4)
            tileValues |= 1<<((board>>i) & CELL_MASK);

        for(int i=1; i<16; i++)
            if(((tileValues>>i) & 1) == 1) counter++;
        return counter;
    }


    public static boolean isLost(long board) {
        if (numFreeCells(board) > 0) return false;

        for(int i=0; i<64; i+=16) {
            for(int j=0; j<16; j+=4) {
                if (j < 12 && ((board >> (i + j)) & CELL_MASK) == ((board >> (i + j + 4)) & CELL_MASK)) return false;
                if (i < 48 && ((board >> (i + j)) & CELL_MASK) == ((board >> (i + j + 16)) & CELL_MASK)) return false;
            }
        }
        return true;
    }


    public static long move(long board, int direction) {
        if((direction & 1) == 0) board = transposeBoard(board);
        long ret = 0;
        if(direction == 0 || direction == 3) {
            for(int i=0; i<64; i+=16) {
                int row = (int) ((board >> i) & ROW_MASK);
                ret |= (long) table_lookup[row] << i;
            }
        } else {
            for(int i=0; i<64; i+=16) {
                int row = reverseRow((int) ((board >> i) & ROW_MASK));
                ret |= (long) reverseRow(table_lookup[row]) << i;
            }
        }
        return ((direction & 1) == 0) ? transposeBoard(ret) : ret;
    }


    public static long transposeBoard(long x) {
        long threes     = (x & 0x0F0000F0000F0000L) >> 12;
        long six        = (x & 0x00F0000F00000000L) >> 24;
        long nines      = (x & 0x000F000000000000L) >> 36;
        long nThrees    = (x & 0x0000F0000F0000F0L) << 12;
        long nSix       = (x & 0x00000000F0000F00L) << 24;
        long nNines     = (x & 0x000000000000F000L) << 36;
        long zeros      = (x & 0xF0000F0000F0000FL);
        return threes | six | nines | nThrees | nSix | nNines | zeros;
    }

    private static int reverseRow(int row) {
        return (row >> 12) | ((row >> 4) & 0x00F0)  | ((row << 4) & 0x0F00) | ((row << 12) & 0xF000);
    }

    
    public long getBoard() {
        return board;
    }


    public int getScore() {
        int sum = 0;
        for(int i=0; i<64; i+=4) {
            byte tile = (byte) ((board>>i) & CELL_MASK);
            if (tile > 1) sum += (tile - 1) * (1 << tile);
        }
        return sum;
    }


    public int getTimeSpent() {
        return (int) (System.currentTimeMillis()-time);
    }


    public int getMoves() {
        return moves;
    }
}
