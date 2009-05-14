package tetris;

import boardrater.BoardRater;
import boardrater.Grant1;

public class Ply1Grant1Brain extends Ply1Brain {
	Ply1Grant1Brain() {
		boardRater = new Grant1();
	}
}
