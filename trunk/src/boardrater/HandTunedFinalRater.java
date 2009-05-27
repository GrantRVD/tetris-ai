package boardrater;

import tetris.Board;

public class HandTunedFinalRater extends FinalRater {
  double[] coefficients = {     //TODO, NOTE, for some reason, these weights didn't override the ones that were in the default coefficients list in FinalRater.java. So, as a quick workaround, I pasted them there. Given how much time is left in the project, this probably doesn't even matter. Just know that changing these weights won't do anything when you run the code.
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
}