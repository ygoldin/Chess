package pieces;

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
	public String getName() {
		return "king";
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
