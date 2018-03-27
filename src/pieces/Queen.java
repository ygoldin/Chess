package pieces;

import java.util.HashSet;
import java.util.Set;

import gamesetup.*;

/**
 * this class represents a queen in a game of chess
 * @author Yael Goldin
 */
public class Queen implements ChessPiece {
	private static final int VALUE = 8;
	private final boolean isWhite;
	private boolean hasMoved;
	
	/**
	 * constructs a queen of the given team
	 * 
	 * @param isWhite whether the queen is for the white team (true) or black (false)
	 */
	public Queen(boolean isWhite) {
		this.isWhite = isWhite;
	}
	
	@Override
	public int getValue() {
		return VALUE;
	}

	@Override
	public boolean isWhite() {
		return isWhite;
	}
	
	@Override
	public String toString() {
		String type = "queen";
		if(isWhite) {
			return "w " + type;
		} else {
			return "b " + type;
		}
	}

	@Override
	public Set<PieceMove> legalMoves(ChessBoard board) {
		Set<PieceMove> moves = new HashSet<>();
		if(board.isWhiteTurn() == isWhite) {
			Integer[] myLocation = board.getSpotOfPiece(this);
			Rook.straightMoves(myLocation[0], myLocation[1], isWhite, board, moves);
			Bishop.diagonalMoves(myLocation[0], myLocation[1], isWhite, board, moves);
		}
		return moves;
	}
	
	@Override
	public boolean hasNeverMoved() {
		return hasMoved;
	}

	@Override
	public void markMoved() {
		hasMoved = true;
	}
}
