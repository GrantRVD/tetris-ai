package tetris;
import boardrater.*;

public class Ply2FinalRaterBrain extends Ply2Brain {
	Ply2FinalRaterBrain() {
		boardRater = new FinalRater();
	}
}
