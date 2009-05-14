package tetris;

public class LameBrain2 extends LameBrain {
	
	/*
	 * I changed the weights to something I think will work better
	 * I also considered consecutiveHoles as one long hole.
	 */
	public double rateBoard(Board board) {
			final int width = board.getWidth();
			final int maxHeight = board.getMaxHeight();
			
			int sumHeight = 0;
			int holes = 0;
			
			// Count the holes, and sum up the heights
			for (int x=0; x<width; x++) {
				final int colHeight = board.getColumnHeight(x);
				sumHeight += colHeight;
				
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
			double avgHeight = ((double)sumHeight)/width;
			
			// find the variance
			int varisum = 0;
			for (int x = 0; x < width; x++) {
				final int colHeight = board.getColumnHeight(x);
				varisum += Math.pow(colHeight - avgHeight, 2);
			}
			double variance = varisum / width;
			
			// Add up the counts to make an overall score
			// The weights were changed
			return (variance + Math.pow(maxHeight / 2, 2) * holes);	
	}
}
