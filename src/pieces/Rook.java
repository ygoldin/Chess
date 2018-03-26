package pieces;

public class Rook implements ChessPiece {
	private static final int VALUE = 5;
	
	@Override
	public int getValue() {
		return VALUE;
	}

	@Override
	public String getName() {
		return "rook";
	}
}
