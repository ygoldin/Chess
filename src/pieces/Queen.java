package pieces;

import gamesetup.*;

public class Queen implements ChessPiece {
	private static final int VALUE = 8;
	private final boolean isWhite;
	
	public Queen(boolean isWhite) {
		this.isWhite = isWhite;
	}
	
	@Override
	public int getValue() {
		return VALUE;
	}

	@Override
	public String getName() {
		return "queen";
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
