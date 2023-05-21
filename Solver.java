import java.util.ArrayList;

public class Solver 
{
	private Board board;
	private int[] bestBoard;
	private int[] stats;
	private int bestScore;
	private ArrayList<Integer> moves;
	private ArrayList<Integer> bestMoves;
	private int bestPosition;
	private int mancala = 6; // Your mancala's index in the board array (in the middle of the board)
	
	/*
	 * Creates the board using an array of size 13 (12 bowls and your Mancala) where 0 is the top 
	 * left corner going counterclockwise.
	 */
	public Solver(Board b)
	{
		board = b;
		bestBoard = new int[14];
		stats = new int[2];
		bestScore = board.getScore();
		moves = new ArrayList<Integer>();
		bestMoves = new ArrayList<Integer>();
		bestPosition = -1;
	}
	
	public void play()
	{
		while(!board.endGame())
		{
			solve();
			displayMessage();
			if(board.endGame())
			{
				board.endGameMessage();
			}
			board.nextTurn();
			board.opponentMove();
			displayMessage();
			if(board.endGame())
			{
				board.endGameMessage();
			}
			board.nextTurn();
		}
	}
	
	public int solve()
	{
		int score = board.getScore();
		for(int i = 0; i < mancala; i++)
		{
			moves.add(i);
			if(board.getGamemode() == 0)
			{
				score = solveAvalancheHelper(i, bestBoard, bestScore);
			}
			else
			{
				score = solveCaptureHelper(i, bestBoard, bestScore);
			}
			if(score > bestScore || (score == bestScore && stats[1] > bestPosition))
			{
				bestScore = score;
				bestMoves.clear();
				for(int move : moves)
				{
					bestMoves.add(move);
				}
				bestPosition = stats[1];
			}
			moves.remove(moves.size() - 1);
		}
		if(bestMoves.size() > 0)
		{
			stats[0] = bestMoves.get(0); //bestBowl
		}
		return bestScore;
	}
	
	private int solveAvalancheHelper(int bowl, int[] bestBoard, int currentBestScore)
	{
		int beads = board.getBeads(bowl);
		//Base Case: Bowl has 0 beads
		if(beads == 0)
		{
			stats[1] = -1;
			return board.getScore();
		}
		else
		{
			int currentPosition = board.move(bowl);
			int score = board.getScore();
			if(score > currentBestScore || (score == currentBestScore && currentPosition > stats[1]))
			{
				currentBestScore = score;
				board.copyBoard(bestBoard);
				stats[1] = currentPosition;
			}
			
			if(board.extraTurn(currentPosition))
			{
				score = solve();
			}
			else if(board.avalanche(currentPosition))
			{
				score = solveAvalancheHelper(currentPosition, bestBoard, currentBestScore);
			}
			
			board.restore(currentPosition, beads);
			return score;
		}
	}
	
	private int solveCaptureHelper(int bowl, int[] bestBoard, int currentBestScore)
	{
		int beads = board.getBeads(bowl);
		//Base Case: Bowl has 0 beads
		if(beads == 0)
		{
			stats[1] = -1;
			return board.getScore();
		}
		else
		{
			int currentPosition = board.move(bowl);
			int score = board.getScore();
			int yourBeads = board.getBeads(currentPosition);
			int opponentBeads = board.getBeads(bestBoard.length - 2 - currentPosition);
			boolean capture = false;
			if(board.capture(currentPosition))
			{
				score = board.captureBeads(currentPosition) + board.getScore();
				board.setBeads(mancala, score);
				capture = true;
			}
			if(score > currentBestScore || (score == currentBestScore && currentPosition > stats[1]))
			{
				currentBestScore = score;
				board.copyBoard(bestBoard);
				stats[1] = currentPosition;
			}
			if(board.extraTurn(currentPosition))
			{
				score = solve();
			}

			if(capture)
			{
				board.captureRestore(currentPosition, beads, capture, yourBeads, opponentBeads);
			}
			board.restore(currentPosition, beads);
			return score;
		}
	}
	
	public void displayMessage()
	{
		if(board.getTurn())
		{
			board = new Board(bestBoard, board.getGamemode());
			String message = "Pick bowl " + stats[0] + ", where 0 is the top left corner and 5 is the "
						   + "bottom right bowl. Your score with this move will be " + bestScore + "!\n"
						   + "Pick the following sequence of bowls: " + bestMoves + "\nThe board now "
						   + "looks like this:\n" + board;
			System.out.println(message);
			bestBoard = new int[14];
			stats = new int[2];
			bestMoves.clear();
			bestPosition = 0;
		}
		else
		{
			System.out.println("After the opponenet's move, the board now looks like this:\n" + board);
		}
	}
}
