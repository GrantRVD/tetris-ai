import java.awt.Color;
import java.awt.Point;


public class DisplayBoard extends Board {
	Color[][] colorGrid;

	public DisplayBoard(int width, int height) {
		super(width, height);
		colorGrid = new Color[width][height];
	}

	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(DisplayPiece piece, int x) {
           int high = 10000;
           int result = 0;
           int temp;
           int i;
           int[] skirt = piece.getSkirt();
           for(i = x; i < x + piece.getWidth(); i++) {
               temp = skirt[i-x]-heights[i];
               if(temp < high) {
                   high = temp;
                   result = i;
               }
           }
           return heights[result]-skirt[result-x];
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
        for(int i = 0; i < body.length; i++) {
            putx = x+body[i].x;
            puty = y+body[i].y;
            
            grid[putx][puty] = true;
            colorGrid[putx][puty] = piece.color;
            widths[puty]++;
            if(puty+1 > heights[putx]) heights[putx] = puty+1;
            
            if(widths[puty] >= width) row_filled = true;
            
        }
        
        sanityCheck();
        
        if(row_filled) return PLACE_ROW_FILLED;
        return PLACE_OK;
	}
}
