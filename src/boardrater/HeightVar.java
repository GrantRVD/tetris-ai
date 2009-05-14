package boardrater;

import tetris.Board;

public class HeightVar implements BoardRater {

	@Override
	public double rateBoard(Board board) {
		double avgHeight = new HeightAvg().rateBoard(board);
		
		// find the variance
		int varisum = 0;
		for (int x = 0; x < board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			varisum += Math.pow(colHeight - avgHeight, 2);
		}
		
		return varisum / board.getWidth();
	}

}
