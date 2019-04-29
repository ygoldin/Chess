package pieces;

import java.util.HashSet;
import java.util.Set;
import gamesetup.*;

/**
 * this class represents a pawn in a game of chess
 * @author Yael Goldin
 */
public class Pawn extends ChessPiece {
	private static final int VALUE = 1;
	private static final String SYMBOL = "P";
	private final boolean isWhite;
	
	/**
	 * constructs a pawn of the given team
	 */
	public Pawn(boolean isWhite) {
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
		int myRow = myLocation[0];
		int myCol = myLocation[1];
		int direction;
		if(isWhite) {
			direction = -1;
		} else {
			direction = 1;
		}
		//move forward one/two spots
		int oneRowForward = myRow + direction;
		if(board.isInBounds(oneRowForward, myCol) &&
				board.getPieceAtSpot(oneRowForward, myCol) == null) {
			moves.add(new PieceMove(oneRowForward, myCol));
			int twoRowsForward = oneRowForward + direction;
			if(!hasMoved && board.isInBounds(twoRowsForward, myCol) &&
					board.getPieceAtSpot(twoRowsForward, myCol) == null) {
				moves.add(new PieceMove(twoRowsForward, myCol));
			}
		}
		//check if you can take a piece diagonally to the left, then right
		checkTakingDiagonally(oneRowForward, myCol - 1, board, moves, findingProtectedSpots);
		checkTakingDiagonally(oneRowForward, myCol + 1, board, moves, findingProtectedSpots);
		//check if you can perform en passant
		if(board.pawnForEnPassant() != null) {
			checkEnPassant(myRow, myCol - 1, board, moves, direction);
			checkEnPassant(myRow, myCol + 1, board, moves, direction);
		}
		if(isTeamsTurn(board)) {
			leaveMovesThatStopCheck(moves, board);
		}
		return moves;
	}
	
	//checks if you can take a piece in that spot and then adds it to the moves
	private void checkTakingDiagonally(int curRow, int curCol, ChessBoard board, Set<PieceMove> moves,
			boolean findingProtectedSpots) {
		if(board.isInBounds(curRow, curCol)) {
			ChessPiece otherPiece = board.getPieceAtSpot(curRow, curCol);
			if(otherPiece != null && (findingProtectedSpots || !isSameTeam(otherPiece))) {
				moves.add(new PieceMove(curRow, curCol, otherPiece));
			}
		}
	}
	
	//checks if there is a pawn in that spot that can be taken en passant
	private void checkEnPassant(int curRow, int curCol, ChessBoard board, Set<PieceMove> moves,
			int direction) {
		if(board.isInBounds(curRow, curCol)) {
			ChessPiece otherPiece = board.getPieceAtSpot(curRow, curCol);
			if(otherPiece != null && !isSameTeam(otherPiece) && otherPiece instanceof Pawn &&
					board.pawnForEnPassant() == otherPiece) {
				moves.add(new PieceMove(curRow + direction, curCol, otherPiece));
			}
		}
	}
}
