import { ChessBoard } from '../ChessBoard';
import { ChessPiece, PieceColor } from './ChessPiece';
import { PieceMove } from '../PieceMove';
import { Position } from '../Position';
import { queenEval } from '../boardEvaluation';

export class Queen implements ChessPiece {
    public static readonly VALUE = 90;
    public static readonly SYMBOL = 'Q';

    constructor(private readonly color: PieceColor) {}

    public getValue = (row: number, column: number) =>
        (Queen.VALUE + queenEval[row][column]) * (this.color === PieceColor.White ? 1 : -1);

    public getSymbol = () => Queen.SYMBOL;

    public getColor = () => this.color;

    public isSameTeam = (otherPiece: ChessPiece) => this.color === otherPiece.getColor();

    public getPossibleMoves = (board: ChessBoard, myRow: number, myColumn: number) => {
        const moves = new Set<PieceMove>();

        const possiblePositionChanges = new Set<Position>();
        possiblePositionChanges.add(new Position(1, -1));
        possiblePositionChanges.add(new Position(1, 0));
        possiblePositionChanges.add(new Position(1, 1));
        possiblePositionChanges.add(new Position(0, -1));
        possiblePositionChanges.add(new Position(0, 1));
        possiblePositionChanges.add(new Position(-1, -1));
        possiblePositionChanges.add(new Position(-1, 0));
        possiblePositionChanges.add(new Position(-1, 1));

        possiblePositionChanges.forEach((possiblePositionChange: Position) => {
            for (let spotsAway = 1; spotsAway < ChessBoard.SIZE; spotsAway++) {
                const newRow = myRow + possiblePositionChange.row * spotsAway;
                const newColumn = myColumn + possiblePositionChange.column * spotsAway;
                if (!board.isValidPosition(newRow, newColumn)) {
                    break;
                }
                const pieceAtNewPosition = board.getPiece(newRow, newColumn);
                if (!pieceAtNewPosition) {
                    moves.add(new PieceMove(newRow, newColumn, false /* isTake */));
                } else {
                    if (!this.isSameTeam(pieceAtNewPosition)) {
                        moves.add(new PieceMove(newRow, newColumn, true /* isTake */));
                    }
                    // There is a piece at this location so we can't keep searching in this direction
                    break;
                }
            }
        });

        return moves;
    };
}
