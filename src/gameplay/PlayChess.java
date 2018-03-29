package gameplay;

import java.util.Scanner;

import gamesetup.*;
import pieces.ChessPiece;

public class PlayChess {

	public static void main(String[] args) {
		ChessBoard chessBoard = new ChessBoard();
		System.out.println(chessBoard);
		System.out.println();
		Scanner input = new Scanner(System.in);
		while(!chessBoard.isGameOver()) {
			System.out.print("Where do you want to move a piece from? ");
			int row = input.nextInt();
			int col = input.nextInt();
			ChessPiece piece = chessBoard.getPieceAtSpot(row, col);
			System.out.print("Where do you want to move it to? ");
			row = input.nextInt();
			col = input.nextInt();
			chessBoard.makeMove(piece, row, col);
			System.out.println(chessBoard);
		}
		input.close();
	}

}
