package pieces;

import java.util.HashSet;
import java.util.Set;

import gamesetup.ChessBoard;
import gamesetup.PieceMove;

/**
 * this class represents a rook in a game of chess
 * @author Yael Goldin
 */
public class Rook extends ChessPiece {
	private static final int VALUE = 5;
	private static final String SYMBOL = "R";
	private final boolean isWhite;
	
	/**
	 * constructs a rook of the given team
	 */
	public Rook(boolean isWhite) {
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
		straightMoves(myLocation[0], myLocation[1], isWhite, board, moves, findingProtectedSpots);
		if(isTeamsTurn(board)) {
			leaveMovesThatStopCheck(moves, board);
		}
		return moves;
	}
	
	/**
	 * finds any possible moves of a rook/queen starting at the given spot
	 * 
	 * @param pieceRow The row of the spot it starts on
	 * @param pieceCol The column of the spot it starts on
	 * @param pieceIsWhite Whether the piece is white or not
	 * @param board The chess board the piece moves on
	 * @param moves The set to add possible found moves to
	 */
	public static void straightMoves(int pieceRow, int pieceCol, boolean pieceIsWhite,
			ChessBoard board, Set<PieceMove> moves, boolean findingProtectedSpots) {
		straightMovesInDirection(pieceRow - 1, pieceCol, -1, 0, pieceIsWhite, board, moves,
				findingProtectedSpots); //up
		straightMovesInDirection(pieceRow + 1, pieceCol, 1, 0, pieceIsWhite, board, moves,
				findingProtectedSpots); //down
		straightMovesInDirection(pieceRow, pieceCol - 1, 0, -1, pieceIsWhite, board, moves,
				findingProtectedSpots); //left
		straightMovesInDirection(pieceRow, pieceCol + 1, 0, 1, pieceIsWhite, board, moves,
				findingProtectedSpots); //right
	}
	
	//adds all of the diagonal moves in the given direction where the row and column increase by
	//the given direction amount on each iteration to represent going in one of the four diagonal directions
	private static void straightMovesInDirection(int curRow, int curCol, int rowDirection,
			int colDirection, boolean pieceIsWhite, ChessBoard board, Set<PieceMove> moves,
			boolean findingProtectedSpots) {
		while(board.isInBounds(curRow, curCol)) {
			ChessPiece otherPiece = board.getPieceAtSpot(curRow, curCol);
			if(otherPiece == null) { //can keep going in this direction
				moves.add(new PieceMove(curRow, curCol));
				curRow += rowDirection;
				curCol += colDirection;
			} else {
				if(findingProtectedSpots || otherPiece.isWhite() != pieceIsWhite) {
					moves.add(new PieceMove(curRow, curCol, otherPiece));
				}
				break; //if there's another piece, you can't move past it
			}
		}
	}
}
