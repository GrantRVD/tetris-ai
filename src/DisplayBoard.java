import java.awt.Color;
import java.awt.Point;


public class DisplayBoard extends Board {
	Color[][] colorGrid;

	public DisplayBoard(int width, int height) {
		super(width, height);
		colorGrid = new Color[width][height];
	}
	
	public int place(DisplayPiece piece, int x, int y) {
        Point[] body = piece.getBody();
        int putx;
        int puty;
        boolean row_filled = false;
        
        if(!committed) throw new RuntimeException("Trying to place on uncommited board!!!");
        committed = false;
        backup();
        
        // might as well do all the error checking at once, since we must leave the board unchanged if we've got
        // an out of bounds error - this has the added bonus of not altering the board on a bad place
        for(int i = 0; i < body.length; i++) {
            putx = x+body[i].x;
            puty = y+body[i].y;
            if(putx<0 || putx>=width) return PLACE_OUT_BOUNDS;
            if(puty<0 || puty>=height) return PLACE_OUT_BOUNDS;
            if(grid[putx][puty]) return PLACE_BAD;
        }
        
		for (int i = 0; i < body.length; i++) {
			putx = x + body[i].x;
			puty = y + body[i].y;

			grid[putx][puty] = true;
			colorGrid[putx][puty] = piece.color;
			
			if (getRowCount(puty) >= width)
				row_filled = true;

		}

        
        
        if(row_filled) return PLACE_ROW_FILLED;
        return PLACE_OK;
	}
	
	public boolean clearRows() {
		boolean cleared = false;

		if (committed)
			backup(); // not calling immediately after place
		committed = false;

		for (int i = 0; i < height; i++) {
			if (getRowCount(i) >= width) {
				cleared = true;

				for (int j = 0; j < width; j++) {
					grid[j][i] = grid[j][i + 1];
					grid[j][height - 1] = false;
					colorGrid[j][i] = colorGrid[j][i + 1];	
				}

				i--;
			}
		}

		return cleared;

	}
}
