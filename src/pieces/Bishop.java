package pieces;

public class Bishop implements ChessPiece {
	private static final int VALUE = 3;
	
	@Override
	public int getValue() {
		return VALUE;
	}

	@Override
	public String getName() {
		return "bishop";
	}
}
