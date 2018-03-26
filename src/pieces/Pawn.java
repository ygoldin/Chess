package pieces;

public class Pawn implements ChessPiece {
	private static final int VALUE = 1;
	
	@Override
	public int getValue() {
		return VALUE;
	}

	@Override
	public String getName() {
		return "pawn";
	}

}
