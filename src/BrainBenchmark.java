import java.util.Random;

/**
 * No-frills brain benchmark
 *
 */
public class BrainBenchmark {
	class Result {
		String name;
		int score;
	}
	
	/* Add your yummy brains here NOM NOM NOM */
	static Brain brainz[] = {
			new LameBrain(),
			new LameBrain2(),
			new Ply1Brain(),
			new Ply2Brain()
		};
	
	private int cur_count = -1;
	
	BrainBenchmark() {
	}
	
	Result[] computeResults(int seed) {
		Result[] results = new Result[brainz.length];
		
		TetrisController tc = new TetrisController();
		
		for (int i = 0; i < brainz.length; i++) {
			tc.startGame();
			tc.random = new Random(seed);
			
			Brain.Move move = null;
			while(tc.gameOn) {
				tc.tick(TetrisController.DOWN);
				
				tc.board.undo();

					if(cur_count != tc.count) {
						move = brainz[i].bestMove(tc.board, tc.currentPiece, tc.nextPiece, tc.board.getHeight()-TetrisController.TOP_SPACE);
						cur_count = tc.count;
					}
				
				
					if(move == null || move.piece == null || tc.currentPiece == null) {
						tc.gameOn = false;
						break;
					}
					if(!tc.currentPiece.equals(move.piece)) tc.tick(TetrisController.ROTATE);

					if(tc.currentX < move.x) tc.tick(TetrisController.RIGHT);
					if(tc.currentX > move.x) tc.tick(TetrisController.LEFT);

			}
			
			results[i] = new Result();
			results[i].name = brainz[i].toString();
			results[i].score = tc.count;
		}
		
		return results;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BrainBenchmark bb = new BrainBenchmark();
		
		int sampleSize = 100;
		
		int scores[] = new int[brainz.length];
		
		for (int seed = 0; seed < sampleSize; seed++) {
			System.out.println("Seed "+seed);
			
			Result[] results = bb.computeResults(seed);
			
			for (int i = 0; i < results.length; i++) {
				System.out.println(results[i].name + " " + results[i].score);
				scores[i] += results[i].score;
			}
		}
		System.out.println("");
		
		System.out.println("Average Scores");
		for (int i = 0; i < brainz.length; i++) {
			System.out.println(brainz[i].toString() + " " + (scores[i]/sampleSize));
		}
		
	}

}
