package boardrater;
import tetris.Board;

/*
 * Default Genetic with all the boardraters but no weights.
 */

//I don't think the genetic algorithm will really have this form.
//The genetic algorithm will be in the form of a method that runs
//for a LONG, LONG time and then returns a list of coefficients.
//It's not actually a rater itself... just a coefficient finder.
//So, I don't know how long-lived this file will be.

public class Genetic extends BoardRater {
 BoardRater boardRaters[] = 
 {
   // new Grant1(),
   // new HeightAvg(),
   // new HeightMinMax(),
   // new HeightVar(),
   // new Holes1(),
   // new Holes2(),
   // new Lame(),
   // new Leo1(),
   // new Trough()
 };
 
 double rate(Board board) {
   
   double score = 0;
   
   for (BoardRater br : boardRaters) {
     score += br.rateBoard(board);
   }
   
   return score;
 }

}
