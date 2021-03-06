package gameplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;
import gamesetup.*;
import pieces.*;

/**
 * ChessFrame can be used to view/control a game of chess via a GUI
 * @author Yael Goldin
 */
@SuppressWarnings("serial")
public class ChessFrame extends JFrame {
	private ChessBoard chessBoard;
	private ChessSpot[][] chessSpots;
	private final PieceIcons icons;
	private int[] pieceToMove;
	
	/**
	 * initializes the GUI frame
	 */
	public ChessFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1024, 768));
		setTitle("Chess");
		
		chessBoard = new ChessBoard();
		
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
	
	//sets up the icons of the initial spots with pieces
	private void setupInitialIcons(boolean white) {
		Map<ChessPiece, Integer[]> teamPieces = chessBoard.getAllPieces(white);
		for(ChessPiece piece : teamPieces.keySet()) {
			Integer[] location = teamPieces.get(piece);
			ChessSpot spot = chessSpots[location[0]][location[1]]; 
			spot.updateIcon(icons.getPieceIcon(piece));
		}
	}
	
	//performs the end of game actions
	private void gameOverActions() {
		String message;
		if(chessBoard.endedInCheckmate()) {
			message = "Checkmate!";
		} else {
			message = "Stalemate - it's a draw";
		}
		if(JOptionPane.showConfirmDialog(this, message, "Play again?", JOptionPane.YES_NO_OPTION)
				== JOptionPane.YES_OPTION) { //play again
			chessBoard = new ChessBoard();
			for(int r = 0; r < ChessBoard.SIZE; r++) {
				for(int c = 0; c < ChessBoard.SIZE; c++) {
					chessSpots[r][c].updateIcon(null);
				}
			}
			setupInitialIcons(true);
			setupInitialIcons(false);
		}
	}
	
	//this class represents one spot on the chess board
	private class ChessSpot extends JButton {
		private final Color WHITE_SQUARE = Color.WHITE;
		private final Color BLACK_SQUARE = new Color(139,69,19);
		private Image pieceImage;
		
		/**
		 * constructs the given spot on the board
		 * 
		 * @param row The row of the spot
		 * @param col The column of the spot
		 */
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
								chessSpots[takenLocation[0]][takenLocation[1]].updateIcon(null);
							}
							chessSpots[pieceToMove[0]][pieceToMove[1]].updateIcon(null);
							//pawn promotion
							if(movingPiece instanceof Pawn && (row == 0 || row == ChessBoard.SIZE - 1)) {
								movingPiece = chessBoard.getPieceAtSpot(row, col);
							}
							ImageIcon pieceIcon = icons.getPieceIcon(movingPiece);
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
		
		//moves the rook if castling occurred
		private void moveCastleRook(int row, int oldRookCol, int newRookCol) {
			chessSpots[row][oldRookCol].updateIcon(null);
			ImageIcon rookIcon = icons.getPieceIcon(chessBoard.getPieceAtSpot(row, newRookCol));
			chessSpots[row][newRookCol].updateIcon(rookIcon);
		}
		
		/**
		 * updates the icon of the spot
		 */
		public void updateIcon(ImageIcon image) {
			if(image == null) {
				pieceImage = null;
			} else {
				pieceImage = image.getImage();
			}
			repaint();
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if(pieceImage == null) {
				g.drawImage(null, 0, 0, null);
			} else {
				int min = Math.min(getWidth(), getHeight());
				int xLocation = (getWidth() - min)/2;
				int yLocation = (getHeight() - min)/2;
				g.drawImage(pieceImage, xLocation, yLocation, min, min, ChessFrame.this);
			}
			
		}
	}
}
