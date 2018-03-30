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
import pieces.*;

@SuppressWarnings("serial")
public class ChessFrame extends JFrame {
	private ChessBoard chessBoard;
	private Scanner input;
	private ChessSpot[][] chessSpots;
	private final PieceIcons icons;
	private int[] pieceToMove;
	
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
		for(ChessPiece piece : teamPieces.keySet()) {
			Integer[] location = teamPieces.get(piece);
			ChessSpot spot = chessSpots[location[0]][location[1]]; 
			spot.updateIcon(getPieceIcon(piece));
		}
	}
	
	private ImageIcon getPieceIcon(ChessPiece piece) {
		String name = piece.getClass().getName();
		name = name.substring(name.indexOf(".") + 1);
		return icons.icons.get(piece.isWhite()).get(name);
	}
	
	private void gameOverActions() {
		String message;
		if(chessBoard.endedInCheckmate()) {
			message = "Checkmate!";
		} else {
			message = "Stalemate - it's a draw";
		}
		if(JOptionPane.showConfirmDialog(this, message, "Play again?", JOptionPane.YES_NO_OPTION)
				== JOptionPane.YES_OPTION) { //play again
			chessBoard = new ChessBoard(input);
			for(int r = 0; r < ChessBoard.SIZE; r++) {
				for(int c = 0; c < ChessBoard.SIZE; c++) {
					chessSpots[r][c].setIcon(null);
				}
			}
			setupInitialIcons(true);
			setupInitialIcons(false);
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
			addActionListener(e -> {
				if(!chessBoard.isGameOver()) {
					if(pieceToMove == null) {
						ChessPiece curPiece = chessBoard.getPieceAtSpot(row, col);
						if(curPiece != null && curPiece.isTeamsTurn(chessBoard)) {
							pieceToMove = new int[] {row, col};
						}
					} else {
						ChessPiece movingPiece = chessBoard.getPieceAtSpot(pieceToMove[0], pieceToMove[1]);
						if(chessBoard.validMove(movingPiece, row, col)) {
							Integer[] takenLocation = chessBoard.makeMove(movingPiece, row, col);
							if(takenLocation != null) {
								chessSpots[takenLocation[0]][takenLocation[1]].setIcon(null);
							}
							chessSpots[pieceToMove[0]][pieceToMove[1]].setIcon(null);
							ImageIcon pieceIcon = getPieceIcon(movingPiece);
							chessSpots[row][col].updateIcon(pieceIcon);
							//castling
							if(movingPiece instanceof King && Math.abs(pieceToMove[1] - col) == 2) {
								if(col > pieceToMove[1]) { //right rook
									moveCastleRook(row, ChessBoard.SIZE - 1, pieceToMove[1] + 1);
								} else { //left rook
									moveCastleRook(row, 0, pieceToMove[1] - 1);
								}
							}
						}
						pieceToMove = null;
					}
					if(chessBoard.isGameOver()) {
						gameOverActions();
					}
				}
			});
		}
		
		private void moveCastleRook(int row, int oldRookCol, int newRookCol) {
			chessSpots[row][oldRookCol].setIcon(null);
			ImageIcon rookIcon = getPieceIcon(chessBoard.getPieceAtSpot(row, newRookCol));
			chessSpots[row][newRookCol].updateIcon(rookIcon);
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
