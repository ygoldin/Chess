package pieces;

import java.util.HashSet;
import java.util.Set;

import gamesetup.*;

public class King implements ChessPiece {
	private static final int VALUE = 0;
	private final boolean isWhite;
	
	public King(boolean isWhite) {
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
		String type = "king";
		if(isWhite) {
			return "w " + type;
		} else {
			return "b " + type;
		}
	}

	@Override
	public Set<PieceMove> legalMoves(ChessBoard board) {
		Set<PieceMove> moves = new HashSet<>();
		Integer[] myLocation = board.getSpotOfPiece(this);
		int myRow = myLocation[0];
		int myCol = myLocation[1];
		for(int curRow = myRow - 1; curRow <= myRow + 1; curRow++) {
			for(int curCol = myCol - 1; curCol <= myCol + 1; curCol++) {
				if((curRow != myRow || curCol != myCol) && board.isInBounds(curRow, curCol)) {
					//TODO: not checking for being in check
					ChessPiece otherPiece = board.getPieceAtSpot(curRow, curCol);
					if(otherPiece == null || otherPiece.isWhite() != isWhite) {
						moves.add(new PieceMove(curRow, curCol, otherPiece));
					}
				}
			}
		}
		return moves;
	}
}
