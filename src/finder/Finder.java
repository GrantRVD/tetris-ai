package finder;
import boardrater.*;
import tetris.*;
import java.util.Random;
import java.util.Date;
//For simplicity, I'm limiting the range of weights to between zero and ten.
//They're all doubles anyway, so it shouldn't matter what the actual upper bound is.
//It just can't be infinitely variable, because there'd be an infinite number of
//children that could lead the same game. (i.e. with proportional weights)

//Kids are lists of doubles (lists of weights).
//Fitnesses are ints, and higher is better.

public class Finder {  
  public static void main(String[] args) {
    Finder f = new Finder();
    f.go();
  }
  
  FinalRater rater = new FinalRater();                    //This is used solely by the fitnessOf method to simulate a game with a given kid. (Each kid is a list of weights, remember.)
  Brain raterUser;                                        //This is used solely by the fitnessOf method to simulate a game with the rater.
  TetrisController controller = new TetrisController();   //This is used solely by the fitnessOf method to simulate a game with the brain.
  int NUM_KIDS   = 200;                   //This is the population size. I picked 200 because it feels like a nice number.
  int numWeights = -1;                    //This is the number of weights, derived straight from the FinalRater class in the boardrater package.
  double[][] pop;                         //This is the actual population - the list of lists of weights.
  double[][] nextPop;
  int[] fitnesses;                        //This is the list of fitnesses obtained after each generation, used to sort.
  Random randy = new Random();            //random generator
  
  public Finder() {
    this.numWeights = FinalRater.raters.length;
    this.pop = new double[this.NUM_KIDS][this.numWeights];
    this.nextPop = new double[this.NUM_KIDS][this.numWeights];
    this.raterUser = new Ply2AnyBrain(this.rater);
  }
  
  void go() {
    for(int i=0; i<NUM_KIDS; i++)
      pop[i] = newRandomKid();
    nextGen();
  }
  
  void nextGen() {                            //This is the method called to produce the next generation of weight list kids.
    int count = 0;                            //It works by starting with the most fit and using appropriate mating/mutation methods to
    for(int i=0; i<NUM_KIDS; i++) {           //gradually fill up the nextPop array with the new children.
      if(i<NUM_KIDS*0.3) {                    //afterwards, it swaps nextPop with pop, gets the fitnesses of the new population, and then sorts.
        nextPop[count] = mateKids(pop[i],pop[i+1]);
        count++;                              //After this method, the population is in the same state it was in before this method was called.
      }
      else if(i>=NUM_KIDS*0.3) {
        nextPop[count] = mutateKid(pop[i]);
        count++;
      }
      else if(i>NUM_KIDS*0.7) {
        nextPop[count] = newRandomKid();
        count++;
      }
    }
    double[][] temp;                          //now that nextPop is full of new children, swap it with pop so that pop becomes the drawing board for the NEXT generation.
    temp = pop;
    pop = nextPop;
    nextPop = temp;
    getFitnesses();                          //figure out what all the fitnesses of the children are.
    quicksortByFitness();                    //sort the new population, to put the most fit children in the front of the list for the next generation.
  }
  
  double[] newRandomKid() {                  //This method generates a new random child. Each child is a list of weights.
    double[] kid = new double[numWeights];
    for(int i=0; i<numWeights; i++)
      kid[i] = randy.nextDouble()*1.0+0.5; //Each new random kid will have all weights between 0.5 and 1.5.
    return kid;
  }
  
  double[] newRandomKidFromBaseline() {      //This method generates a new random child from a baseline list of hand-selected weights.
    double[] baseline = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0}; //todo come up with a good baseline weightlist.
    double[] kid = new double[numWeights];
    for(int i=0; i<numWeights; i++)
      kid[i] = baseline[i]+randy.nextDouble()*0.3-.15; //Each new random kid will have all weights between 0.5 and 1.5.
    return kid;
  }
  
  double[] mutateKid(double[] kid) {      //This slightly tweaks all of the parameters in the kid. DOES NOT CREATE A COPY.
    for(int i=0; i<numWeights; i++)
      kid[i] += randy.nextDouble()*0.3-0.15;  //Add a random number between -0.15 and 0.15 to each value in the kid.
    return kid;
  }
  
  double[] tweakKid(double[] kid) {       //This slightly tweaks ONE of the parameters in the kid. DOES NOT CREATE A COPY.
    int i = randy.nextInt(numWeights);
    kid[i] += randy.nextDouble()*0.5-0.25;  //Add a random number between -1/4 and 1/4 to one random value in the kid.
    return kid;
  }
  
  double[] dupeKid(double[] kid) {        //This does nothing more than create a duplicate of the kid passed to it.
    double[] dupe = new double[numWeights];
    for(int i=0; i<numWeights; i++) dupe[i] = kid[i];
    return dupe;
  }
  
  double[] mateKids(double[] one, double[] two) {   //This mates two kids and produces offspring: the offspring is a new object.
    double[] kid = new double[numWeights];
    for(int i=0; i<numWeights; i++)
      kid[i] = randy.nextBoolean()?one[i]:two[i];
    return kid;
  }
  
  void getFitnesses() {
    for(int i=0; i<NUM_KIDS; i++) {
      fitnesses[i] = fitnessOf(pop[i]);
    }
  }
  
  void quicksortByFitness() {
    quicksortByFitness(0,NUM_KIDS-1);
    double[] temp;
    for(int i=0; i<NUM_KIDS; i++) {
      temp = pop[i];
      pop[i] = pop[NUM_KIDS-1-i];
      pop[NUM_KIDS-1-i] = temp;
    }
  }
  
  void quicksortByFitness(int startlo, int starthi) {   //This is a quicksort that swaps the children in the gen array right along with the fitnesses in the fitnesses array. It just keeps them aligned.
    if(startlo>=starthi) return;              //get out if the current list to sort is length zero or less...
    int lo = startlo;
    int hi = starthi;
    if(hi-1==lo) {                            //handle the case where we swap only a two-element list
      if(fitnesses[lo]>fitnesses[hi]) {
        int temp = fitnesses[lo];             //swap!
        fitnesses[lo] = fitnesses[hi];
        fitnesses[hi] = temp;
        double[] tempkid = pop[lo];          //also swap the kids themselves... not only the fitnesses.
        pop[lo] = pop[hi];
        pop[hi] = tempkid;
      }
      return;                                 //get out if we just finished swapping the only two things in the list.
    }
    int piv = fitnesses[(lo+hi)/2];           //choose pivot
    fitnesses[(lo+hi)/2] = fitnesses[hi];     //...and swap it away for now
    fitnesses[hi] = piv;
    double[] Kidpivot = pop[(lo+hi)/2];      //now swap the kids to stay aligned!
    pop[(lo+hi)/2] = pop[hi];
    pop[hi] = Kidpivot;
    while(lo<hi) {
      while(fitnesses[lo]<=piv && lo<hi)      //go up from lo till bigger is found... 
        lo++;
      while(piv<=fitnesses[hi] && lo<hi)      //go down from hi till smaller is found...
        hi--;
      if(lo<hi) {                             //swap lo and hi if out of order
        int temp = fitnesses[lo];
        fitnesses[lo] = fitnesses[hi];
        fitnesses[hi] = temp;
        double[] tempkid = pop[lo];          //now swap the kids!
        pop[lo] = pop[hi];
        pop[hi] = tempkid;
      }
    }
    fitnesses[starthi] = fitnesses[hi];       //put pivot back in the middle...
    fitnesses[hi] = piv;
    pop[starthi] = pop[hi];
    pop[hi] = Kidpivot;
    quicksortByFitness(startlo,lo-1);       //Recurse now that we're sorted around the pivot.
    quicksortByFitness(hi+1,starthi);
  }
  
  int fitnessOf(double[] kid) {
    //todo make a tetris controller and use it to evaluate a kid in this method.
    //do this by setting the coefficients of the FinalRater to the kid, and giving the
    //FinalRater to the TetrisController so the brain can use it to play the game.
		TetrisController tc = controller;
		
		int seed = 0; //todo perhaps find a better seed? probably doesn't matter.
		
		tc.startGame(seed);

		Date start = new Date();

		while (tc.gameOn) {
			Move move = raterUser.bestMove(new Board(tc.board), tc.currentMove.piece, tc.nextPiece, tc.board.getHeight() - TetrisController.TOP_SPACE);
			while (!tc.currentMove.piece.equals(move.piece)) tc.tick(TetrisController.ROTATE);
			while (tc.currentMove.x != move.x) tc.tick(((tc.currentMove.x < move.x) ? TetrisController.RIGHT : TetrisController.LEFT));
			int current_count = tc.count;
			while ((current_count == tc.count) && tc.gameOn) tc.tick(TetrisController.DOWN);
		}

		return tc.count;
  }
}

//todo make mutations of kids with higher fitnesses change their values by a lesser amount each time.