package pieces;

public class Knight implements ChessPiece {
	private static final int VALUE = 3;
	
	@Override
	public int getValue() {
		return VALUE;
	}

	@Override
	public String getName() {
		return "knight";
	}
}
