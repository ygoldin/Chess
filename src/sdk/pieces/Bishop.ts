import { ChessBoard } from '../ChessBoard';
import { ChessPiece, PieceColor } from './ChessPiece';
import { PieceMove } from '../PieceMove';
import { bishopEvalBlack, bishopEvalWhite } from '../boardEvaluation';

export class Bishop implements ChessPiece {
    public static readonly VALUE = 30;
    public static readonly SYMBOL = 'B';

    constructor(private readonly color: PieceColor) {}

    public getValue = (row: number, column: number) => {
        if (this.color === PieceColor.White) {
            return Bishop.VALUE + bishopEvalWhite[row][column];
        } else {
            return -1 * (Bishop.VALUE + bishopEvalBlack[row][column]);
        }
    };

    public getSymbol = () => Bishop.SYMBOL;

    public getColor = () => this.color;

    public isSameTeam = (otherPiece: ChessPiece) => this.color === otherPiece.getColor();

    public getPossibleMoves = (board: ChessBoard, myRow: number, myColumn: number) => {
        const moves = new Set<PieceMove>();

        for (const rowChange of [-1, 1]) {
            for (const columnChange of [-1, 1]) {
                for (let spotsAway = 1; spotsAway < ChessBoard.SIZE; spotsAway++) {
                    const newRow = myRow + rowChange * spotsAway;
                    const newColumn = myColumn + columnChange * spotsAway;
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
            }
        }

        return moves;
    };
}
