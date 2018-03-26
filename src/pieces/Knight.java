package pieces;

import java.util.HashSet;
import java.util.Set;

import gamesetup.*;

public class Knight implements ChessPiece {
	private static final int VALUE = 3;
	private final boolean isWhite;
	
	public Knight(boolean isWhite) {
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
		String type = "knight";
		if(isWhite) {
			return "w " + type;
		} else {
			return "b " + type;
		}
	}

	@Override
	public Set<PieceMove> legalMoves(ChessBoard board) {
		Set<PieceMove> moves = new HashSet<>();
		int[] firstDirection = new int[] {1, -1};
		int[] secondDirection = new int[] {2, -2};
		Integer[] myLocation = board.getSpotOfPiece(this);
		int myRow = myLocation[0];
		int myCol = myLocation[1];
		for(int direction1 : firstDirection) {
			for(int direction2 : secondDirection) {
				checkMove(myRow + direction1, myCol + direction2, board, moves);
				checkMove(myRow + direction2, myCol + direction1, board, moves);
			}
		}
		return moves;
	}
	
	//checks if the knight can move to that spot and what it might take
	private void checkMove(int curRow, int curCol, ChessBoard board, Set<PieceMove> moves) {
		if(board.isInBounds(curRow, curCol)) {
			ChessPiece otherPiece = board.getPieceAtSpot(curRow, curCol);
			if(otherPiece == null || otherPiece.isWhite() != isWhite) {
				moves.add(new PieceMove(curRow, curCol, otherPiece));
			}
		}
	}
}
