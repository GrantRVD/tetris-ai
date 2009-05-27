package tetris;

import java.util.Date;
import java.util.Random;

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
	static Brain brain = new Ply2HandTunedBrain();

	static final int SAMPLE_SIZE = 5;

	SingleBrainTest() {
	}

	Result computeResult(int seed) {
    Result result;
		TetrisController tc = new TetrisController();

		tc.startGame(seed);

		Date start = new Date();

    long lastDisplay = System.nanoTime(),tempTime;
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
      
      if((tempTime=System.nanoTime()) - lastDisplay > 500000000) {
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

    Result sum = new Result();

		for (int seed = 0; seed < SAMPLE_SIZE; seed++) {
			System.out.println("Seed " + seed + " score time");

			Result result = bb.computeResult(seed);

			System.out.println(result.name + " " + result.score
					+ " " + result.thinkTime);
			sum.score += result.score;
			sum.thinkTime += result.thinkTime;
		}
		System.out.println("");

		System.out.println("Average Score");
		System.out.println(brain.toString() + " "
				+ (sum.score / SAMPLE_SIZE) + " "
				+ ((double) sum.score / sum.thinkTime));

	}

}
