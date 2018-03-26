package pieces;

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
		// TODO Auto-generated method stub
		return null;
	}
}
