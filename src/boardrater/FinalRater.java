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
 public static BoardRater raters[] = //staticness prevents these raters from getting instantiated over and over and over and over again... this'll save garbage collection time.
 {
    new ConsecHorzHoles(),
    new HeightAvg(),
    new HeightMax(),
    new HeightMinMax(),
    new HeightVar(),
    new HeightStdDev(),
    new SimpleHoles(),
    new ThreeVariance(),
    new Trough(),
    new WeightedHoles(),
    new RowsWithHolesInMostHoledColumn(),
    new AverageSquaredTroughHeight(),
    new BlocksAboveHoles()
 };
 
 double[] coefficients = {1,1,1,1,1,1,1,1,1,1,1,1,1}; //default weights. replace these with the ones obtained from the genetic algorithm.
 
 public FinalRater() {
   //if this constructor is used, then the overloaded rate(board,coeffs) method will have to be used in order to supply a list of weights
 }
 
 public FinalRater(double[] c) {
   if(c.length!=FinalRater.raters.length) {
     System.out.println("Make sure that the array passed into the FinalRater has the correct number of coefficients! Using DEFAULT COEFFICIENTS instead!");
     return;
   }
   this.coefficients = c;
 }
 
 double rate(Board board) {
   double score = 0;
   for (int x=0; x<raters.length; x++)
     score += FinalRater.raters[x].rate(board)*this.coefficients[x];
   return score;
 }
 
 double rate(Board board, double[] coefficients) {
   this.coefficients = coefficients;
   return this.rate(board);
 }
}
