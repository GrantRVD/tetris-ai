package tetris;

import boardrater.Grant1;

public class PlyNGrant1Brain extends PlyNBrain {
	PlyNGrant1Brain(int level, Piece[] possiblePieces) {
		boardRater = new Grant1();
		this.level = level;
		
		this.possiblePieces = possiblePieces;
	}
}