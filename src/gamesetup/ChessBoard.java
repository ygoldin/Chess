package gamesetup;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pieces.ChessPiece;

public class ChessBoard {
	private static final int SIZE = 8;
	private Map<ChessPiece, Integer[]> whitePieces;
	private Map<ChessPiece, Integer[]> blackPieces;
	private ChessPiece[][] board;
	private boolean whiteTurn;
	private Set<ChessPiece> doubleJumpPawns;
	
	public ChessBoard() {
		whitePieces = initializeTeamPieces(true);
		blackPieces = initializeTeamPieces(false);
		board = new ChessPiece[SIZE][SIZE];
		//add pieces to board
		whiteTurn = true;
		doubleJumpPawns = new HashSet<>();
	}
	
	//sets up the pieces for the given team at the start of the game
	private Map<ChessPiece, Integer[]> initializeTeamPieces(boolean isWhite) {
		Map<ChessPiece, Integer[]> pieces = new HashMap<>();
		
		return pieces;
	}
	
	/**
	 * returns all of a team's pieces
	 * 
	 * @param isWhite Whether the client wants the white or black pieces
	 * @return an unmodifiable map from every chess piece on the team to its location on the board
	 */
	public Map<ChessPiece, Integer[]> getAllPieces(boolean isWhite) {
		if(isWhite) {
			return Collections.unmodifiableMap(whitePieces);
		} else {
			return Collections.unmodifiableMap(blackPieces);
		}
	}
	
	/**
	 * checks if there's a piece at the given spot
	 * 
	 * @param row The row of the spot
	 * @param col The column of the spot
	 * @return the chess piece at that spot, or null if no piece exists there
	 */
	public ChessPiece getPieceAtSpot(int row, int col) {
		return null;
	}
	
	/**
	 * checks the location of a given piece
	 * 
	 * @param piece The piece to look at
	 * @return the location of the piece on the board, in the form [row, col]
	 */
	public Integer[] getSpotOfPiece(ChessPiece piece) {
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
	 * @param row The row of the spot
	 * @param col The column of the spot
	 * @return The piece taken during this move, or null if no piece was taken
	 */
	public ChessPiece makeMove(ChessPiece piece, int row, int col) {
		//TODO: make sure to clear en passant
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
	
	/**
	 * returns if the given spot is in bounds on the board
	 * 
	 * @param row The row of the spot
	 * @param col The column of the spot
	 * @return true if it's in bounds, false otherwise
	 */
	public boolean isInBounds(int row, int col) {
		return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
	}
	
	/**
	 * finds all of the pawns that performed a double jump
	 * 
	 * @return all of the pawns currently in position to be taken en-passant
	 */
	public Set<ChessPiece> pawnsForEnPassant() {
		return Collections.unmodifiableSet(doubleJumpPawns);
	}
}
