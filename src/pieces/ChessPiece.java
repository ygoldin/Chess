package pieces;

import gamesetup.*;

public interface ChessPiece {
	
	public int getValue();
	
	public boolean isWhite();
	
	public PieceMove legalMoves(ChessBoard board);
}
