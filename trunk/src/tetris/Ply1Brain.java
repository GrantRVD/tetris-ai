package tetris;
import boardrater.BoardRater;
import boardrater.Grant1;



public class Ply1Brain implements Brain
{
	Move move = new Move();
	BoardRater boardRater = new Grant1();

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

		move.x=bestX;
		move.y=bestY;
		move.piece=bestPiece;
		move.score = bestScore;
		return(move);

	}


}
