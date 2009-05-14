package boardrater;

import tetris.Board;

public class Lame2 implements BoardRater {
	
	/*
	 * I changed the weights to something I think will work better
	 * I also considered consecutiveHoles as one long hole.
	 */
	public double rateBoard(Board board) {
			final int width = board.getWidth();
			final int maxHeight = board.getMaxHeight();
			
			int holes = 0;
			
			// Count the holes, and sum up the heights
			for (int x=0; x<width; x++) {
				final int colHeight = board.getColumnHeight(x);
				int y = colHeight - 2;	// addr of first possible hole
				
				boolean consecutiveHole = false;
				while (y>=0) {
					if  (!board.getGrid(x,y)) {
						if (!consecutiveHole) {
							holes++;
							consecutiveHole = true;
						}
					} else {
						consecutiveHole = false;
					}
					y--;
				}
			}
			
			// Add up the counts to make an overall score
			// The weights were changed
			return (new HeightVar().rateBoard(board) + Math.pow(maxHeight / 2, 2) * holes);	
	}
}
