package pieces;

import java.util.HashSet;
import java.util.Set;
import gamesetup.*;

public class Pawn implements ChessPiece {
	private static final int VALUE = 1;
	private final boolean isWhite;
	
	public Pawn(boolean isWhite) {
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
		String type = "pawn";
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
		if(isWhite) {
			if(board.isInBounds(myRow - 1, myCol) && board.getPieceAtSpot(myRow - 1, myCol) == null) {
				moves.add(new PieceMove(myRow - 1, myCol));
				if(board.isInBounds(myRow - 2, myCol) && board.getPieceAtSpot(myRow - 2, myCol) == null) {
					moves.add(new PieceMove(myRow - 2, myCol));
				}
			}
		} else { //black
			
		}
		return moves;
	}

}
