/*
 * JBrainTetris.java
 *
 * Created on January 31, 2002, 10:58 AM
 */

//package Hw2;
import javax.swing.Box;
import javax.swing.JCheckBox;

/**
 * 
 * @author Lews Therin
 * @version
 */
public class JBrainTetris extends JTetris {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Brain mBrain = new Ply2Brain();
	private Brain.Move mMove;

	/** Creates new JBrainTetris */
	public JBrainTetris(int width, int height) {
		super(width, height);
	}

	public void tick(int verb) {
		if (brainPlay.isSelected()) {
			tc.board.undo();
			if (verb == TetrisController.DOWN) {
				mMove = mBrain.bestMove(tc.board, tc.currentPiece,
						tc.nextPiece, tc.board.getHeight()
								- TetrisController.TOP_SPACE);

				//TODO: Figure out why this needs to be here
				if (mMove == null || mMove.piece == null
						|| tc.currentPiece == null) {
					stopGame();
					return; // game over
				}

				if (!tc.currentPiece.equals(mMove.piece))
					super.tick(TetrisController.ROTATE);

				if (tc.currentX < mMove.x)
					super.tick(TetrisController.RIGHT);
				if (tc.currentX > mMove.x)
					super.tick(TetrisController.LEFT);

			}
		}
		super.tick(verb);

	}

	// Controls
	protected JCheckBox brainPlay;

	public java.awt.Container createControlPanel() {
		java.awt.Container panel2 = Box.createVerticalBox();
		panel2 = super.createControlPanel();

		brainPlay = new JCheckBox("Brain Play", false);

		panel2.add(brainPlay);

		return (panel2);
	}

}
