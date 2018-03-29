package gameplay;

import java.util.Scanner;
import javax.swing.*;
import gamesetup.ChessBoard;

public class ChessFrame extends JFrame {
	private ChessBoard chessBoard;
	private Scanner input;
	
	public ChessFrame() {
		input = new Scanner(System.in);
		chessBoard = new ChessBoard(input);
	}
}
