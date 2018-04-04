package gameplay;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import gamesetup.ChessBoard;
import pieces.ChessPiece;

@SuppressWarnings("serial")
public class TakenPanel extends JPanel {
	private List<TakenPieceComponent> teamPieces;
	private final boolean isWhite;
	private final PieceIcons icons;
	private static final int MOST_AMOUNT_TAKEN = ChessBoard.SIZE*2 - 1;
	
	public TakenPanel(boolean isWhite, PieceIcons icons) {
		teamPieces = new ArrayList<>(MOST_AMOUNT_TAKEN);
		this.isWhite = isWhite;
		this.icons = icons;
		setLayout(new GridLayout(1, MOST_AMOUNT_TAKEN));
	}
	
	public void addPiece(ChessPiece piece) {
		if(piece.isWhite() == isWhite && !isFull()) {
			TakenPieceComponent newTakenPiece = new TakenPieceComponent(icons.getPieceIcon(piece));
			teamPieces.add(newTakenPiece);
			add(newTakenPiece);
		}
	}
	
	public boolean isFull() {
		return teamPieces.size() == MOST_AMOUNT_TAKEN;
	}
	
	private class TakenPieceComponent extends JComponent {
		private Image pieceImage;
		
		public TakenPieceComponent(ImageIcon image) {
			if(image == null) {
				pieceImage = null;
			} else {
				pieceImage = image.getImage();
			}
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
				g.drawImage(pieceImage, xLocation, yLocation, min, min, TakenPanel.this);
			}
		}
	}
}
