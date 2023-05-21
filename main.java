
public class main 
{
	public static void main(String[] args)
	{
		Board board = new Board();
		Solver solver = new Solver(board);
		solver.play();
	}
}
