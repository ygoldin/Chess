package gameplay;

import java.awt.EventQueue;
import java.util.Scanner;

import gamesetup.*;
import pieces.ChessPiece;

public class PlayChess {

	public static void main(String[] args) {
//		Scanner input = new Scanner(System.in);
//		ChessBoard chessBoard = new ChessBoard(input);
//		System.out.println(chessBoard);
//		System.out.println();
//		while(!chessBoard.isGameOver()) {
//			System.out.print("Where do you want to move a piece from? ");
//			int row = input.nextInt();
//			int col = input.nextInt();
//			ChessPiece piece = chessBoard.getPieceAtSpot(row, col);
//			System.out.print("Where do you want to move it to? ");
//			row = input.nextInt();
//			col = input.nextInt();
//			chessBoard.makeMove(piece, row, col);
//			System.out.println(chessBoard);
//		}
//		input.close();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ChessFrame();
			}
		});
	}

}
