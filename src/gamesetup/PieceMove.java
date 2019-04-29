package gamesetup;

import pieces.ChessPiece;

/**
 * PieceMove models the result of moving a piece to a destination (and possible taking a piece)
 * @author Yael Goldin
 */
public class PieceMove {
	public final int destinationRow;
	public final int destinationColumn;
	public final ChessPiece takenPiece;
	
	/**
	 * constructs a move to the destination, with no piece taken
	 */
	public PieceMove(int destinationRow, int destinationColumn) {
		this(destinationRow, destinationColumn, null);
	}
	
	/**
	 * constructs a move to the destination
	 */
	public PieceMove(int destinationRow, int destinationColumn, ChessPiece takenPiece) {
		this.destinationRow = destinationRow;
		this.destinationColumn = destinationColumn;
		this.takenPiece = takenPiece;
	}
}
