package tetris;
import java.util.Random;

public class TetrisController {
	// size of the board in blocks
	public static final int WIDTH = 10; //10
	public static final int HEIGHT = 20; //20

	// Extra blocks at the top for pieces to start.
	// If a piece is sticking up into this area
	// when it has landed -- game over!
	public static final int TOP_SPACE = 4;

	// Board data structures
	protected DisplayBoard board;
	protected DisplayPiece[] pieces;

	// The current piece in play or null
	protected DisplayPiece nextPiece; // The piece which will be generated next
	protected DisplayPiece currentPiece;
	protected int currentX;
	protected int currentY;
	protected boolean moved;	// If the player moved the piece, true; else false

	// The piece we're thinking about playing
	// -- set by computeNewPosition
	// (storing this in ivars is slightly questionable style)
	protected DisplayPiece newPiece;
	protected int newX;
	protected int newY;

	// State of the game
	protected boolean gameOn;	// true if we are playing
	protected int count;		// how many pieces played so far

	protected Random random;	// the random generator for new pieces
	
	TetrisController() {
		gameOn = false;

		pieces = DisplayPiece.getPieces();
		board = new DisplayBoard(WIDTH, HEIGHT + TOP_SPACE);
	}

	public static final int ROTATE = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int DROP = 3;
	public static final int DOWN = 4;
	/**
	 Called to change the position of the current piece.
	 Each key press call this once with the verbs
	 LEFT RIGHT ROTATE DROP for the user moves,
	 and the timer calls it with the verb DOWN to move
	 the piece down one square.

	 Before this is called, the piece is at some location in the board.
	 This advances the piece to be at its next location.

	 Overriden by the brain when it plays.
	 */
	public void tick(int verb) {
		if (!gameOn) return;

		if (currentPiece != null) {
			board.undo();	// remove the piece from its old position
		}

		// Sets the newXXX ivars
		computeNewPosition(verb);

		// try out the new position (rolls back if it doesn't work)
		int result = setCurrent(newPiece, newX, newY);

		boolean failed = (result >= Board.PLACE_OUT_BOUNDS);

		// if it didn't work, put it back the way it was
		if (failed) {
			if (currentPiece != null) board.place(currentPiece, currentX, currentY);
		}

		/*
		 How to detect when a piece has landed:
		 if this move hits something on its DOWN verb,
		 and the previous verb was also DOWN (i.e. the player was not
		 still moving it),  then the previous position must be the correct
		 "landed" position, so we're done with the falling of this piece.
		 */
		if (failed && verb==DOWN && !moved) {	// it's landed		
			board.clearRows();
			// if the board is too tall, we've lost
			if (board.getMaxHeight() > board.getHeight() - TOP_SPACE) {
				gameOn = false;
			}
			// Otherwise add a new piece and keep playing
			else {
				addNewPiece();
			}
		}

		// Note if the player made a successful non-DOWN move --
		// used to detect if the piece has landed on the next tick()
		moved = (!failed && verb!=DOWN);
	}

	/**
	 Figures a new position for the current piece
	 based on the given verb (LEFT, RIGHT, ...).
	 The board should be in the committed state --
	 i.e. the piece should not be in the board at the moment.
	 This is necessary so dropHeight() may be called without
	 the piece "hitting itself" on the way down.

	 Sets the ivars newX, newY, and newPiece to hold
	 what it thinks the new piece position should be.
	 (Storing an intermediate result like that in
	 ivars is a little tacky.)
	 */
	public void computeNewPosition(int verb) {
		// As a starting point, the new position is the same as the old
		newPiece = currentPiece;
		newX = currentX;
		newY = currentY;

		// Make changes based on the verb
		switch (verb) {
		case LEFT: newX--; break;

		case RIGHT: newX++; break;

		case ROTATE:
			newPiece = newPiece.nextRotation();

			// tricky: make the piece appear to rotate about its center
			// can't just leave it at the same lower-left origin as the
			// previous piece.
			newX = newX + (currentPiece.getWidth() - newPiece.getWidth())/2;
			newY = newY + (currentPiece.getHeight() - newPiece.getHeight())/2;
			break;

		case DOWN: newY--; break;

		case DROP:
			newY = board.dropHeight(newPiece, newX);

			// trick: avoid the case where the drop would cause
			// the piece to appear to move up
			if (newY > currentY) {
				newY = currentY;
			}
			break;

		default:
			throw new RuntimeException("Bad verb");
		}

	}

	/**
	 Sets the internal state and starts the timer
	 so the game is happening.
	 */
	public void startGame() {
		// cheap way to reset the board state
		board = new DisplayBoard(WIDTH, HEIGHT + TOP_SPACE);

		count = 0;
		gameOn = true;

		random = new Random();	// diff seq each game

		nextPiece = pickNextPiece();

		addNewPiece();

	}


	/**
	 Given a piece, tries to install that piece
	 into the board and set it to be the current piece.
	 Does the necessary repaints.
	 If the placement is not possible, then the placement
	 is undone, and the board is not changed. The board
	 should be in the committed state when this is called.
	 Returns the same error code as Board.place().
	 */
	public int setCurrent(DisplayPiece piece, int x, int y) {
		int result = board.place(piece, x, y);

		if (result <= Board.PLACE_ROW_FILLED) {	// SUCCESS
			currentPiece = piece;
			currentX = x;
			currentY = y;
		}
		else {
			board.undo();
		}

		return(result);
	}

	/**
	 Selects the next piece to use using the random generator
	 set in startGame().
	 */
	public DisplayPiece pickNextPiece() {	
		return pieces[random.nextInt(pieces.length)];
	}

	/**
	 Tries to add a new random piece at the top of the board.
	 Ends the game if it's not possible.
	 */
	public void addNewPiece() {
		count++;

		// commit things the way they are
		board.commit();
		currentPiece = null;

		DisplayPiece piece = nextPiece;
		nextPiece = pickNextPiece();

		// Center it up at the top
		int px = (board.getWidth() - piece.getWidth())/2;
		int py = board.getHeight() - piece.getHeight();

		// add the new piece to be in play
		int result = setCurrent(piece, px, py);

		// This probably never happens, since
		// the blocks at the top allow space
		// for new pieces to at least be added.
		if (result>Board.PLACE_ROW_FILLED) {
			gameOn = false;
		}
	}
}