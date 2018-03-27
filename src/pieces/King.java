package pieces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gamesetup.*;

public class King implements ChessPiece {
	private static final int VALUE = 0;
	private final boolean isWhite;
	
	public King(boolean isWhite) {
		this.isWhite = isWhite;
	}
	
	@Override
	public int getValue() {
		return VALUE;
	}

	@Override
	public boolean isWhite() {
		return isWhite;
	}
	
	@Override
	public String toString() {
		String type = "king";
		if(isWhite) {
			return "w " + type;
		} else {
			return "b " + type;
		}
	}

	@Override
	public Set<PieceMove> legalMoves(ChessBoard board) {
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
					if(otherPiece == null || otherPiece.isWhite() != isWhite) {
						moves.add(new PieceMove(curRow, curCol, otherPiece));
					}
				}
			}
		}
		return moves;
	}
	
	//returns a map from rows to columns of moves that would put the king in a line of fire
	private Map<Integer, Set<Integer>> movesIntoALineOfFire(ChessBoard board) {
		Map<ChessPiece, Integer[]> otherTeam = board.getAllPieces(!isWhite);
		Map<Integer, Set<Integer>> result = new HashMap<>();
		for(ChessPiece opposingPiece : otherTeam.keySet()) {
			if(!(opposingPiece instanceof Pawn)) {
				for(PieceMove move : opposingPiece.legalMoves(board)){
					if(!result.containsKey(move.destinationRow)) {
						result.put(move.destinationRow, new HashSet<Integer>());
					}
					result.get(move.destinationRow).add(move.destinationColumn);
				}
			} else {
				//only diagonal "taking" is the line of fire for pawns
				int direction;
				if(isWhite) { //pawn is black
					direction = 1;
				} else { //pawn is white
					direction = -1;
				}
				Integer[] pawnLocation = board.getSpotOfPiece(opposingPiece);
				int pawnAttackRow = pawnLocation[0] + direction;
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
}
