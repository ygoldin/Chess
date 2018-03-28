package gamesetup;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	private Map<ChessPiece, Set<PieceMove>> currentTeamMoves;
	private Integer[] currentTeamsKingLocation;
	
	public ChessBoard() {
		board = new ChessPiece[SIZE][SIZE];
		whitePieces = initializeTeamPieces(true);
		blackPieces = initializeTeamPieces(false);
		whiteTurn = true;
		doubleJumpPawns = new HashSet<>();
		currentTeamMoves = findCurrentTeamsMoves();
	}
	
	//sets up the pieces for the given team at the start of the game
	private Map<ChessPiece, Integer[]> initializeTeamPieces(boolean isWhite) {
		Map<ChessPiece, Integer[]> teamPieces = new HashMap<>();
		int pawnRow;
		int otherPiecesRow;
		if(isWhite) {
			pawnRow = SIZE - 2;
			otherPiecesRow = SIZE - 1;
		} else {
			pawnRow = 1;
			otherPiecesRow = 0;
		}
		//creates pieces for the team
		for(int curCol = 0; curCol < SIZE; curCol++) {
			teamPieces.put(new Pawn(isWhite), new Integer[] {pawnRow, curCol});
		}
		int inFromBoundary = 0;
		teamPieces.put(new Rook(isWhite), new Integer[] {otherPiecesRow, inFromBoundary});
		teamPieces.put(new Rook(isWhite), new Integer[] {otherPiecesRow, SIZE - 1 - inFromBoundary});
		inFromBoundary++;
		teamPieces.put(new Knight(isWhite), new Integer[] {otherPiecesRow, inFromBoundary});
		teamPieces.put(new Knight(isWhite), new Integer[] {otherPiecesRow, SIZE - 1 - inFromBoundary});
		inFromBoundary++;
		teamPieces.put(new Bishop(isWhite), new Integer[] {otherPiecesRow, inFromBoundary});
		teamPieces.put(new Bishop(isWhite), new Integer[] {otherPiecesRow, SIZE - 1 - inFromBoundary});
		inFromBoundary++;
		teamPieces.put(new Queen(isWhite), new Integer[] {otherPiecesRow, inFromBoundary});
		teamPieces.put(new King(isWhite), new Integer[] {otherPiecesRow, SIZE - 1 - inFromBoundary});
		if(isWhite) {
			currentTeamsKingLocation = new Integer[] {otherPiecesRow, SIZE - 1 - inFromBoundary};
		}
		//puts pieces on the board
		for(ChessPiece piece : teamPieces.keySet()) {
			Integer[] location = teamPieces.get(piece);
			board[location[0]][location[1]] = piece;
		}
		return teamPieces;
	}
	
	//map from every piece on the current team to every move it can make
	//empty set means this piece cannot move
	private Map<ChessPiece, Set<PieceMove>> findCurrentTeamsMoves() {
		Map<ChessPiece, Integer[]> currentTeamPieces;
		Map<ChessPiece, Integer[]> opposingTeamPieces;
		if(whiteTurn) {
			currentTeamPieces = whitePieces;
			opposingTeamPieces = blackPieces;
		} else {
			currentTeamPieces = blackPieces;
			opposingTeamPieces = whitePieces;
		}
		Map<ChessPiece, Map<Integer, Set<Integer>>> linesOfFire = new HashMap<>();
		for(ChessPiece opposingPiece : opposingTeamPieces.keySet()) {
			Map<Integer, Set<Integer>> spotsInLineOfFire =
					spotsBetweenOpposingPieceAndCurrentKing(opposingPiece);
			if(spotsInLineOfFire != null) {
				linesOfFire.put(opposingPiece, spotsInLineOfFire);
			}
		}
		
		Map<ChessPiece, Set<PieceMove>> possibleMoves = new HashMap<>();
		for(ChessPiece teamPiece : currentTeamPieces.keySet()) {
			if(teamPiece instanceof King) {
				possibleMoves.put(teamPiece, teamPiece.legalMoves(this));
			} else {
				ChessPiece lineOfFireCauser = inPossibleLineOfFire(teamPiece, linesOfFire);
				if(lineOfFireCauser == null) {
					possibleMoves.put(teamPiece, teamPiece.legalMoves(this));
				} else if(teamPiece instanceof Pawn || teamPiece instanceof Knight) {
					possibleMoves.put(teamPiece, new HashSet<>());
				} else {
					Set<PieceMove> moves = teamPiece.legalMoves(this);
					retainSpotsInLineOfFire(moves, linesOfFire.get(lineOfFireCauser));
					possibleMoves.put(teamPiece, moves);
				}
			}
		}
		return possibleMoves;
	}
	
	//returns the piece causing the line of fire for the defender
	//or null if the defender is free to move anywhere
	private ChessPiece inPossibleLineOfFire(ChessPiece defender,
			Map<ChessPiece, Map<Integer, Set<Integer>>> linesOfFire) {
		Integer[] defenderLocation = getSpotOfPiece(defender);
		int defenderRow = defenderLocation[0];
		int defenderCol = defenderLocation[1];
		for(ChessPiece lineOfFireCauser : linesOfFire.keySet()) {
			Map<Integer, Set<Integer>> spotsInLineOfFire = linesOfFire.get(lineOfFireCauser);
			if(spotsInLineOfFire.containsKey(defenderRow) &&
					spotsInLineOfFire.get(defenderRow).contains(defenderCol)) {
				return lineOfFireCauser;
			}
		}
		return null;
	}
	
	//only keep moves in the set that are in the line of fire
	private void retainSpotsInLineOfFire(Set<PieceMove> moves, Map<Integer, Set<Integer>> lineOfFire) {
		Iterator<PieceMove> moveIterator = moves.iterator();
		while(moveIterator.hasNext()) {
			PieceMove curMove = moveIterator.next();
			if(!lineOfFire.containsKey(curMove.destinationRow) ||
					!lineOfFire.get(curMove.destinationRow).contains(curMove.destinationColumn)) {
				moveIterator.remove();
			}
		}
	}
	
	//finds all of the spots in the line of fire for the opposing piece
	//null if it cannot be in an attacking "line" with the king
	private Map<Integer, Set<Integer>> spotsBetweenOpposingPieceAndCurrentKing(ChessPiece opposingPiece) {
		if(opposingPiece instanceof Pawn || opposingPiece instanceof Knight ||
				opposingPiece instanceof King) {
			return null;
		}
		Integer[] pieceLocation = getSpotOfPiece(opposingPiece);
		int attackingRow = pieceLocation[0];
		int attackingCol = pieceLocation[1];
		int kingRow = currentTeamsKingLocation[0];
		int kingCol = currentTeamsKingLocation[1];
		//pieces can only possibly have a line of fire if they're in the right position
		//so a bishop is diagonally away, a rook is in a straight line, and a queen is one of those
		if(opposingPiece instanceof Bishop && Math.abs(attackingRow - kingRow) !=
				Math.abs(attackingCol - kingCol)) {
			return null;
		} else if(opposingPiece instanceof Rook && attackingRow != kingRow && attackingCol != kingCol) {
			return null;
		} else if(opposingPiece instanceof Queen &&
				Math.abs(attackingRow - kingRow) != Math.abs(attackingCol - kingCol) &&
				attackingRow != kingRow && attackingCol != kingCol) {
			return null;
		}
		//now the piece is confirmed to be in the right position for a possible line of fire
		int spotsToLookAt = Math.abs(kingRow - attackingRow - 1);
		Map<Integer, Set<Integer>> result = new HashMap<>();
		if(opposingPiece instanceof Bishop || (opposingPiece instanceof Queen && attackingRow != kingRow &&
				attackingCol != kingCol)) {
			addDiagonalToLineOfFire(attackingRow, attackingCol, kingRow, kingCol, spotsToLookAt, result);
		} else {
			addStraightToLineOfFire(attackingRow, attackingCol, kingRow, kingCol, spotsToLookAt, result);
		}
		
		int[] spotInLineOfFire = null;
		for(int row : result.keySet()) {
			for(int col : result.get(row)) {
				if(opposingPiece.isSameTeam(getPieceAtSpot(row, col))) {
					return null;
				} else if(spotInLineOfFire == null) { //first "defender" found
					spotInLineOfFire = new int[] {row, col};
				} else { //multiple "defenders" found, so there is no immediate threat
					return null;
				}
			}
		}
		//adds the position of the attacking piece to the line of fire
		if(!result.containsKey(attackingRow)) {
			result.put(attackingRow, new HashSet<>());
		}
		result.get(attackingRow).add(attackingCol);
		return result;
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
		if(!isInBounds(row, col)) {
			throw new IllegalArgumentException("spot out of bounds");
		}
		return board[row][col];
	}
	
	/**
	 * checks the location of a given piece
	 * 
	 * @param piece The piece to look at
	 * @return the location of the piece on the board, in the form [row, col]
	 */
	public Integer[] getSpotOfPiece(ChessPiece piece) {
		Map<ChessPiece, Integer[]> teamPieces;
		if(piece.isWhite()) {
			teamPieces = whitePieces;
		} else {
			teamPieces = blackPieces;
		}
		if(!teamPieces.containsKey(piece)) {
			throw new IllegalArgumentException("piece doesn't exist in the game");
		}
		return teamPieces.get(piece);
	}
	
	/**
	 * finds all of the legal moves for the given piece
	 * 
	 * @param piece The piece to find moves for
	 * @return a set of all of the moves the piece can make, will be empty if it cannot make any
	 * @throws IllegalArgumentException if it's not this team's turn or the piece doesn't exist in the game
	 */
	public Set<PieceMove> legalMoves(ChessPiece piece) {
		checkInvalidPieceInput(piece);
		return currentTeamMoves.get(piece);
	}
	
	/**
	 * moves the given piece to the given spot
	 * 
	 * @param piece The piece to move
	 * @param row The row of the spot
	 * @param col The column of the spot
	 * @return The piece taken during this move, or null if no piece was taken
	 * @throws IllegalArgumentException if the spot is out of bounds, it is not this team's turn,
	 * the piece doesn't exist in the game, or the move is not valid
	 */
	public ChessPiece makeMove(ChessPiece piece, int row, int col) {
		if(!isInBounds(row, col)) {
			throw new IllegalArgumentException("spot not in bounds");
		}
		PieceMove move = isValidMove(piece, row, col);
		if(move == null) {
			throw new IllegalArgumentException("invalid move");
		}
		//TODO: make sure to:
		//clear en passant
		//check for kings in check
		//check if a move puts a king in check (by the moved piece or any other piece)
		return null;
	}
	
	//checks if it's valid to move that piece there
	//returns the associated PieceMove object with that move
	private PieceMove isValidMove(ChessPiece piece, int row, int col) {
		checkInvalidPieceInput(piece);
		Set<PieceMove> possiblePieceMoves = currentTeamMoves.get(piece);
		for(PieceMove move : possiblePieceMoves) {
			if(move.destinationRow == row && move.destinationColumn == col) {
				return move;
			}
		}
		return null;
	}
	
	//checks if the piece is valid (this teams turn, is on the board)
	private void checkInvalidPieceInput(ChessPiece piece) {
		if(!piece.isTeamsTurn(this)) {
			throw new IllegalArgumentException("it's the opposing team's turn");
		} else if(!currentTeamMoves.containsKey(piece)) {
			throw new IllegalArgumentException("this piece doesn't exist in the game");
		}
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
	
	public Integer[] currentTeamsKingsLocation() {
		return currentTeamsKingLocation;
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
			Integer[] kingLocation = locationOfCurrentTeamsKing();
			int kingRow = kingLocation[0];
			int kingCol = kingLocation[1];
			int spotsToLookAt = Math.abs(kingRow - attackingRow) - 1;
			if(pieceCausingCheck instanceof Queen) {
				if(attackingRow == kingRow || attackingCol == kingCol) { //attacking like a rook
					addStraightToLineOfFire(attackingRow, attackingCol, kingRow, kingCol, spotsToLookAt,
							spotsInLineOfFire);
				} else { //attacking like a bishop
					addDiagonalToLineOfFire(attackingRow, attackingCol, kingRow, kingCol, spotsToLookAt,
							spotsInLineOfFire);
				}
			} else if(pieceCausingCheck instanceof Bishop) {
				addDiagonalToLineOfFire(attackingRow, attackingCol, kingRow, kingCol, spotsToLookAt,
						spotsInLineOfFire);
			} else { //rook
				addStraightToLineOfFire(attackingRow, attackingCol, kingRow, kingCol, spotsToLookAt,
						spotsInLineOfFire);
			}
		}
		return spotsInLineOfFire;
	}
	
	//finds the location of the current team's king
	private Integer[] locationOfCurrentTeamsKing() {
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
	
	/**
	 * checks if the game is over
	 * 
	 * @return true if the game is over, false otherwise
	 */
	public boolean isGameOver() {
		return currentTeamMoves.isEmpty();
	}
	
	/**
	 * checks if the game ended in a checkmate
	 * 
	 * @return true if it did, false if it ended in stalemate
	 * @throws IllegalStateException if the game is not over
	 */
	public boolean endedInCheckmate() {
		if(!isGameOver()) {
			throw new IllegalStateException("game not over");
		}
		return curPlayerInCheck;
	}
	
	@Override
	public String toString() {
		String result = "";
		String blank = "  ";
		for(int row = 0; row < SIZE; row++) {
			result += "[";
			ChessPiece curPiece = board[row][0];
			if(curPiece != null) {
				result += curPiece.toString();
			} else {
				result += blank;
			}
			for(int col = 1; col < SIZE; col++) {
				result += ", ";
				curPiece = board[row][col];
				if(curPiece != null) {
					result += curPiece.toString();
				} else {
					result += blank;
				}
			}
			result += "]";
			if(row != SIZE - 1) {
				result += "\n";
			}
		}
		return result;
	}
}
