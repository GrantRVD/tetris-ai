package tetris;

import java.util.Date;
import java.util.Random;
import java.lang.*;

import boardrater.Leo1;

/**
 * No-frills brain benchmark
 * 
 */
public class SingleBrainTest {
	static class Result {
		String name;
		int score = 0;
		long thinkTime = 0;
	}

	/* Add your yummy brain here NOM NOM NOM */
	static Brain brain = new Ply2Brain();

	static final int SAMPLE_SIZE = 1;

	SingleBrainTest() {
	}

	Result computeResult(int seed) {
    Result result;
		TetrisController tc = new TetrisController();

		tc.startGame(seed);

		Date start = new Date();

    long lastDisplay = System.currentTimeMillis(),tempTime;
		while (tc.gameOn) {
			Move move = brain.bestMove(new Board(tc.board),
					tc.currentMove.piece, tc.nextPiece, tc.board
							.getHeight()
							- TetrisController.TOP_SPACE);

			while (!tc.currentMove.piece.equals(move.piece)) {
				tc.tick(TetrisController.ROTATE);
			}

			while (tc.currentMove.x != move.x) {
				tc.tick(((tc.currentMove.x < move.x) ? TetrisController.RIGHT : TetrisController.LEFT));
			}

			int current_count = tc.count;
			while ((current_count == tc.count) && tc.gameOn) {
				tc.tick(TetrisController.DOWN);
			}
      
      if((tempTime=System.currentTimeMillis()) - lastDisplay > 20000) {
        lastDisplay = tempTime;
        System.out.print("..."+tc.count);
      }
		}
    System.out.println();
		result = new Result();
		result.thinkTime = (new Date().getTime() - start.getTime());
		result.name = brain.toString();
		result.score = tc.count;

		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SingleBrainTest bb = new SingleBrainTest();
		int seed = 0;
    if(args.length==1) seed = Integer.parseInt(args[0]);

		System.out.println("Running with seed " + seed+":");

		Result result = bb.computeResult(seed);

		System.out.println("");

		System.out.println("Performance:");
		System.out.println(brain.toString() + " "
				+ (result.score) + " "
				+ ((double) result.score / result.thinkTime));

	}

}
