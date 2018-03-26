package gamesetup;

import pieces.ChessPiece;

public class PieceMove {
	public final int destinationRow;
	public final int destinationColumn;
	public final ChessPiece takenPiece;
	
	public PieceMove(int destinationRow, int destinationColumn) {
		this(destinationRow, destinationColumn, null);
	}
	
	public PieceMove(int destinationRow, int destinationColumn, ChessPiece takenPiece) {
		this.destinationRow = destinationRow;
		this.destinationColumn = destinationColumn;
		this.takenPiece = takenPiece;
	}
}
