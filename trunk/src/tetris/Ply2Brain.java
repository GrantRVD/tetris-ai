package tetris;
import boardrater.Grant1;



public class Ply2Brain implements Brain
{

	Move move = new Move();

	public Move bestMove(Board board, Piece piece, Piece nextPiece, int limitHeight) {
		double bestScore = 1e20;
		int bestX = 0;
		int bestY = 0;
		Piece bestPiece = null;
		Piece current = piece;
		Piece next = nextPiece;
		Board temp;

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

						double currentScore = new Grant1().rateBoard(board);
						double nextScore;
						temp = new Board(board);
						temp.commit();

						// Everything in this while loop evaluates possible moves with the next piece
						while(true)
						{
							final int jBound = limitHeight - next.getHeight()+1;
							final int iBound = temp.getWidth() - next.getWidth()+1;
							
							for(int i = 0; i < iBound; i++)
							{
								int j = temp.dropHeight(next, i);
								if(j < jBound) {
									int r = temp.place(next, i, j);
									if (r <= Board.PLACE_ROW_FILLED)
									{
										if(r == Board.PLACE_ROW_FILLED)
											temp.clearRows();
										
										nextScore = new Grant1().rateBoard(temp);
										
										if((currentScore + nextScore) < bestScore)
										{
											bestScore = currentScore + nextScore;
											bestX = x;
											bestY = y;
											bestPiece = current;
										}
									}
									temp.undo();
								}

							}
							next = next.nextRotation();
							if(next == nextPiece) break;
						}
						// Back out to the current piece

					}

					board.undo();	// Reset the board for the next rotation
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
