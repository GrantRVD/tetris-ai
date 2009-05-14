package boardrater;

import tetris.Board;

public class Trough implements BoardRater {

	public double rateBoard(Board board) {
		int[] troughs = new int[board.getWidth()];
		int troughCount = 0;
		
		for (int x = 0; x < board.getWidth(); x++)
		{
			int height = board.getColumnHeight(x); // Store the height for each column
			// The start of the trough will always be at heights[x] - 1, so we want to include this space in the
			// depth without changing our loop for counting holes
			if (height > 0 && !board.getGrid(x, height-1))
			{
				troughs[x]++;
				troughCount++;
			}
			
		}
		return troughCount;
	}

}
