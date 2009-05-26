package boardrater;

import tetris.Board;

public class HandTunedFinalRater extends FinalRater {
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
}