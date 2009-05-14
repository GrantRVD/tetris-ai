package boardrater;

import tetris.Board;

/*
 * Default Genetic with all the boardraters but no weights.
 */

public class Genetic implements BoardRater {
	BoardRater boardRaters[] = 
	{
		new Grant1(),
		new HeightAvg(),
		new HeightMinMax(),
		new HeightVar(),
		new Holes1(),
		new Holes2(),
		new Lame(),
		new Leo1(),
		new Trough()
	};
	
	public double rateBoard(Board board) {
		
		double score = 0;
		
		for (BoardRater br : boardRaters) {
			score += br.rateBoard(board);
		}
		
		return score;
	}

}
