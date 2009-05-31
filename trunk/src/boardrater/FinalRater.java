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
 
//these weights are hand-tuned only.
//   public double[] coefficients = {
// /*new ConsecHorzHoles(),*/                0,  
// /*new HeightAvg(),*/                      10,
// /*new HeightMax(),*/                      1,
// /*new HeightMinMax(),*/                   1,
// /*new HeightVar(),*/                      0,
// /*new HeightStdDev(),*/                   5,
// /*new SimpleHoles(),*/                    40,
// /*new ThreeVariance(),*/                  10,
// /*new Trough(),*/                         1,
// /*new WeightedHoles(),*/                  4,
// /*new RowsWithHolesInMostHoledColumn()*/  4,
// /*new AverageSquaredTroughHeight()*/      15,
// /*new BlocksAboveHoles()*/                2
//   }


/***********DEFUALT WEIGHTS. UNCOMMENT THE DESIRED WEIGHTS.**************/

//these are weights from gen. 21 of the genetic algorithm, starting with the hand tuned weights above.
//these weights have scored 1.43 million in a single ply game.
 //public double[] coefficients = {0.07996672957203162, 5.249915291143696, 0.7615980333336664, 1.5793630193033281, -0.05020715891195912, 2.3439280170167276, 28.823943116495848, 12.357422820878064, 1.2165324765507346, 2.7357785144348763, 4.635003883701018, 24.02658382296249, 0.3853758982469925};

//these weights are from gen. 35 of the genetic algorithm, starting with a list of all 1s for weights.
//these weights have scored 1.69 million in a single ply game.
 //public double[] coefficients = {2.49410038938842, 0.9388492572347871, -0.48140765187055645, 0.32990060828351453, 0.5574379956647663, 0.3538547940221663, 1.7495740291407684, 0.8704507143191742, 1.2571443652090235, 0.8965065762788876, 2.2880437169435592, 3.5804223716291204, -0.15808178736719514};
 
//these weights are from gen. 70 of the genetic algorithm, starting with a list of all zeroes for weights.
//these weights have scored 2.5 million in a single ply game. other weights have obtained 2.8 million, but these averaged the highest.
 public double[] coefficients = {0.9686097026795061, 0.1366862371509124, 0.1959640814385032, -0.4157005367058263, 0.4393275614613794, -0.19272725359581952, 0.643303423041282, 0.3048018715760217, 0.5230983454901121, 0.406929661228957, 0.2525305054989866, 1.4247599835416362, 0.0286589312318309};
 
 /***********END DEFAULT WEIGHTS. UNCOMMENT THE DESIRED WEIGHTS.**********/
 
 
 public FinalRater() {
   // System.out.println("new final rater:");
   // String temp;`
   // for(int i=0; i<raters.length; i++) {
   //   System.out.println((temp=""+coefficients[i]).substring(0,temp.length()>=4?temp.length():3)+"\t\t"+raters[i]);
   // }
   // for(int i=0; i<this.coefficients.length; i++)       //UNCOMMENT THIS LOOP TO NEGATE THE WEIGHTS AND SEE HOW BADLY IT KNOWS HOW TO PLAY!
     // this.coefficients[i] = 0-this.coefficients[i];
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
   double[] temp = this.coefficients;
   this.coefficients = coefficients;
   double ret = this.rate(board);
   this.coefficients = temp;
   return ret;
 }
}
