package pieces;

import java.util.Set;
import gamesetup.*;

public interface ChessPiece {
	
	public int getValue();
	
	public boolean isWhite();
	
	public Set<PieceMove> legalMoves(ChessBoard board);
}
