package pieces;

import java.util.Set;
import gamesetup.*;

/**
 * This interface represents a single piece in a game of chess
 * @author Yael Goldin
 */
public abstract class ChessPiece {
	protected boolean hasMoved;
	
	/**
	 * finds the point value of the piece
	 * 
	 * @return the value
	 */
	public abstract int getValue();
	
	/**
	 * checks if the piece is on the white or black team
	 * 
	 * @return true if white, false if black
	 */
	public abstract boolean isWhite();
	
	/**
	 * finds all of the moves this piece can make
	 * 
	 * @param board The chess board the piece is on
	 * @return a set of all of the moves the piece can make: will be empty if it's not the turn
	 * of this piece's team or no legal moves can be made
	 */
	public abstract Set<PieceMove> legalMoves(ChessBoard board);
	
	/**
	 * checks if the piece has never moved during the game
	 * 
	 * @return true if it has never moved, false otherwise
	 */
	public boolean hasMoved() {
		return hasMoved;
	}
	
	/**
	 * marks that the piece has moved
	 */
	public void markMoved() {
		hasMoved = true;
	}
	
	/**
	 * checks if this piece is the same color as the given one
	 * 
	 * @param other The piece to compare to
	 * @return true if they are on the same team, false otherwise
	 */
	public boolean isSameTeam(ChessPiece other) {
		return isWhite() == other.isWhite();
	}
	
	/**
	 * checks if its the turn of the team this piece belongs to
	 * 
	 * @param board The board the piece is on
	 * @return true if it is this team's turn, false otherwise
	 */
	public boolean isTeamsTurn(ChessBoard board) {
		return board.isWhiteTurn() == isWhite();
	}
}
