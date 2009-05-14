package tetris;

import boardrater.BoardRater;
import boardrater.Lame;
import boardrater.Leo1;

public class Ply1Leo1Brain extends Ply1Brain {
	Ply1Leo1Brain() {
		boardRater = new Leo1();
	}
}