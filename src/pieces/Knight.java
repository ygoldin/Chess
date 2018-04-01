package pieces;

import java.util.HashSet;
import java.util.Set;

import gamesetup.*;

/**
 * this class represents a knight in a game of chess
 * @author Yael Goldin
 */
public class Knight extends ChessPiece {
	private static final int VALUE = 3;
	private static final String SYMBOL = "N";
	private final boolean isWhite;
	
	/**
	 * constructs a knight of the given team
	 * 
	 * @param isWhite whether the knight is for the white team (true) or black (false)
	 */
	public Knight(boolean isWhite) {
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
	public Set<PieceMove> legalMoves(ChessBoard board) {
		Set<PieceMove> moves = new HashSet<>();
		int[] firstDirection = new int[] {1, -1};
		int[] secondDirection = new int[] {2, -2};
		Integer[] myLocation = board.getSpotOfPiece(this);
		int myRow = myLocation[0];
		int myCol = myLocation[1];
		for(int direction1 : firstDirection) {
			for(int direction2 : secondDirection) {
				checkMove(myRow + direction1, myCol + direction2, board, moves);
				checkMove(myRow + direction2, myCol + direction1, board, moves);
			}
		}
		if(isTeamsTurn(board)) {
			leaveMovesThatStopCheck(moves, board);
		}
		return moves;
	}
	
	//checks if the knight can move to that spot and what it might take
	private void checkMove(int curRow, int curCol, ChessBoard board, Set<PieceMove> moves) {
		if(board.isInBounds(curRow, curCol)) {
			ChessPiece otherPiece = board.getPieceAtSpot(curRow, curCol);
			if(otherPiece == null || !isSameTeam(otherPiece)) {
				moves.add(new PieceMove(curRow, curCol, otherPiece));
			}
		}
	}
}
