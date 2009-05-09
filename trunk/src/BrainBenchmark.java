import java.util.Random;

/**
 * No-frills brain benchmark
 *
 */
public class BrainBenchmark {
	/* Add your yummy brains here NOM NOM NOM */
	Brain brainz[] = {
			new LameBrain(),
			new LameBrain2()
		};
	
	BrainBenchmark() {
	}
	
	String[][] computeResults() {
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BrainBenchmark bb = new BrainBenchmark();
		for (String[] result : bb.computeResults()) {
			for (String col : result) {
				System.out.print(col + " ");
			}
			System.out.println();
		}
	}

}
