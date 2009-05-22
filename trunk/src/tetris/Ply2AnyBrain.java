package tetris;

import boardrater.*;

//takes any BoardRater in the constructor, but defaults to a default-weighted FinalRater.

public class Ply2AnyBrain extends Ply2Brain {
  public Ply2AnyBrain() {
	  boardRater = new FinalRater();
  }
	public Ply2AnyBrain(BoardRater rater) {
	  if(rater==null)
		  boardRater = new FinalRater();
		boardRater = rater;
	}
}
