package gameplay;

import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import pieces.ChessPiece;

/**
 * PieceIcons stores all of the icons of the different piece types
 * @author Yael Goldin
 */
public class PieceIcons {
	public final Map<Boolean, Map<String, ImageIcon>> icons;
	
	/**
	 * initializes all of the icons
	 */
	public PieceIcons() {
		icons = new HashMap<>();
		String[] colors = {"white", "black"};
		String[] pieces = {"Bishop", "King", "Knight", "Pawn", "Queen", "Rook"};
		icons.put(true, loadIcons(colors[0], pieces));
		icons.put(false, loadIcons(colors[1], pieces));
	}
		
	//loads the icons of the given team
	private Map<String, ImageIcon> loadIcons(String color, String[] pieces) {
		Map<String, ImageIcon> team = new HashMap<>();
		for(String piece : pieces) {
			String filename = color + "_" + piece.toLowerCase();
			team.put(piece, new ImageIcon(getClass().getResource("/images/" + filename + ".png")));
		}
		return team;
	}
	
	/**
	 * gets the icon of a piece
	 */
	public ImageIcon getPieceIcon(ChessPiece piece) {
		String name = piece.getClass().getName();
		name = name.substring(name.indexOf(".") + 1);
		return icons.get(piece.isWhite()).get(name);
	}
}