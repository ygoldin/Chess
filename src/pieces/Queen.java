package pieces;

import java.util.HashSet;
import java.util.Set;

import gamesetup.*;

/**
 * this class represents a queen in a game of chess
 * @author Yael Goldin
 */
public class Queen extends ChessPiece {
	private static final int VALUE = 8;
	private static final String SYMBOL = "Q";
	private final boolean isWhite;
	
	/**
	 * constructs a queen of the given team
	 */
	public Queen(boolean isWhite) {
		this.isWhite = isWhite;
	}
	
	@Override
	public int getValue() {
		return VALUE;
	}
	
	@Override
	public String getSymbol() {
		return SYMBOL;
	}

	@Override
	public boolean isWhite() {
		return isWhite;
	}

	@Override
	public Set<PieceMove> legalMoves(ChessBoard board, boolean findingProtectedSpots) {
		Set<PieceMove> moves = new HashSet<>();
		Integer[] myLocation = board.getSpotOfPiece(this);
		Rook.straightMoves(myLocation[0], myLocation[1], isWhite, board, moves, findingProtectedSpots);
		Bishop.diagonalMoves(myLocation[0], myLocation[1], isWhite, board, moves, findingProtectedSpots);
		if(isTeamsTurn(board)) {
			leaveMovesThatStopCheck(moves, board);
		}
		return moves;
	}
}
