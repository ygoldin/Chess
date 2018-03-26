package pieces;

import java.util.Set;

import gamesetup.*;

public class Bishop implements ChessPiece {
	private static final int VALUE = 3;
	private final boolean isWhite;
	
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
		// TODO Auto-generated method stub
		return null;
	}
}
