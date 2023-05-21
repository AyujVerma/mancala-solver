import java.util.Scanner;

/**
 * A class that models the Mancala board and all of the opponenet's functionality.
 */
public class Board 
{
	private int[] board; // Game board
	private boolean myTurn; // Determines whose turn it is
	private Scanner s = new Scanner(System.in);
	private int gamemode; // 0 for avalanche and 1 for capture
	private int mancala = 6; // Your mancala's index in the board array (in the middle of the board)
	private int totalBowls = 14; // Number of  bowls in a Mancala board
	
	/**
     * Create a Board where bead values are determined by input.
     */
	public Board()
	{
		board = new int[totalBowls];
		myTurn = true;
		System.out.println("Welcome to Mancala Solver! Enter 0 for Avalanche or 1 for Capture.");
		gamemode = Integer.parseInt(s.next());
		while(gamemode != 0 && gamemode != 1)
		{
			System.out.println("Please enter a valid gamemode. Enter 0 for Avalanche or 1 for Capture.");
			gamemode = Integer.parseInt(s.next());
		}
		System.out.println("Enter the bead counts going counter clockwise from the top left bowl.");
		for(int i = 0; i < totalBowls; i++)
		{
			board[i] = Integer.parseInt(s.next());
		}
	}
	
	/**
     * Create a Board where bead values are determined by the values in tempBoard.
     * @param tempBoard: array of bead values we are copying.
     * @param mode: determines which gamemode we are in.
     */
	public Board(int[] tempBoard, int mode)
	{
		board = tempBoard;
		myTurn = true;
		gamemode = mode;
	}
	
	/**
     * The user is making a single move on the mancala board.
     * @param  bowl: determines which index we are starting from.
     * @return the index of the last bead dropped
     */
	public int move(int bowl)
	{
		int beads = board[bowl];
		board[bowl] = 0;
		int currentPosition = bowl + 1;
		for(int i = 0; i < beads; i++)
		{
			if(myTurn && currentPosition == totalBowls - 1)
			{
				currentPosition = 0;
			}
			else if(!myTurn && currentPosition == mancala)
			{
				currentPosition = currentPosition + 1;
			}
			if(currentPosition == totalBowls)
			{
				currentPosition = 0;
			}
			board[currentPosition] = board[currentPosition] + 1;
			currentPosition = i + 1 == beads ? currentPosition : currentPosition + 1;
		}
		return currentPosition;
	}
	
	/**
     * Reverses the user's move on the mancala board. 
     * Used for recursion purposes.
     * @param  currentPosition: determines which index we are starting from.
     * @parma beads: determines the number of bowls to go backwards (clockwise).
     * @return the index of where the original move started from.
     */
	public int restore(int currentPosition, int beads)
	{
		for(int i = beads; i > 0; i--)
		{
			if(currentPosition == -1)
			{
				currentPosition = totalBowls - 2;
			}
			board[currentPosition] = board[currentPosition] - 1;
			currentPosition = currentPosition - 1;
		}
		if(currentPosition == -1)
		{
			currentPosition = totalBowls - 2;
		}
		board[currentPosition] = beads;
		return currentPosition;
	}
	
	/**
     * For the capture mode only, restores the board to how it was before the capture.
     * The user and the opponenet's beads go back to their bowls and the user's score is decreased.
     * Used for recursion purposes.
     * @param  currentPosition: determines which index we are starting from.
     * @parma beads: determines the number of bowls to go backwards (clockwise).
     * @param capture: determines if a capture was made.
     * @param yourBeads: the user's beads to go back.
     * @param opponentBeads: the opponenet's beads to go back.
     */
	public void captureRestore(int currentPosition, int beads, boolean capture, int yourBeads, int opponentBeads)
	{
		if(capture)
		{
			board[currentPosition] = yourBeads;
			board[totalBowls - currentPosition] = opponentBeads;
			board[mancala] -= (yourBeads + opponentBeads);
		}
	}
	
	/**
     * The opponent is making a move on the mancala board.
     * Takes care of avalanches, captures, and extra turns.
     * @return the index of the last bead dropped
     */
	public int opponentMove()
	{
		if(endGame())
		{
			endGameMessage();
		}
		System.out.println("What move did your oppenent make? 7 is the bottom left corner and 12 is"
						 + " the upper right bowl.");
		int bowl = Integer.parseInt(s.next());
		while(bowl <= mancala && bowl >= totalBowls - 1)
		{
			System.out.println("Please enter a valid bowl for your opponenet.");
			bowl = Integer.parseInt(s.next());
		}
		int currentPosition = move(bowl);
		while(avalanche(currentPosition))
		{
			currentPosition = move(currentPosition);
		}
		if(capture(currentPosition))
		{
			board[totalBowls - 1] += captureBeads(currentPosition);
		}
		if(extraTurn(currentPosition))
		{
			System.out.println("The board now looks like this:\n" + this);
			currentPosition = opponentMove();
		}
		return currentPosition;
	}
	
	/**
     * Returns true if the game is over or false if it is not.
     * The game is over if one person is unable to make any moves on their turn (their side has 0 beads).
     * @return true if the game ended.
     */
	public boolean endGame()
	{
		if(myTurn)
		{
			for(int i = 0; i < mancala; i++)
			{
				if(board[i] != 0)
				{
					return false;
				}
			}
		}
		else
		{
			for(int i = mancala + 1; i < totalBowls - 1; i++)
			{
				if(board[i] != 0)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/**
     * Returns true if the game is over or false if it is not.
     * The game is over if one person is unable to make any moves on their turn (their side has 0 beads).
     * The program is terminated once the game ends.
     * @return true if the game ended.
     */
	public void endGameMessage()
	{
		int score = getScore();
		int opponentScore = getOpponentScore();
		if(score > opponentScore)
		{
			System.out.println("The game has ended! You have won with a score of " 
							  + score + " to " + opponentScore + ".");
		}
		else if(score == opponentScore)
		{
			System.out.println("The game has ended! You have tied with a score of " 
						      + score + " to " + opponentScore + ".");
		}
		else
		{
			System.out.println("The game has ended! You have lost with a score of " 
						      + score + " to " + opponentScore + ".");
		}
		s.close();
		System.exit(0);
	}
	
	/**
     * @return the user's mancala.
     */
	public int getScore()
	{
		return board[mancala];
	}
	
	/**
     * @return the opponent's mancala.
     */
	public int getOpponentScore()
	{
		return board[totalBowls - 1];
	}
	
	/**
	 * @param bowl: the index we are looking at in board.
     * @return the beads at this bowl.
     */
	public int getBeads(int bowl)
	{
		return board[bowl];
	}
	
	/**
	 * @param bowl: the index we are looking at in board.
	 * @param beads: the beads we want to put in bowl.
     * @return the beads at this bowl.
     */
	public void setBeads(int bowl, int beads)
	{
		board[bowl] = beads;
	}
	
	/**
     * @return the gamemode.
     */
	public int getGamemode()
	{
		return gamemode;
	}
	
	/**
     * Copy the values of board into copy.
     * @param copy: the array we want to copy the bead values into.
     */
	public void copyBoard(int[] copy)
	{
		for(int i = 0; i < totalBowls; i++)
		{
			copy[i] = board[i];
		}
	}
	
	/**
     * Return true if the person gets an extra turn and false if they don't.
     * The user gets an extra turn if the last bead in their move goes into their mancala.
     * @param currentPosition: the index we are looking at.
     * @return true if the user gets to choose another bowl.
     */
	public boolean extraTurn(int currentPosition)
	{
		if(myTurn)
		{
			return currentPosition == mancala;
		}
		else
		{
			return currentPosition == totalBowls - 1;
		}
	}
	
	/**
     * Return true if an avalanche occurs and false otherwise.
     * An avalanche happens if the last bead in a move drops in a non-empty hole.
     * @param currentPosition: the index we are looking at.
     * @return true if an avalanche happens.
     */
	public boolean avalanche(int currentPosition)
	{
		return gamemode == 0 && currentPosition != mancala && currentPosition != totalBowls - 1 && 
			   board[currentPosition] > 1;
	}
	
	/**
     * Return true if a capture happens and false otherwise.
     * A capture happens when the last bead drop in an empty hole on your side. If the opponent has
     * beads on the adjacent hole, then the user captures all beads.
     * @param currentPosition: the index we are looking at.
     * @return true if a capture happens.
     */
	public boolean capture(int currentPosition)
	{
		if(myTurn)
		{
			return gamemode == 1 && currentPosition != mancala && currentPosition != totalBowls - 1 && 
				   board[currentPosition] == 1 && board[totalBowls - 2 - currentPosition] != 0 && 
				   currentPosition < mancala;
		}
		else
		{
			return gamemode == 1 && currentPosition != mancala && currentPosition != totalBowls - 1 && 
				   board[currentPosition] == 1 && board[totalBowls - 2 - currentPosition] != 0 &&
				   currentPosition > mancala;   
		}
	}
	
	/**
     * Returns the number of beads captured.
     * Removes the beads from the user and the opponent's sides.
     * @param currentPosition: the index we are looking at.
     * @return the beads captured.
     */
	public int captureBeads(int currentPosition)
	{
		int capture = board[currentPosition] + board[totalBowls - 2 - currentPosition];
		board[currentPosition] = 0;
		board[totalBowls - 2 - currentPosition] = 0;
		return capture;
	}
	
	/**
     * Sets myTurn to its opposite.
     */
	public void nextTurn()
	{
		myTurn = !myTurn;
	}
	
	/**
     * @return myTurn to determine whose turn it is.
     */
	public boolean getTurn()
	{
		return myTurn;
	}
	
	@Override
	/**
     * Displays the board like an actual Mancala board.
     */
	public String toString()
	{
		String str = " " + getOpponentScore() + " \n";
		for(int i = 0; i < mancala; i++)
		{
			str += getBeads(i) + " " + getBeads(totalBowls - 2 - i) + "\n";
		}
		str += " " + getScore() + " ";
		return str;
	} 
}