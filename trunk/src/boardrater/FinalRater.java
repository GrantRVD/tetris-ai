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
 
  double[] coefficients = {
/*new ConsecHorzHoles(),*/                0,  
/*new HeightAvg(),*/                      10,
/*new HeightMax(),*/                      1,
/*new HeightMinMax(),*/                   1,
/*new HeightVar(),*/                      0,
/*new HeightStdDev(),*/                   5,
/*new SimpleHoles(),*/                    40,
/*new ThreeVariance(),*/                  10,
/*new Trough(),*/                         1,
/*new WeightedHoles(),*/                  4,
/*new RowsWithHolesInMostHoledColumn()*/  4,
/*new AverageSquaredTroughHeight()*/      10,
/*new BlocksAboveHoles()*/                2
  };
   
 public FinalRater() {
   System.out.println("new final rater:");
   String temp;
   for(int i=0; i<raters.length; i++) {
     System.out.println((temp=""+coefficients[i]).substring(0,temp.length()>=4?temp.length():3)+"\t\t"+raters[i]);
   }
 }
 
 public FinalRater(double[] c) {
   if(c.length!=FinalRater.raters.length) {
     System.out.println("Make sure that the array passed into the FinalRater has the correct number of coefficients! Using DEFAULT COEFFICIENTS instead!");
     return;
   }
   this.coefficients = c;
 }
 
 double rate(Board board) {
   double score = 0, temp;
   for (int x=0; x<raters.length; x++) {
     score += (temp=this.coefficients[x])==0?0:temp*FinalRater.raters[x].rate(board);
     // System.out.print(this.coefficients[x]);
   }
   return score;
 }
 
 double rate(Board board, double[] coefficients) {
   this.coefficients = coefficients;
   return this.rate(board);
 }
}
