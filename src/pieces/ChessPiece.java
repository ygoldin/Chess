package pieces;

import java.util.Set;
import gamesetup.*;

/**
 * This interface represents a single piece in a game of chess
 * @author Yael Goldin
 */
public interface ChessPiece {
	
	/**
	 * finds the point value of the piece
	 * 
	 * @return the value
	 */
	public int getValue();
	
	/**
	 * checks if the piece is on the white or black team
	 * 
	 * @return true if white, false if black
	 */
	public boolean isWhite();
	
	/**
	 * finds all of the moves this piece can make
	 * 
	 * @param board The chess board the piece is on
	 * @return a set of all of the moves the piece can make: will be empty if it's not the turn
	 * of this piece's team or no legal moves can be made
	 */
	public Set<PieceMove> legalMoves(ChessBoard board);
	
	/**
	 * checks if the piece has never moved during the game
	 * 
	 * @return true if it has never moved, false otherwise
	 */
	public boolean hasMoved();
	
	/**
	 * marks that the piece has moved
	 */
	public void markMoved();
}
