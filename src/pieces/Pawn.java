package pieces;

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
	public String getName() {
		return "pawn";
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
