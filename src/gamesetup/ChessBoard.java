package gamesetup;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pieces.ChessPiece;

public class ChessBoard {
	private static final int SIZE = 8;
	private Map<ChessPiece, BoardSpot> whitePieces;
	private Map<ChessPiece, BoardSpot> blackPieces;
	private Set<BoardSpot> board;
	private boolean whiteTurn;
	
	public ChessBoard() {
		whitePieces = initializeTeamPieces(true);
		blackPieces = initializeTeamPieces(false);
		board = new HashSet<>();
		//add pieces to set
		whiteTurn = true;
	}
	
	/**
	 * returns all of a team's pieces
	 * 
	 * @param isWhite Whether the client wants the white or black pieces
	 * @return an unmodifiable map from every chess piece on the team to its location on the board
	 */
	public Map<ChessPiece, BoardSpot> getAllPieces(boolean isWhite) {
		if(isWhite) {
			return Collections.unmodifiableMap(whitePieces);
		} else {
			return Collections.unmodifiableMap(blackPieces);
		}
	}
	
	//sets up the pieces for the given team at the start of the game
	private Map<ChessPiece, BoardSpot> initializeTeamPieces(boolean isWhite) {
		Map<ChessPiece, BoardSpot> pieces = new HashMap<>();
		
		return pieces;
	}
	
	/**
	 * checks if there's a piece at the given spot
	 * 
	 * @param spot The spot to look at
	 * @return the chess piece at that spot, or null if no piece exists there
	 */
	public ChessPiece getPieceAtSpot(BoardSpot spot) {
		return null;
	}
	
	/**
	 * checks the location of a given piece
	 * 
	 * @param piece The piece to look at
	 * @return the location of the piece on the board
	 */
	public BoardSpot getSpotOfPiece(ChessPiece piece) {
		return null;
	}
	
	/**
	 * finds all of the legal moves for the given piece
	 * 
	 * @param piece The piece to find moves for
	 * @return a set of all of the moves the piece can make, will be empty if it cannot make any
	 */
	public Set<PieceMove> legalMoves(ChessPiece piece) {
		return null;
	}
	
	/**
	 * moves the given piece to the given spot
	 * 
	 * @param piece The piece to move
	 * @param spot The spot to move the piece to
	 * @return The piece taken during this move, or null if no piece was taken
	 */
	public ChessPiece makeMove(ChessPiece piece, BoardSpot spot) {
		return null;
	}
	
	/**
	 * checks whose turn it is
	 * 
	 * @return true if it is white's turn, false if black's
	 */
	public boolean isWhiteTurn() {
		return whiteTurn;
	}
}
