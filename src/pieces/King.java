package pieces;

public class King implements ChessPiece {
	private static final int VALUE = 1;
	
	@Override
	public int getValue() {
		return VALUE;
	}

	@Override
	public String getName() {
		return "king";
	}
}
