package tetris;

import boardrater.*;

public class Ply1GeneticBrain extends Ply1Brain {
	Ply1GeneticBrain() {
		boardRater = new FinalRater();
	}
}