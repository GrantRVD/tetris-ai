package boardrater;

import tetris.Board;

public class HeightMinMax implements BoardRater {

	public double rateBoard(Board board) {
		int maxHeight = 0;
		int minHeight = board.getHeight();
		
		for (int x = 0; x < board.getWidth(); x++)
		{
			int height = board.getColumnHeight(x);
			if (height > maxHeight)
				maxHeight = height; // Record height of highest column on the board
			if (height < minHeight)
				minHeight = height; // Record height of the lowest column on the board
		}
		
		return maxHeight - minHeight;
	}

}
