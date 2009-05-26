package tetris;
import boardrater.*;

public class Ply2HandTunedBrain extends Ply2Brain {
	Ply2HandTunedBrain() {
		boardRater = new HandTunedFinalRater();
	}
}
