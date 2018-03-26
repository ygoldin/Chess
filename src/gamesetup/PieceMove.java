package gamesetup;

import pieces.ChessPiece;

public class PieceMove {
	public final BoardSpot destination;
	public final ChessPiece takenPiece;
	
	public PieceMove(BoardSpot destination) {
		this(destination, null);
	}
	
	public PieceMove(BoardSpot destination, ChessPiece takenPiece) {
		this.destination = destination;
		this.takenPiece = takenPiece;
	}
}
