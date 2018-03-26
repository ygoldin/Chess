package pieces;

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
		return null;
	}
}
