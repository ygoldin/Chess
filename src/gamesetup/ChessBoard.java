package gamesetup;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pieces.*;

public class ChessBoard {
	public static final int SIZE = 8;
	private Map<ChessPiece, Integer[]> whitePieces;
	private Map<ChessPiece, Integer[]> blackPieces;
	private ChessPiece[][] board;
	private boolean whiteTurn;
	private boolean curPlayerInCheck;
	private ChessPiece pieceCausingCheck; //null if !curPlayerInCheck
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
		//TODO: make sure to:
		//clear en passant
		//check for kings in check
		//check if a move puts a king in check (by the moved piece or any other piece)
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
	
	/**
	 * checks if the king of the current player is in check
	 * 
	 * @return true if he is, false otherwise
	 */
	public boolean curPlayerIsInCheck() {
		return curPlayerInCheck;
	}
	
	/**
	 * checks if a piece is causing the opponent's king to be in check
	 * 
	 * @return the piece causing check if the opponent is in check, null otherwise
	 */
	public ChessPiece pieceCausingCheck() {
		return pieceCausingCheck;
	}
	
	/**
	 * finds all of the spots on the board that other pieces could move to, to block the "check"
	 * 
	 * @return a map from rows to columns of spots on the board in the line of fire (including the
	 * attacking piece). Will be null if the current player is not in check 
	 */
	public Map<Integer, Set<Integer>> inCheckLineOfFire() {
		if(!curPlayerInCheck) {
			return null;
		}
		Integer[] attackingPieceLocation = getSpotOfPiece(pieceCausingCheck);
		int attackingRow = attackingPieceLocation[0];
		int attackingCol = attackingPieceLocation[1];
		Map<Integer, Set<Integer>> spotsInLineOfFire = new HashMap<>();
		//add location of piece causing check
		spotsInLineOfFire.put(attackingRow, new HashSet<>());
		spotsInLineOfFire.get(attackingRow).add(attackingCol);
		if(pieceCausingCheck instanceof Bishop || pieceCausingCheck instanceof Queen ||
				pieceCausingCheck instanceof Rook) {
			//pawns and knights can only attack directly so there is no line of fire
			Integer[] kingLocation = locationOfInCheckKing();
			int kingRow = kingLocation[0];
			int kingCol = kingLocation[1];
			int spotsToLookAt = Math.abs(kingRow - attackingRow) - 1;
			if(pieceCausingCheck instanceof Bishop || pieceCausingCheck instanceof Queen) {
				addDiagonalToLineOfFire(attackingRow, attackingCol, kingRow, kingCol, spotsToLookAt,
						spotsInLineOfFire);
			}
			if(pieceCausingCheck instanceof Rook || pieceCausingCheck instanceof Queen) {
				addStraightToLineOfFire(attackingRow, attackingCol, kingRow, kingCol, spotsToLookAt,
						spotsInLineOfFire);
			}
		}
		return spotsInLineOfFire;
	}
	
	//finds the location of the "in check" king
	private Integer[] locationOfInCheckKing() {
		Map<ChessPiece, Integer[]> team;
		if(whiteTurn) {
			team = whitePieces;
		} else {
			team = blackPieces;
		}
		for(ChessPiece piece : team.keySet()) {
			if(piece instanceof King) {
				return getSpotOfPiece(piece);
			}
		}
		throw new IllegalStateException("king doesn't exist");
	}
	
	//adds to the line of fire in the diagonal attack
	private void addDiagonalToLineOfFire(int attackingRow, int attackingCol, int kingRow, int kingCol,
			int spotsToLookAt, Map<Integer, Set<Integer>> spotsInLineOfFire) {
		if(attackingRow < kingRow) {
			if(attackingCol < kingCol) {
				addToLineOfFire(attackingRow, attackingCol, 1, 1, spotsToLookAt,
						spotsInLineOfFire);
			} else {
				addToLineOfFire(attackingRow, attackingCol, 1, -1, spotsToLookAt,
						spotsInLineOfFire);
			}
		} else {
			if(attackingCol < kingCol) {
				addToLineOfFire(attackingRow, attackingCol, -1, 1, spotsToLookAt,
						spotsInLineOfFire);
			} else {
				addToLineOfFire(attackingRow, attackingCol, -1, -1, spotsToLookAt,
						spotsInLineOfFire);
			}
		}
	}
	
	//adds to the line of fire in the straight line attack
	private void addStraightToLineOfFire(int attackingRow, int attackingCol, int kingRow, int kingCol,
			int spotsToLookAt, Map<Integer, Set<Integer>> spotsInLineOfFire) {
		if(attackingRow < kingRow) {
			addToLineOfFire(attackingRow, attackingCol, 1, 0, spotsToLookAt,
					spotsInLineOfFire);
		} else if(attackingRow > kingRow) {
			addToLineOfFire(attackingRow, attackingCol, -1, 0, spotsToLookAt,
					spotsInLineOfFire);
		} else if(attackingCol < kingCol) {
			addToLineOfFire(attackingRow, attackingCol, 0, 1, spotsToLookAt,
					spotsInLineOfFire);
		} else { //attackingCol > kingCol
			addToLineOfFire(attackingRow, attackingCol, 0, -1, spotsToLookAt,
					spotsInLineOfFire);
		}
	}
	
	//adds spots to line of fire based on the diagonal/straight line passed in
	private void addToLineOfFire(int startingRow, int startingCol, int rowChange, int colChange,
			int spotsToLookAt, Map<Integer, Set<Integer>> spotsInLineOfFire) {
		for(int i = 1; i <= spotsToLookAt; i++) {
			int attackingRow = startingRow + rowChange*i;
			if(!spotsInLineOfFire.containsKey(attackingRow)) {
				spotsInLineOfFire.put(attackingRow, new HashSet<>());
			}
			spotsInLineOfFire.get(attackingRow).add(startingCol + colChange*i);
		}
	}
}
