package pieces;

public class Queen implements ChessPiece {
	private static final int VALUE = 8;
	
	@Override
	public int getValue() {
		return VALUE;
	}

	@Override
	public String getName() {
		return "queen";
	}
}
