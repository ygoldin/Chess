package pieces;

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
	public String getName() {
		return "bishop";
	}

	@Override
	public boolean isWhite() {
		return isWhite;
	}

	@Override
	public PieceMove legalMoves(ChessBoard board) {
		// TODO Auto-generated method stub
		return null;
	}
}
