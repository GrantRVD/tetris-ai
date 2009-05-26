import boardrater.*;
package tetris;

public class Ply2HandTunedBrain extends Ply2Brain {
	Ply2HandTunedBrain() {
		boardRater = new HandTunedFinalRater();
	}
}
