package gameplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;
import gamesetup.*;
import pieces.ChessPiece;

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
		
		chessSpots = new ChessSpot[ChessBoard.SIZE][ChessBoard.SIZE];
		JPanel spots = new JPanel();
		spots.setLayout(new GridLayout(ChessBoard.SIZE, ChessBoard.SIZE));
		for(int r = 0; r < ChessBoard.SIZE; r++) {
			for(int c = 0; c < ChessBoard.SIZE; c++) {
				chessSpots[r][c] = new ChessSpot(r, c);
				spots.add(chessSpots[r][c]);
			}
		}
		icons = new PieceIcons();
		add(spots);
		pack();
		setVisible(true);
		setupInitialIcons(true);
		setupInitialIcons(false);
	}
	
	private void setupInitialIcons(boolean white) {
		Map<ChessPiece, Integer[]> teamPieces = chessBoard.getAllPieces(white);
		Map<String, ImageIcon> teamIcons = icons.icons.get(white);
		for(ChessPiece piece : teamPieces.keySet()) {
			Integer[] location = teamPieces.get(piece);
			String name = piece.getClass().getName();
			name = name.substring(name.indexOf(".") + 1);
			ImageIcon image = teamIcons.get(name);
			ChessSpot spot = chessSpots[location[0]][location[1]]; 
			spot.updateIcon(image);
		}
	}
	
	private class ChessSpot extends JButton {
		private final Color WHITE_SQUARE = Color.WHITE;
		private final Color BLACK_SQUARE = new Color(139,69,19);
		
		public ChessSpot(int row, int col) {
			super();
			if((row + col) % 2 == 0) {
				setBackground(WHITE_SQUARE);
			} else {
				setBackground(BLACK_SQUARE);
			}
		}
		
		public void updateIcon(ImageIcon image) {
			int height = getHeight();
			Image scaled = image.getImage().getScaledInstance(-1, height, Image.SCALE_SMOOTH);
			setIcon(new ImageIcon(scaled));
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
