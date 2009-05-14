package tetris;

import boardrater.BoardRater;
import boardrater.Genetic;
import boardrater.Leo1;

public class Ply1GeneticBrain extends Ply1Brain {
	Ply1GeneticBrain() {
		boardRater = new Genetic();
	}
}