package gameplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;
import gamesetup.ChessBoard;

@SuppressWarnings("serial")
public class ChessFrame extends JFrame {
	private ChessBoard chessBoard;
	private Scanner input;
	private ChessSpot[][] chessSpots;
	private PieceIcons icons;
	
	public ChessFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1024, 768));
		setTitle("Chess");
		
		input = new Scanner(System.in);
		chessBoard = new ChessBoard(input);
		icons = new PieceIcons();
		
		chessSpots = new ChessSpot[ChessBoard.SIZE][ChessBoard.SIZE];
		JPanel spots = new JPanel();
		spots.setLayout(new GridLayout(ChessBoard.SIZE, ChessBoard.SIZE));
		for(int r = 0; r < ChessBoard.SIZE; r++) {
			for(int c = 0; c < ChessBoard.SIZE; c++) {
				chessSpots[r][c] = new ChessSpot(r, c);
				spots.add(chessSpots[r][c]);
			}
		}
		add(spots);
	}
	
	private class ChessSpot extends JButton {
		private final Color WHITE_SQUARE = Color.WHITE;
		private final Color BLACK_SQUARE = Color.BLACK;
		
		public ChessSpot(int row, int col) {
			if((row + col) % 2 == 0) {
				setBackground(WHITE_SQUARE);
			} else {
				setBackground(BLACK_SQUARE);
			}
		}
	}
	
	private class PieceIcons {
		public final Map<Boolean, Map<String, ImageIcon>> icons;
		
		public PieceIcons() {
			icons = new HashMap<>();
			String[] colors = {"white", "black"};
			String[] pieces = {"Bishop", "King", "Knight", "Pawn", "Queen", "Rook"};
			icons.put(true, loadIcons(colors[0], pieces));
			icons.put(false, loadIcons(colors[1], pieces));
		}
		
		private Map<String, ImageIcon> loadIcons(String color, String[] pieces) {
			Map<String, ImageIcon> team = new HashMap<>();
			for(String piece : pieces) {
				String filename = color + "_" + piece.toLowerCase();
				team.put(piece, new ImageIcon(getClass().getResource("/images/" + filename + ".png")));
			}
			return team;
		}
	}
}
