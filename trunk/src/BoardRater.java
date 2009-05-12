
public class BoardRater
{
	// All of the fields for this class are static so that a BoardRater does not need to be constructed to rate the board,
	// but if we did, we would be able to successively call different rating methods on the same board
	static Integer[] holes;
	static int holeCount;
	static Integer[] troughs;
	static int troughCount; // Troughs are essentially holes in the board that have nothing on top of them
	static int sumHeight;
	static int maxHeight;
	static int minHeight; // The board will never have columns 20 blocks high
	static Integer[] heights; // For storing each column's height
	
	public BoardRater()
	{
		
	}
	/**
	 * This board rater takes into account the number of holes, number of troughs, max height, minimum heigh, and average height
	 * to give the board a score.
	 * @param board
	 * @return
	 */
	public static double rateBoard(Board board)
	{
		holes = new Integer[board.getWidth()];
		troughs = new Integer[board.getWidth()];
		holeCount = 0;
		troughCount = 0;
		sumHeight = 0;
		maxHeight = 0;
		minHeight = 25; // The board will never have columns 20 blocks high
		heights = new Integer[board.getWidth()];
		int score = 0;
		
		// For each new time we rate the board, we reset the counts for holes, troughs, and column heights.
		for ( int i = 0; i < holes.length; i++)
		{
			holes[i] = 0;
			troughs[i] = 0;
			heights[i] = 0;
		}
		
		for (int x = 0; x < board.getWidth(); x++)
		{
			heights[x] = board.getColumnHeight(x); // Store the height for each column
			sumHeight += heights[x]; // Accumulates the sum of the column heights
			
			if (heights[x] > maxHeight)
				maxHeight = heights[x]; // Record height of highest column on the board
			if (heights[x] < minHeight)
				minHeight = heights[x]; // Record height of the lowest column on the board
			
			
			int y = heights[x] - 2;	// y-coordinate of the first possible hole
			
			
			// The start of the trough will always be at heights[x] - 1, so we want to include this space in the
			// depth without changing our loop for counting holes
			if (heights[x] > 0 && !board.getGrid(x, heights[x]-1))
			{
				troughs[x]++;
				troughCount++;
			}

			
			// Now we count the holes in the board and the depth of the trough.
			while(y >= 0)
			{
				if (!board.getGrid(x, y))
				{
					// holes[x]++; // increment the hole counter if (x,y) is empty
					holeCount++;
					
					// if the space above (x,y) is empty, then the trough is deeper
					//if (!board.getGrid(x, y+1))
						// troughs[x]++; 
						
					
				}
				y--;
			}
			
		}
		
		// Now we do some calculations for the average height.
		double avgHeight = ((double)sumHeight)/board.getWidth();
		
		// For this board rating method, the weights are arbitrary, but reflect more or less importance
		return (20*(maxHeight - minHeight) + 25*avgHeight + 20*holeCount + 15*troughCount);
		
	}
}
