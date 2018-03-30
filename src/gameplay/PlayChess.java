package gameplay;

import java.awt.EventQueue;
import java.util.Scanner;

import gamesetup.*;
import pieces.ChessPiece;

public class PlayChess {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ChessFrame();
			}
		});
	}

}
