import java.util.Random;

/**
 * No-frills brain benchmark
 *
 */
public class BrainBenchmark {
	/* Add your yummy brains here NOM NOM NOM */
	Brain brainz[] = {
			new LameBrain(),
			new LameBrain2(),
			new Ply1Brain(),
			new Ply2Brain()
		};
	
	private int cur_count = -1;
	
	BrainBenchmark() {
	}
	
	String[][] computeResults(int seed) {
		String[][] results = new String[brainz.length][2];
		
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
			
			results[i][0] = brainz[i].toString();
			results[i][1] = Integer.toString(tc.count);
		}
		
		return results;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BrainBenchmark bb = new BrainBenchmark();
		for (int seed = 0; seed < 10; seed++) {
			for (String[] result : bb.computeResults(seed)) {
				for (String col : result) {
					System.out.print(col + " ");
				}
				System.out.println();
			}
		}
	}

}
