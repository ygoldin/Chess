package gameplay;

import java.awt.EventQueue;
import java.util.Scanner;

import gamesetup.*;
import pieces.ChessPiece;

/**
 * PlayChess can be used to play an interactive game of chess via a GUI
 * @author Yael Goldin
 */
public class PlayChess {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ChessFrame();
			}
		});
	}

}
