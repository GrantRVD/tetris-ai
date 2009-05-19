import java.util.*;
import java.io.*;
import java.lang.*;

public class Solver {
  
  int       //PARAMETERS!
              GEN_SIZE = 300,               //the number of kids in each generation.
              PASS_RATE = 3,                //the percentage of kids of each new generation that get to be passed on unmodified to the next. These are the cream of the crop!
              SEX_RATE = 30,                //the percentage of kids of each new generation that are children of two parents. all other kids are mutations of single parents or new random kids.
              TINYMUT_RATE = 2,             //the percentage of top kids to be inserted into the new generation with only a constant number of bits flipped.
              TINYMUT_MAXFLIPQUANTITY = 2,  //the maximum number of bits to flip in a tiny mutation.
              MUTATION_RATE = 40,           //the percentage of kids after the mated ones who are mutated and inserted into the next generation. All remaining kids are created brand new, randomly.
              MUTATION_STRENGTH = 50,       //the percentage of bits that change with each non-mating mutation.
              SEX_MUTATION_PROBABILITY = 30,//the percentage of time that the offspring of two parents will itself mutate
              SEX_MUTATION_STRENGTH = 30    //the percentage of bits that change with each post-mating mutation.
  ;
  
  int gmax, nvars, nclauses, clausesize;    //gmax is maximum number of generations. nvars is number of variables. nclauses is number of clauses. clausesize is maximum clause length.
  int[][] kb;                               //this is the knowledge base array. rows are clauses, columns are the variables within them.
  boolean[][] gen, nextg;                   //these are the current and next generation. rows are kids, columns are bits within the kids.
  int[] fitnesses;                          //this is a list of ints to be used to hold the calculated fitnesses of each kid within a generation.
  boolean T = true, F = false;              //handy.
  String filename;                          //this is the input file name.
  Random randGenner = new Random();         //handy.
  boolean[] preknownSolution;               //if the solution is supplied in the second comment of the file, this is it. The program will test against this in order to verify that the given solution IS IN FACT A SOLUTION. This is a sanity check to verify correct program operation, because problems with low R (clauses/variables) ratios seem to have many solutions.
  long lastFeedbackTime;                    //this is to allow the program to give some feedback when it runs for a long time.
  long startTime = 0;                       //this is to allow the program to give some feedback when it runs for a long time.
  int feedbackCount = 0;                    //this is to allow the program to give some feedback when it runs for a long time.
  int gencount;                             //this is the running total number of generations.
  
  public static void main(String[] args) {
    Solver it = new Solver(args[0], Integer.parseInt(args[1]));
    it.solve();
  }
  
  public Solver(String filename, int gmax) {
    this.filename = filename;
    this.gmax = gmax;
    fitnesses = new int[GEN_SIZE];
  }
  
  void solve() {
    p("");
    p("");
    p("");
    p("");
    p("");
    lastFeedbackTime = System.currentTimeMillis();
    if(gmax>0)      System.out.println("Now running a maximum of "+gmax+" generations, using "+filename+" as input.");
    else if(gmax>0) System.out.println("Now running a with no cap on generation count, using "+filename+" as input.");
    if(!readInput()) return;
    
    makeFirstGen();                                         //INSTANTIATE THE FIRST GENERATION WITH RANDOM BITS.
    
    p("\n--------------------------------------");
    p("    Processing now.");
    
    gencount = 0;
    boolean[] solution = null;                              //get us a pointer we can use to hold a possible solution...
    startTime = System.currentTimeMillis();                 //start the timer!
    while((gmax==-1 || gencount<gmax) && (solution = nextGen())==null) {gencount++;}  //this is an infinite loop if there is no solution and gmax = -1.
    if(gencount==gmax && solution==null) p();
    p("--------------------------------------\n");
    if(solution==null) {                                    //if we didn't find a solution, yell!
      generateFitnesses(); quicksortKids();
      p("NO SOLUTION FOUND WITHIN "+gmax+" GENERATIONS.");
      System.out.print(
        "Clause Satisfaction:  C = "); pa(getSatisfiedList(gen[0]));
      p("Fitness:              F = "+fitnesses[0]);
      p("Generation Count:     G = "+gencount);
      p("Solution:             X   has no value.");
    }
    if(solution!=null) {                                    //if we DID find a solution, yell louder! I guess it has no choice but to yell.
      p("Yeah. A solution is FOUND."); 
      System.out.print(
        "Clause Satisfaction:  C = "); pa(getSatisfiedList(gen[0]));
      p("Fitness:              F = 0. A BIG, BEAUTIFUL, ZERO!");
      p("Generation Count:     G = "+gencount);
      System.out.print("Solution:             X = "); pa(solution);
    }
    p("Compute Time was "+(System.currentTimeMillis()-startTime)+"ms.");
    p("--------------------------------------");
  }
  
  boolean readInput() {                                     //Reads the input file into the knowledge base, and returns true when successful. Returns false if there are evil shenanigans!
    FileReader reader;
    Scanner scan;
    try {
      reader = new FileReader(filename);
      scan = new Scanner(reader);
      if(!scan.hasNextLine()) return false;
      String firstLine = scan.nextLine();                   //grab the first line.
      String preknownSolutionString = null;
      while(firstLine.charAt(0)=='#') {                     //ignore comments, except one that has a solution in it (for sanity checking; it should always display that the given solution works.)
        firstLine = scan.nextLine();
        if(firstLine.indexOf("solution = ")!=-1)
          preknownSolutionString = firstLine;
      }
      Scanner firstLineScanner = new Scanner(firstLine);    //make a whole new scanner for the first line, because we already have it.
      nvars = firstLineScanner.nextInt();                   //Store the three values on the first line.
      nclauses = firstLineScanner.nextInt();
      clausesize = firstLineScanner.nextInt();
      p("There are "+nvars+" variables and "+nclauses+" clauses of maximum size "+clausesize+".");
      kb = new int[nclauses][clausesize];                   //now that we know the dimensions of the problem, initialize the knowledge base.
      gen       = new boolean[GEN_SIZE][nvars];             //also init the generation array...
      nextg     = new boolean[GEN_SIZE][nvars];             //and the next one.
      for(int i=0; i<nclauses; i++) {                       //now, in this loop, go through all the lines and populate the knowledge base.
        String line = scan.nextLine();                      //go one line at a time, using the whole line.
        Scanner thisLineScanner = new Scanner(line);        //scan this line, as it has an unknown number of ints. (There may not be the maximum number of variables in each clause line.)
        for(int j=0; j<clausesize; j++) {                   //for each column in the knowledge base...
          if(thisLineScanner.hasNextInt())                  //put the variable number in that column if the current line still has variables...
            kb[i][j] = thisLineScanner.nextInt();
          else                                              //or, if we're out of variables, just put a zero, which will refer to a don't-care proposition, which is always satisfied.
            kb[i][j] = 0;                                   //this will allow us to handle clauses of different lengths, as long as they are all within the maximum length.
        }
      }
      if(preknownSolutionString!=null) {
        int bitspot = preknownSolutionString.indexOf("solution = ")+11;
        preknownSolution = new boolean[nvars];
        for(int i=0; bitspot<preknownSolutionString.length() && i<nvars; i++) {
          preknownSolution[i] = preknownSolutionString.charAt(bitspot)=='1'?true:false;
          bitspot++;
        }
        p("--------------------------------------");
        System.out.print("By the way, a solution was given in a comment in the input: ");
        pa(preknownSolution);
        p("As a sanity check, I tested it, and it solution given does indeed "+(fitness(preknownSolution)==0?"":"NOT ")+"satisfy the constraints.");
      }
    } catch(Exception e) {
      p("Something bad happened with the scanner. Because the author of this programmer has never made a mistake or experienced any bugs in his entire life, you probably had a bad file name or bad input. Fix it! Then I will play nice! :-)");
      return false;
    }
    return true;
  }
  
  void makeFirstGen() {                                     //populates the first generation with random strings of bits.
    for(int i=0; i<GEN_SIZE*nvars; i++)
      gen[i/nvars][i%nvars] = randGenner.nextBoolean();
  }
  
  
  
  
  
  
  
  
  
  boolean[] nextGen() {                                     //computes the next generation, starting by getting the fitnesses of the current generation and returning if it sees a solution.
    generateFitnesses();                                    //figure out what all the kids' fitnesses are.
    quicksortKids();                                        //now sort the kids based on their fitnesses.
    
    long now = System.currentTimeMillis();                   //Before we do anything else, make sure we don't leave the user hanging.
    boolean canPrint = false;
    if(now-lastFeedbackTime>200) {
      feedbackCount++;
      if(feedbackCount%20==0) {
        System.out.println("\n    Progress: At Generation: "+gencount+".");    //let people know that we're still working for them!
        System.out.print("              Top Fitnesses: ");    //let people know that we're still working for them!
        for(int i=0; i<10 && i<GEN_SIZE; i++) System.out.print(fitnesses[i]+" ");
        p();
      }
      if(feedbackCount==2) System.out.print("...");      
      if(feedbackCount>2) System.out.print(".");
      // p("\n  ...best fitness attained so far: "+fitnesses[0]);//let user know how far we've come along!
      canPrint = true;
      lastFeedbackTime = now;
    }
    
    if(fitnesses[0]==0) {                                   //if the first kid is SUPER DE-DUPER AWESOME and has fitness==0...
      if(feedbackCount>=2) p();                             //new line for formatting
      return gen[0];                                        //then return him, because he wins!
    }
    
    int numberAdded = 0;                                    //we're going to overwrite the contents of nextg one by one.
    int numberUsed = 0;                                     //we're going to use up the contents of gen one by one.
    
    
    
    int tinyMutCount = TINYMUT_RATE * GEN_SIZE / 100;       //the cream cream of the crop that have a small constant number of random bits flipped...
    for(int i=0; i<tinyMutCount; i++) {                     //if a kid is so good that he's got lots of potential, flip a tiny number of its bits.
      nextg[numberAdded] = tinyMutationOf(gen[i]);
      numberAdded++;
    }
    
    int passthruCount = PASS_RATE * GEN_SIZE / 100;         //the cream of the crop get to go on with their children.
    for(int i=0; i<passthruCount; i++) {                    //if a kid is so good that he's just too crazy to get rid of, pass him unmodified into the next generation so we don't ever go backwards.
      nextg[numberAdded] = gen[i];
      numberAdded++;
    }
    
    int luckyCount = ((SEX_RATE * GEN_SIZE) / 100)*2;       //this is the number of folks that get to mate. they're the best ones. Only the best ones get to mate.
    for(int i=0; i<luckyCount && numberUsed<gen.length-1; i+=2) {//for all of the folks that get to mate...
      boolean[] luckyOne = gen[numberUsed];                      //figure out who they are...
      boolean[] luckyTwo = gen[numberUsed+1];
      nextg[numberAdded] = offspringOf(luckyOne,luckyTwo);  //mate them and store them in the next gen's list.
      numberAdded++;
      numberUsed+=2;
    }
    
    int possibleDupCount = passthruCount+tinyMutCount;      //this block goes through the beginning of the next generation and replaces dupes with random bits, to prevent lack of variation in the most fit folks of the population.
    for(int i=0; i<possibleDupCount+3 && i<GEN_SIZE; i++) {      //this does an O(n^2) traversal through the beginning of the next generation's list and removes duplicates.
      for(int j=i+1; j<possibleDupCount+3 && j<GEN_SIZE; j++) {  //this duplicate removal allows the higher-fitness children interact more with each other, rather than with copies of themselves.
        if(equals(nextg[i],nextg[j])) {                          //duplicates have one of the copies replaced with a random bit string.
          nextg[j] = newRandomKid();
        }
      }
    }
    
    int mutatedCount = MUTATION_RATE * GEN_SIZE / 100;      //figure out how many get to mutate.
    for(int i=0; i<mutatedCount && numberUsed<gen.length; i++) {//for all the folks who just mutate...
      boolean[] unluckyOne = gen[numberUsed];                        //grab them...
      nextg[numberAdded] = mutationOf(unluckyOne);          //mutate them, and store them.
      numberAdded++;
      numberUsed++;
    }    
    
    int numberDerived = numberAdded;
    for(int i=numberAdded; i<gen.length; i++) {           //fill up the empty spots with randomly generated new children. There will be empty spots because, for every two that mated, only one resulted.
      nextg[numberAdded] = newRandomKid();
      numberAdded++;
    }    
    
    for(int i=0; i<GEN_SIZE; i++) {                         //copy the new generation into the old generation //TODO: OPTIMIZE THIS; YOU DON'T NEED TWO ARRAYS! YOU CAN DO THIS IN PLACE!
      gen[i] = nextg[i];
    }
    
    return null;
  }
  
  void generateFitnesses() {                                //calculate the fitnesses of every single kid, and store in the fitnesses array.
    for(int i=0; i<GEN_SIZE; i++)
      fitnesses[i] = fitness(gen[i]);
  }
  
  void quicksortKids() {                                    //sort the kids based on their fitnesses.
    quicksortAllThemKids(0, GEN_SIZE-1);
  }
  
  
  
  
  boolean[] offspringOf(boolean[] mom, boolean[] dad) {     //returns the child of two folks that are mated in the nextGen function.
    boolean willMutateAlso = randGenner.nextInt(100)<SEX_MUTATION_PROBABILITY;      //will mutate only with this much chance...
    boolean[] kid = new boolean[nvars];
    for(int i=0; i<nvars; i++) {                                                    //for each bit in the parents...
      kid[i] = randGenner.nextBoolean()?mom[i]:dad[i];                              //randomly pick one for the kid's bit.
      if(willMutateAlso && randGenner.nextInt(100)<SEX_MUTATION_STRENGTH)           //also, hey, if we're mutating, then we have a chance to flip the bit!
        kid[i] = !mom[i];
    }
    return kid;
  }

  boolean[] tinyMutationOf(boolean[] eliteKid) {                   //returns a mutation of a poor guy who gets to die all alone.
    int numberFlips = TINYMUT_MAXFLIPQUANTITY==1?1:randGenner.nextInt(TINYMUT_MAXFLIPQUANTITY-1)+1;
    boolean[] ret = new boolean[nvars];
    for(int i=0; i<nvars; i++)
      ret[i] = eliteKid[i];
    for(int i=0; i<numberFlips; i++) {
      int index = randGenner.nextInt(eliteKid.length);
      ret[index] = !ret[index];
    }
    return ret;
  }
  
  boolean[] mutationOf(boolean[] loner) {                   //returns a mutation of a poor guy who gets to die all alone.
    boolean[] ret = new boolean[nvars];
    for(int i=0; i<nvars; i++)                              //for each bit... pick whether to have it come in flipped or not.
      ret[i] = (randGenner.nextInt(100)<MUTATION_STRENGTH)?!loner[i]:loner[i];
    return ret;
  }
  
  boolean[] newRandomKid() {                            //Returns a brand new random string of bits... nvars bits long.
    boolean[] ret = new boolean[nvars];
    for(int i=0; i<nvars; i++) ret[i] = randGenner.nextBoolean();
    return ret;
  }
  
  boolean equals(boolean[] one, boolean[] two) {                             //returns whether two kids are equal...
    for(int i=0; i<nvars; i++) if(one[i]!=two[i]) return false;
    return true;
  }
  
  
  
  int fitness(boolean[] kid) {                          //returns the fitness of a given string of bits. The fitness is the number of clauses not yet satisfied, so lower fitness is better.
    int fitness = 0;                                    //we need to keep track of a running fitness count.
    for(int i=0; i<kb.length; i++) if(!satisfies(kid,kb[i])) {
      fitness++; //for each clause in the kb, check whether it is satsified by the kid and, if not, inc the fitness count.
      // System.out.print("NOT SATISFIED: "); pa(kb[i]);
    }
    return fitness;                                     //return it!
  }
  boolean[] getSatisfiedList(boolean[] kid) {           //returns the fitness of a given string of bits. The fitness is the number of clauses not yet satisfied, so lower fitness is better.
    boolean[] satlist = new boolean[kb.length];
    for(int i=0; i<kb.length; i++)
      satlist[i] = satisfies(kid,kb[i]); //for each clause in the kb, check whether it is satsified by the kid and, if not, inc the fitness count.
    return satlist;                                     //return it!
  }
  
  boolean satisfies(boolean[] kid, int[] clause) {      //returns whether a candidate string of bits solves a given clause. Also allows for the same variable to show up more than once in the clause.
    for(int i=0; i<kid.length; i++) {                   //for each bit in the string...
      for(int j=0; j<clause.length; j++) {              //and for each variable in the clause...
        int varNumber = Math.abs(clause[j]);            //get the variable number we're working with...
        if(varNumber-1==i) {                            //find out if the variable is pertinent to the position we're at in the bit string...
          boolean positive = varNumber==clause[j];      //if so, then find out if it's negated or not...
          if(positive==kid[i]) return true;             //if the variable is positive and the bit is high, or if the variable is negative and the bit is low, return true, because the string satisfies the clause by meeting one of its propositions.
        }
      }
    }
    return false;
  }
  
  
  
  
  
  
  
  void p() {              //This is just handy.
    System.out.println();
  }
  void p(Object o) {              //This is just handy.
    System.out.println(o);
  }
  void pa(int[] x) {              //This is just handy.
    System.out.print('[');
    for(int i=0; i<x.length; i++) {
      System.out.print(x[i]);
      if(i<x.length-1) System.out.print(", ");
    }
    p("]");
  }
  void pa(int[][] x) {              //This is just handy.
    System.out.print("[");
    for(int i=0; i<x.length; i++) {
      pa(x[i]);
      if(i<x.length-1) System.out.print(", ");
    }
    p("]");
  }
  void pa(boolean[] x) {              //This is just handy.
    for(int i=0; i<x.length; i++) {
      System.out.print(x[i]?"1":"0");
    }
    p();
  }
  void pa(boolean[][] x) {              //This is just handy.
    System.out.print("[");
    for(int i=0; i<x.length; i++) {
      pa(x[i]);
      if(i<x.length-1) System.out.print(", ");
    }
    p("]");
  }
  
  void quicksortAllThemKids(int startlo, int starthi) {   //This is a quicksort that swaps the children in the gen array right along with the fitnesses in the fitnesses array. It just keeps them aligned. I hate using customized data-holding objects when I don't feel like I need to!
    if(startlo>=starthi) return;              //get out if the current list to sort is length zero or less...
    int lo = startlo;
    int hi = starthi;
    if(hi-1==lo) {                            //handle the case where we swap only a two-element list
      if(fitnesses[lo]>fitnesses[hi]) {
        int temp = fitnesses[lo];             //swap!
        fitnesses[lo] = fitnesses[hi];
        fitnesses[hi] = temp;
        boolean[] tempkid = gen[lo];          //also swap the kids themselves... not only the fitnesses.
        gen[lo] = gen[hi];
        gen[hi] = tempkid;
      }
      return;                                 //get out if we just finished swapping the only two things in the list.
    }
    int piv = fitnesses[(lo+hi)/2];           //choose pivot
    fitnesses[(lo+hi)/2] = fitnesses[hi];     //...and swap it away for now
    fitnesses[hi] = piv;
    boolean[] Kidpivot = gen[(lo+hi)/2];      //now swap the kids to stay aligned!
    gen[(lo+hi)/2] = gen[hi];
    gen[hi] = Kidpivot;
    while(lo<hi) {
      while(fitnesses[lo]<=piv && lo<hi)      //go up from lo till bigger is found... 
        lo++;
      while(piv<=fitnesses[hi] && lo<hi)      //go down from hi till smaller is found...
        hi--;
      if(lo<hi) {                             //swap lo and hi if out of order
        int temp = fitnesses[lo];
        fitnesses[lo] = fitnesses[hi];
        fitnesses[hi] = temp;
        boolean[] tempkid = gen[lo];          //now swap the kids!
        gen[lo] = gen[hi];
        gen[hi] = tempkid;
      }
    }
    fitnesses[starthi] = fitnesses[hi];       //put pivot back in the middle...
    fitnesses[hi] = piv;
    gen[starthi] = gen[hi];
    gen[hi] = Kidpivot;
    quicksortAllThemKids(startlo,lo-1);       //Recurse now that we're sorted around the pivot.
    quicksortAllThemKids(hi+1,starthi);
  }
}