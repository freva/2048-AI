import AI.heuristic.AdvancedWeighted;
import AI.heuristic.GridHeuristic;
import AI.search.Expectimax;
import game.Game;

public class Main {

    public static void main(String[] args) {
        Game.startNewGame(new Expectimax(GridHeuristic.CORNER_GRADIENT_EDGE_PRIORITY), 4);
    }
}
