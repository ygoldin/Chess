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
	 * @return a set of all of the moves the piece can make
	 */
	public Set<PieceMove> legalMoves(ChessBoard board);
}
