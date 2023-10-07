import { ChessBoard } from '../ChessBoard';
import { ChessPiece, PieceColor } from './ChessPiece';
import { PieceMove } from '../PieceMove';
import { Position } from '../Position';
import { knightEval } from '../boardEvaluation';

export class Knight implements ChessPiece {
    public static readonly VALUE = 30;
    public static readonly SYMBOL = 'N';

    constructor(private readonly color: PieceColor) {}

    public getValue = (row: number, column: number) =>
        (Knight.VALUE + knightEval[row][column]) * (this.color === PieceColor.White ? 1 : -1);

    public getSymbol = () => Knight.SYMBOL;

    public getColor = () => this.color;

    public isSameTeam = (otherPiece: ChessPiece) => this.color === otherPiece.getColor();

    public getPossibleMoves = (board: ChessBoard, myRow: number, myColumn: number) => {
        const moves = new Set<PieceMove>();

        const possiblePositionChanges = new Set<Position>();
        possiblePositionChanges.add(new Position(2, 1));
        possiblePositionChanges.add(new Position(2, -1));
        possiblePositionChanges.add(new Position(-2, 1));
        possiblePositionChanges.add(new Position(-2, -1));
        possiblePositionChanges.add(new Position(1, 2));
        possiblePositionChanges.add(new Position(1, -2));
        possiblePositionChanges.add(new Position(-1, 2));
        possiblePositionChanges.add(new Position(-1, -2));

        possiblePositionChanges.forEach((possiblePositionChange: Position) => {
            const newRow = myRow + possiblePositionChange.row;
            const newColumn = myColumn + possiblePositionChange.column;
            if (board.isValidPosition(newRow, newColumn)) {
                const pieceAtNewPosition = board.getPiece(newRow, newColumn);
                if (!pieceAtNewPosition) {
                    moves.add(new PieceMove(newRow, newColumn, false /* isTake */));
                } else if (!this.isSameTeam(pieceAtNewPosition)) {
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */));
                }
            }
        });

        return moves;
    };
}
