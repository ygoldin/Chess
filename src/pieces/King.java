package pieces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gamesetup.*;

/**
 * this class represents a king in a game of chess
 * @author Yael Goldin
 */
public class King extends ChessPiece {
	private static final int VALUE = 0;
	private static final String SYMBOL = "K";
	private final boolean isWhite;
	
	/**
	 * constructs a king of the given team
	 */
	public King(boolean isWhite) {
		this.isWhite = isWhite;
	}
	
	@Override
	public int getValue() {
		return VALUE;
	}
	
	@Override
	public String getSymbol() {
		return SYMBOL;
	}

	@Override
	public boolean isWhite() {
		return isWhite;
	}

	@Override
	public Set<PieceMove> legalMoves(ChessBoard board, boolean findingProtectedSpots) {
		Set<PieceMove> moves = new HashSet<>();
		Integer[] myLocation = board.getSpotOfPiece(this);
		int myRow = myLocation[0];
		int myCol = myLocation[1];
		Map<Integer, Set<Integer>> illegalMoves = movesIntoALineOfFire(board);
		for(int curRow = myRow - 1; curRow <= myRow + 1; curRow++) {
			for(int curCol = myCol - 1; curCol <= myCol + 1; curCol++) {
				//first check for not staying still, going out of bounds, or going into a line of fire
				if((curRow != myRow || curCol != myCol) && board.isInBounds(curRow, curCol) &&
						(!illegalMoves.containsKey(curRow) ||
						!illegalMoves.get(curRow).contains(curCol))) {
					ChessPiece otherPiece = board.getPieceAtSpot(curRow, curCol);
					if(otherPiece == null || !isSameTeam(otherPiece)) {
						moves.add(new PieceMove(curRow, curCol, otherPiece));
					}
				}
			}
		}
		//check if the king can castle
		if(!hasMoved && !board.curPlayerIsInCheck()) { //can't castle if king has moved/is in check
			if(canCastle(myRow, 0, myCol - 2, myCol - 1, board, illegalMoves)) { //castle left
				moves.add(new PieceMove(myRow, myCol - 2));
			}
			if(canCastle(myRow, ChessBoard.SIZE - 1, myCol + 1, ChessBoard.SIZE - 2, board,
					illegalMoves)) { //castle right
				moves.add(new PieceMove(myRow, myCol + 2));
			}
		}
		return moves;
	}
	
	//returns a map from rows to columns of moves that would put the king in a line of fire
	//not all are necessarily reachable for the king at this point in the game, but that doesn't affect
	//the final outcome since the king just needs to know where it can't go, not where it can
	private Map<Integer, Set<Integer>> movesIntoALineOfFire(ChessBoard board) {
		Map<Integer, Set<Integer>> result = new HashMap<>();
		if(!isTeamsTurn(board)) { //if not this teams turn, don't restrict it's attacks
			return result;
		}
		Map<ChessPiece, Integer[]> otherTeam = board.getAllPieces(!isWhite);
		for(ChessPiece opposingPiece : otherTeam.keySet()) {
			if(!(opposingPiece instanceof Pawn)) {
				Set<PieceMove> possible = opposingPiece.legalMoves(board, true);
				for(PieceMove move : possible){
					if(!result.containsKey(move.destinationRow)) {
						result.put(move.destinationRow, new HashSet<Integer>());
					}
					result.get(move.destinationRow).add(move.destinationColumn);
				}
			} else {
				//only diagonal "taking" is the line of fire for pawns
				Integer[] pawnLocation = board.getSpotOfPiece(opposingPiece);
				int pawnAttackRow;
				if(isWhite) { //pawn is black
					pawnAttackRow = pawnLocation[0] + 1;
				} else { //pawn is white
					pawnAttackRow = pawnLocation[0] - 1;
				}
				if(!result.containsKey(pawnAttackRow)) {
					result.put(pawnAttackRow, new HashSet<Integer>());
				}
				//doesn't matter if these locations are out of bounds or taken by other pieces
				//king can't move there in that case anyway
				result.get(pawnAttackRow).add(pawnLocation[1] - 1);
				result.get(pawnAttackRow).add(pawnLocation[1] + 1);
			}
		}
		return result;
	}
	
	//checks if you can castle with the given rook
	//the rook couldn't have moved before and there can be no pieces in the way
	//the king can't move through/land on a square that would put it in check
	private boolean canCastle(int myRow, int possibleRookColumn, int columnCheckStart, int columnCheckEnd,
			ChessBoard board, Map<Integer, Set<Integer>> illegalMoves) {
		ChessPiece possibleRook = board.getPieceAtSpot(myRow, possibleRookColumn);
		if(possibleRook instanceof Rook && isSameTeam(possibleRook) && !possibleRook.hasMoved()) {
			for(int curCol = columnCheckStart; curCol < columnCheckEnd; curCol++) {
				if(board.getPieceAtSpot(myRow, curCol) != null || (illegalMoves.containsKey(myRow)
						&& illegalMoves.get(myRow).contains(curCol))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
