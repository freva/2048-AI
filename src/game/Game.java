package game;

import AI.search.GameTreeSearch;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

public class Game extends JPanel {
    private GUI gui;
    private Board board;

    public Game() {
        this.gui = new GUI();
        this.board = new Board();

        setFocusable(true);
        setBackground(new Color(0xbbada0));
    }


    public void AI(GameTreeSearch ai, int baselineSearchDepth) {
        while(! Board.isLost(board.getBoard())) {
            board.makeMove(ai.getBestMove(board.getBoard(), baselineSearchDepth + Math.max(0, Board.countDistinctTiles(board.getBoard()) - 5)));
            repaint();
        }

        Integer[] tiles = new Integer[16];
        for(int i=0; i<64; i+=4) tiles[i>>2] = 1 << ((board.getBoard()>>i) & Board.CELL_MASK);
        Arrays.sort(tiles, Collections.reverseOrder());
        System.out.println("Score: " + board.getScore() + " | Moves: " + board.getMoves() + " | Time: " + board.getTimeSpent() + "ms | Tiles: " + Arrays.toString(Arrays.copyOfRange(tiles, 0, 5)));
    }


    @Override
    public void paint(Graphics g){
        super.paint(g);
        gui.drawBoard(g, board.getBoard());
    }


    public static void startNewGame(GameTreeSearch gts, int depth) {
        JFrame game = new JFrame();
        game.setTitle("2048");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setResizable(false);
        game.pack();
        game.setSize(592 + game.getInsets().left + game.getInsets().right, 592 + game.getInsets().top + game.getInsets().bottom);

        Game p = new Game();
        game.add(p);
        game.setLocationRelativeTo(null);
        game.setVisible(true);

        p.AI(gts, depth);
    }
}