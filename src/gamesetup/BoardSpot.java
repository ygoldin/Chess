package gamesetup;

import pieces.ChessPiece;

public class BoardSpot {
	public final int row;
	public final int column;
	public ChessPiece piece;
	
	public BoardSpot(int row, int column) {
		this(row, column, null);
	}
	
	public BoardSpot(int row, int column, ChessPiece piece) {
		this.row = row;
		this.column = column;
		this.piece = piece;
	}
}
