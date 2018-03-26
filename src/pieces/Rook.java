package pieces;

import gamesetup.ChessBoard;
import gamesetup.PieceMove;

public class Rook implements ChessPiece {
	private static final int VALUE = 5;
	private final boolean isWhite;
	
	public Rook(boolean isWhite) {
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
		String type = "rook";
		if(isWhite) {
			return "w " + type;
		} else {
			return "b " + type;
		}
	}

	@Override
	public PieceMove legalMoves(ChessBoard board) {
		// TODO Auto-generated method stub
		return null;
	}
}
