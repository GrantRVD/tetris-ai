package boardrater;

import tetris.Board;

public class Lame2 implements BoardRater {
	
	/*
	 * I changed the weights to something I think will work better
	 * I also considered consecutiveHoles as one long hole.
	 */
	public double rateBoard(Board board) {
			// Add up the counts to make an overall score
			// The weights were changed
			return (new HeightVar().rateBoard(board) + Math.pow(board.getMaxHeight() / 2, 2) * new Holes2().rateBoard(board));	
	}
}
