package tetris;
// Board.java
//package Hw2;
import java.awt.Point;

/**
 * Represents a Tetris board -- essentially a 2-d grid of booleans. Supports
 * tetris pieces and row clearning. Has an "undo" feature that allows clients to
 * add and remove pieces efficiently. Does not do any drawing or have any idea
 * of pixels. Intead, just represents the abtsract 2-d board. See
 * Tetris-Architecture.html for an overview.
 * 
 * This is the starter file version -- a few simple things are filled in already
 * 
 * @author Nick Parlante
 * @version 1.0, Mar 1, 2001
 */
public class Board {
	protected int width; // width of the board
	protected int height;

	protected boolean[][] grid;
  
	/**
	 * Creates an empty board of the given width and height measured in blocks.
	 */
	public Board(int aWidth, int aHeight) {
		width = aWidth;
		height = aHeight;

		grid = new boolean[width][height];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				grid[x][y] = false;
			}
		}
	}
	
	public Board(Board o) {
		this(o.width, o.height);
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
					grid[x][y] = o.grid[x][y];
			}
		}
	}
	
	public Board clone() {
		Board cloned = new Board(width, height);
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
					cloned.grid[x][y] = grid[x][y];
			}
		}
		
		return cloned;
	}

  public void makeDirty() {
    maxHeightDirty = true;
    for(int i=0; i<10; i++) {
      columnHeightDirties[i] = true;
    }
  }

	/**
	 * Returns the width of the board in blocks.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the board in blocks.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the max column height present in the board. For an empty board
	 * this is 0.
	 */
	private boolean maxHeightDirty = true;
	private int _maxHeight = -1;
	public int getMaxHeight() {
	  if(!maxHeightDirty) return _maxHeight;
	  maxHeightDirty = false;
		int max = 0;
		for (int i = 0; i < width; i++) {
			if (getColumnHeight(i) > max)
				max = getColumnHeight(i);
		}
		return (_maxHeight = max);

	}

	/**
	 * Given a piece and an x, returns the y value where the piece would come to
	 * rest if it were dropped straight down at that x.
	 * 
	 * <p>
	 * Implementation: use the skirt and the col heights to compute this fast --
	 * O(skirt length).
	 */
	public int dropHeight(Piece piece, int x) {
		int high = 10000;
		int result = 0;
		int temp;
		int i;
		int[] skirt = piece.getSkirt();
		for (i = x; i < x + piece.getWidth(); i++) {
			temp = skirt[i - x] - getColumnHeight(i);
			if (temp < high) {
				high = temp;
				result = i;
			}
		}
		return getColumnHeight(result) - skirt[result - x];
	}

	/**
	 * Returns the height of the given column -- i.e. the y value of the highest
	 * block + 1. The height is 0 if the column contains no blocks.
	 */
	private boolean[] columnHeightDirties = {true,true,true,true,true,true,true,true,true,true};
	private int[] _columnHeights = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	public int getColumnHeight(int x) {
	    if(!columnHeightDirties[x]) return _columnHeights[x];
      columnHeightDirties[x] = false;
			for (int j = height - 1; j >= 0; j--) {
				if (grid[x][j]) {
					return (_columnHeights[x] = j+1);
				}
			}
			return (_columnHeights[x] = 0);
	}

	/**
	 * Returns the number of filled blocks in the given row.
	 */
	public int getRowCount(int y) {
		int count = 0;
		
		for (int x = 0; x < width; x++) {
			if (grid[x][y]) {
				count++;
			}
		}
		
		return count;
	}

	/**
	 * Returns true if the given block is filled in the board. Blocks outside of
	 * the valid width/height area always return true.
	 * 
	 */
	public final boolean getGrid(int x, int y) {
		if (x < 0 || x > width)
			return true;
		if (y < 0 || y > height)
			return true;
		return grid[x][y];
	}

	public boolean canPlace(Piece piece, int x, int y) {
		// might as well do all the error checking at once, since we must leave
		// the board unchanged if we've got
		// an out of bounds error - this has the added bonus of not altering the
		// board on a bad place
		for (Point block : piece.getBody()) {
			int putx = x + block.x;
			int puty = y + block.y;
			
			if (putx < 0 
					|| putx >= width 
					|| puty < 0 
					|| ((puty < height) && grid[putx][puty])) {
				return false;
			}
		}
		return true;
	}
	
	public boolean canPlace(Move move) {
		return canPlace(move.piece, move.x, move.y);
	}
	
	/**
	 * Attempts to add the body of a piece to the board. Copies the piece blocks
	 * into the board grid. Returns PLACE_OK for a regular placement, or
	 * PLACE_ROW_FILLED for a regular placement that causes at least one row to
	 * be filled.
	 * 
	 * <p>
	 * Error cases: If part of the piece would fall out of bounds, the placement
	 * does not change the board at all, and PLACE_OUT_BOUNDS is returned. If
	 * the placement is "bad" --interfering with existing blocks in the grid --
	 * then the placement is halted partially complete and PLACE_BAD is
	 * returned. An undo() will remove the bad placement.
	 * @throws Exception 
	 */
	public void place(Piece piece, int x, int y) {
		if (!canPlace(piece, x, y)) {
			return;
		}
		makeDirty();
		for (Point block : piece.getBody()) {
			grid[x + block.x][y + block.y] = true;
		}
	}
	
	public void place(Move move) {
		place(move.piece, move.x, move.y);
	}

	/**
	 * Deletes rows that are filled all the way across, moving things above
	 * down. Returns true if any row clearing happened.
	 * 
	 * <p>
	 * Implementation: This is complicated. Ideally, you want to copy each row
	 * down to its correct location in one pass. Note that more than one row may
	 * be filled.
	 */
	public int clearRows() {
		int cleared = 0;

		for (int i = 0; i < height; i++) {
			if (getRowCount(i) >= width) {
				cleared++;

				for (int j = 0; j < width; j++) {
					System
							.arraycopy(grid[j], i + 1, grid[j], i, height - 1
									- i);
					grid[j][height - 1] = false;
				}

				i--;
			}
		}
    makeDirty();
		return cleared;

	}
}
