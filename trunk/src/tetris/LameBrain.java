package tetris;

import boardrater.BoardRater;
import boardrater.Lame1;

// LameBrain.java

/**
 A simple Brain implementation.
 bestMove() iterates through all the possible x values
 and rotations to play a particular piece (there are only
 around 10-30 ways to play a piece).
 
 For each play, it uses the rateBoard() message to rate how
 good the resulting board is and it just remembers the
 play with the lowest score. Undo() is used to back-out
 each play before trying the next. To experiment with writing your own
 brain -- just subclass off LameBrain and override rateBoard().
*/
//package Hw2;
public class LameBrain implements Brain {
	Move move = new Move();
	BoardRater boardRater = new Lame1();
	
	/**
	 Given a piece and a board, returns a move object that represents
	 the best play for that piece, or returns null if no play is possible.
	 See the Brain interface for details.
	*/
	public Move bestMove(Board board, Piece piece, Piece nextPiece, int limitHeight) {
		double bestScore = 1e20;
		int bestX = 0;
		int bestY = 0;
		Piece bestPiece = null;
		Piece current = piece;
		
		// loop through all the rotations
		while (true) {
			final int yBound = limitHeight - current.getHeight()+1;
			final int xBound = board.getWidth() - current.getWidth()+1;
			
			// For current rotation, try all the possible columns
			for (int x = 0; x<xBound; x++) {
				int y = board.dropHeight(current, x);
				if (y<yBound) {	// piece does not stick up too far
					int result = board.place(current, x, y);
					if (result <= Board.PLACE_ROW_FILLED) {
						if (result == Board.PLACE_ROW_FILLED) board.clearRows();
						
						double score = boardRater.rateBoard(board);
						
						if (score<bestScore) {
							bestScore = score;
							bestX = x;
							bestY = y;
							bestPiece = current;
						}
					}
					
					board.undo();	// back out that play, loop around for the next
				}
			}
			
			current = current.nextRotation();
			if (current == piece) break;	// break if back to original rotation
		}
		
		if (bestPiece == null) return(null);	// could not find a play at all!
		else {
			move.x=bestX;
			move.y=bestY;
			move.piece=bestPiece;
			move.score = bestScore;
			return(move);
		}
	}

}

