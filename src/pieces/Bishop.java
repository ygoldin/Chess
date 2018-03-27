package pieces;

import java.util.HashSet;
import java.util.Set;

import gamesetup.*;

/**
 * this class represents a bishop in a game of chess
 * @author Yael Goldin
 */
public class Bishop extends ChessPiece {
	private static final int VALUE = 3;
	private final boolean isWhite;
	
	/**
	 * constructs a bishop of the given team
	 * 
	 * @param isWhite whether the bishop is for the white team (true) or black (false)
	 */
	public Bishop(boolean isWhite) {
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
		String type = "bishop";
		if(isWhite) {
			return "w " + type;
		} else {
			return "b " + type;
		}
	}

	@Override
	public Set<PieceMove> legalMoves(ChessBoard board) {
		Set<PieceMove> moves = new HashSet<>();
		if(isTeamsTurn(board)) {
			Integer[] myLocation = board.getSpotOfPiece(this);
			diagonalMoves(myLocation[0], myLocation[1], isWhite, board, moves);
			leaveMovesThatStopCheck(moves, board);
		}
		return moves;
	}
	
	/**
	 * finds any possible moves of a bishop/queen starting at the given spot
	 * 
	 * @param pieceRow The row of the spot it starts on
	 * @param pieceCol The column of the spot it starts on
	 * @param pieceIsWhite Whether the piece is white or not
	 * @param board The chess board the piece moves on
	 * @param moves The set to add possible found moves to
	 */
	public static void diagonalMoves(int pieceRow, int pieceCol, boolean pieceIsWhite,
			ChessBoard board, Set<PieceMove> moves) {
		diagonalMovesInDirection(pieceRow - 1, pieceCol - 1, -1, -1, pieceIsWhite, board, moves); //up left
		diagonalMovesInDirection(pieceRow - 1, pieceCol - 1, -1, 1, pieceIsWhite, board, moves); //up right
		diagonalMovesInDirection(pieceRow - 1, pieceCol - 1, 1, -1, pieceIsWhite, board, moves); //down left
		diagonalMovesInDirection(pieceRow - 1, pieceCol - 1, 1, 1, pieceIsWhite, board, moves); //down right
	}
	
	//adds all of the diagonal moves in the given direction where the row and column increase by
	//the given direction amount on each iteration to represent going in one of the four diagonal directions
	private static void diagonalMovesInDirection(int curRow, int curCol, int rowDirection,
			int colDirection, boolean pieceIsWhite, ChessBoard board, Set<PieceMove> moves) {
		while(board.isInBounds(curRow, curCol)) {
			ChessPiece otherPiece = board.getPieceAtSpot(curRow, curCol);
			if(otherPiece == null) { //can keep going in this direction
				moves.add(new PieceMove(curRow, curCol));
				curRow += rowDirection;
				curCol += colDirection;
			} else { //there's another piece, you can't move past it
				if(otherPiece.isWhite() != pieceIsWhite) { //can take it TODO can't call isSameTeam
					moves.add(new PieceMove(curRow, curCol, otherPiece));
				}
				break;
			}
		}
	}
}
