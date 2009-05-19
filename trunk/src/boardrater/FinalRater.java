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

public class FinalRater extends BoardRater {
 static BoardRater raters[] = //staticness prevents these raters from getting instantiated over and over and over and over again... this'll save garbage collection time.
 {
   new ConsecHorzHoles(),
   new HeightAvg(),
   new HeightMax(),
   new HeightMinMax(),
   new HeightVar(),
   new Lame(),
   new SimpleHoles(),
   new ThreeVariance(),
   new Trough(),
   new WeightedHoles()
 };
 
 double[] coefficients;
 
 public FinalRater(double[] c) {
   if(c.length!=FinalRater.raters.length) throw new Exception("Make sure that the array passed into the FinalRater has the correct number of coefficients!");
   this.coefficients = c;
 }
 
 double rate(Board board) {
   double score = 0;
   for (int x=0; x<raters.length; x++)
     score += FinalRater.raters[x].rateBoard(board)*this.coefficients[x];
   return score;
 }
 
 double rate(Board board, double[] coefficients) {
   this.coefficients = coefficients;
   return this.rate(board);
 }
}
