package tetris;

import boardrater.BoardRater;
import boardrater.Lame;

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
public class Ply1LameBrain extends Ply1Brain {	
	Ply1LameBrain() {
		boardRater = new Lame();
	}
}

