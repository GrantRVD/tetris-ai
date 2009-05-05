// This class is used to generate structured sequences of pieces for testing algorithms
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
public class TestGenerator
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		FileWriter writer = new FileWriter(args[0]);
		
		Random gen = new Random();

		for(int j = 0; j < 1000; j++) // write ten trial sequences
		{
			for(int i = 0; i < 100; i++) // sequences will be 100,000 pieces long
			{
				int next = gen.nextInt(7); // There are 7 pieces in tetris
				switch (next)
				{
				case 0: writer.write("i "); break;
				case 1: writer.write("z "); break;
				case 2: writer.write("s "); break;
				case 3: writer.write("o "); break;
				case 4: writer.write("t "); break;
				case 5: writer.write("l "); break;
				case 6: writer.write("j "); break;
				}
			}
			writer.write("\r\n"); // end this sequence
		}
		writer.close();

	}

}
