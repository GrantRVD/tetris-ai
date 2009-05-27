package tetris;
import boardrater.*;

public class Ply1FinalRaterBrain extends Ply1Brain {
	Ply1FinalRaterBrain() {
		boardRater = new FinalRater();
	}
}
