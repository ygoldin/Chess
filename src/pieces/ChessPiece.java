package pieces;

import java.util.Iterator;
import java.util.Map;
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
	 * finds the symbol representation of the piece type
	 * 
	 * @return the symbol
	 */
	public abstract String getSymbol();
	
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
	
	/**
	 * retains the moves that will stop the check from occurring
	 * does nothing if there is no check
	 * 
	 * @param moves The possible legal moves of the piece without considering check
	 * @param board The board on which the check occurred
	 */
	protected static void leaveMovesThatStopCheck(Set<PieceMove> moves, ChessBoard board) {
		if(board.curPlayerIsInCheck()) {
			Map<Integer, Set<Integer>> lineOfFire = board.inCheckLineOfFire();
			Iterator<PieceMove> moveIterator = moves.iterator();
			while(moveIterator.hasNext()) {
				PieceMove curMove = moveIterator.next();
				if(!lineOfFire.containsKey(curMove.destinationRow) ||
						!lineOfFire.get(curMove.destinationRow).contains(curMove.destinationColumn)) {
					moveIterator.remove();
				}
			}
		}
	}
	
	/**
	 * checks if this piece is preventing a check on its king (it can't move if that's true)
	 * 
	 * @param board The board the piece is on
	 * @return true if it is preventing check, false otherwise
	 */
	protected boolean isPreventingCheck(ChessBoard board) {
		return false;
	}
	
	/**
	 * retains the moves that will keep the piece in the line of fire of a possible check
	 * does nothing if there is no opposing piece that could be checking the king if this piece moved
	 * 
	 * @param moves The possible legal moves of the piece without considering check
	 * @param board The board on which the check occurred
	 */
	protected static void leaveMovesThatRemainInLineOfFire(Set<PieceMove> moves, ChessBoard board) {
		
	}
	
	@Override
	public String toString() {
		String type = getSymbol();
		if(isWhite()) {
			return "w" + type;
		} else {
			return "b" + type;
		}
	}
}
