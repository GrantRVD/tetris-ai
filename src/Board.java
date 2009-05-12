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

	// x____ holds undo values
	private boolean[][] xgrid;

	protected boolean committed = true;

	/**
	 * Creates an empty board of the given width and height measured in blocks.
	 */
	public Board(int aWidth, int aHeight) {
		width = aWidth;
		height = aHeight;

		grid = new boolean[width][height];
		xgrid = new boolean[width][height];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				grid[x][y] = false;
				xgrid[x][y] = false;
			}
		}
	}
	
	public Board(Board o) {
		this(o.width, o.height);
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
					grid[x][y] = o.grid[x][y];
					xgrid[x][y] = o.xgrid[x][y];
			}
		}
		committed = true;
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
	public int getMaxHeight() {
		int max = 0;
		for (int i = 0; i < width; i++) {
			if (getColumnHeight(i) > max)
				max = getColumnHeight(i);
		}
		return max;

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
	public int getColumnHeight(int x) {
			for (int j = height - 1; j >= 0; j--) {
				if (grid[x][j]) {
					return j + 1;
				}
			}
			return 0;
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

	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;

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
	 */
	public int place(Piece piece, int x, int y) {
		Point[] body = piece.getBody();
		int putx;
		int puty;
		boolean row_filled = false;

		if (!committed)
			throw new RuntimeException("Trying to place on uncommited board!!!");
		committed = false;
		backup();

		// might as well do all the error checking at once, since we must leave
		// the board unchanged if we've got
		// an out of bounds error - this has the added bonus of not altering the
		// board on a bad place
		for (int i = 0; i < body.length; i++) {
			putx = x + body[i].x;
			puty = y + body[i].y;
			if (putx < 0 || putx >= width)
				return PLACE_OUT_BOUNDS;
			if (puty < 0 || puty >= height)
				return PLACE_OUT_BOUNDS;
			if (grid[putx][puty])
				return PLACE_BAD;
		}
		for (int i = 0; i < body.length; i++) {
			putx = x + body[i].x;
			puty = y + body[i].y;

			grid[putx][puty] = true;
			
			if (getRowCount(puty) >= width)
				row_filled = true;

		}

		if (row_filled)
			return PLACE_ROW_FILLED;
		return PLACE_OK;
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
	public boolean clearRows() {
		boolean cleared = false;

		if (committed)
			backup(); // not calling immediately after place
		committed = false;

		for (int i = 0; i < height; i++) {
			if (getRowCount(i) >= width) {
				cleared = true;

				for (int j = 0; j < width; j++) {
					System
							.arraycopy(grid[j], i + 1, grid[j], i, height - 1
									- i);
					grid[j][height - 1] = false;
				}

				i--;
			}
		}

		return cleared;

	}

	/**
	 * If a place() happens, optionally followed by a clearRows(), a subsequent
	 * undo() reverts the board to its state before the place(). If the
	 * conditions for undo() are not met, such as calling undo() twice in a row,
	 * then the second undo() does nothing. See the overview docs.
	 */
	public void undo() {
		if (!committed) {
			boolean[][] temp2;

			temp2 = grid;
			grid = xgrid;
			xgrid = temp2;

			committed = true;
		}
	}

	protected void backup() {
		for (int i = 0; i < width; i++) {
			System.arraycopy(grid[i], 0, xgrid[i], 0, height);
		}
	}

	/**
	 * Puts the board in the committed state. See the overview docs.
	 */
	public void commit() {
		committed = true;
	}
}
