import { ChessBoard } from '../ChessBoard';
import { ChessPiece, PieceColor } from './ChessPiece';
import { PieceMove } from '../PieceMove';
import { Position } from '../Position';
import { pawnEvalBlack, pawnEvalWhite } from '../boardEvaluation';

export class Pawn implements ChessPiece {
    private isOpenToEnPassant = false;
    public static readonly VALUE = 10;
    public static readonly SYMBOL = '';

    constructor(private readonly color: PieceColor) {}

    public getValue = (row: number, column: number) => {
        if (this.color === PieceColor.White) {
            return Pawn.VALUE + pawnEvalWhite[row][column];
        } else {
            return -1 * (Pawn.VALUE + pawnEvalBlack[row][column]);
        }
    };

    public getSymbol = () => Pawn.SYMBOL;

    public getColor = () => this.color;

    public isSameTeam = (otherPiece: ChessPiece) => this.color === otherPiece.getColor();

    public getIsOpenToEnPassant = () => {
        return this.isOpenToEnPassant;
    };

    public setIsOpenToEnPassant = (isOpen: boolean) => {
        this.isOpenToEnPassant = isOpen;
    };

    public getPossibleMoves = (board: ChessBoard, myRow: number, myColumn: number) => {
        const moves = new Set<PieceMove>();
        const rowDifference = this.color === PieceColor.White ? -1 : 1; // how the row value changes when this pawn moves forward

        // Move one spot forward
        let newRow = myRow + rowDifference;
        let newColumn = myColumn;
        const oneSpotForwardIsValid = board.isValidPosition(newRow, newColumn) && !board.getPiece(newRow, newColumn);
        if (oneSpotForwardIsValid) {
            if (newRow > 0 && newRow < ChessBoard.SIZE - 1) {
                // Normal move forward
                moves.add(new PieceMove(newRow, newColumn, false /* isTake */));
            } else {
                // Promotion
                moves.add(new PieceMove(newRow, newColumn, false /* isTake */, undefined /* enPassant */, 'rook'));
                moves.add(new PieceMove(newRow, newColumn, false /* isTake */, undefined /* enPassant */, 'queen'));
                moves.add(new PieceMove(newRow, newColumn, false /* isTake */, undefined /* enPassant */, 'bishop'));
                moves.add(new PieceMove(newRow, newColumn, false /* isTake */, undefined /* enPassant */, 'knight'));
            }
        }

        // Move two spots forward
        newRow += rowDifference;
        if (
            oneSpotForwardIsValid &&
            board.isValidPosition(newRow, newColumn) &&
            !board.getPiece(newRow, newColumn) &&
            (myRow === 1 || myRow === ChessBoard.SIZE - 2)
        ) {
            moves.add(new PieceMove(newRow, newColumn, false /* isTake */));
        }

        // Take a piece diagonally left
        newRow = myRow + rowDifference;
        newColumn = myColumn - 1;
        if (board.isValidPosition(newRow, newColumn)) {
            const pieceDiagonalLeft = board.getPiece(newRow, newColumn);
            if (pieceDiagonalLeft && !this.isSameTeam(pieceDiagonalLeft)) {
                if (newRow > 0 && newRow < ChessBoard.SIZE - 1) {
                    // Normal move forward
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */));
                } else {
                    // Promotion
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */, undefined /* enPassant */, 'rook'));
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */, undefined /* enPassant */, 'queen'));
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */, undefined /* enPassant */, 'bishop'));
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */, undefined /* enPassant */, 'knight'));
                }
            }
        }

        // Take a piece diagonally right
        newColumn = myColumn + 1;
        if (board.isValidPosition(newRow, newColumn)) {
            const pieceDiagonalRight = board.getPiece(newRow, newColumn);
            if (pieceDiagonalRight && !this.isSameTeam(pieceDiagonalRight)) {
                if (newRow > 0 && newRow < ChessBoard.SIZE - 1) {
                    // Normal move forward
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */));
                } else {
                    // Promotion
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */, undefined /* enPassant */, 'rook'));
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */, undefined /* enPassant */, 'queen'));
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */, undefined /* enPassant */, 'bishop'));
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */, undefined /* enPassant */, 'knight'));
                }
            }
        }

        // En passant
        if (
            (this.color === PieceColor.White && myRow === 3) ||
            (this.color === PieceColor.Black && myRow === ChessBoard.SIZE - 4)
        ) {
            newRow = this.color === PieceColor.White ? myRow - 1 : myRow + 1;
            for (const columnChange of [-1, 1]) {
                newColumn = myColumn + columnChange;
                if (board.isValidPosition(myRow, newColumn)) {
                    const piece = board.getPiece(myRow, newColumn);
                    if (piece && piece instanceof Pawn && piece.getIsOpenToEnPassant()) {
                        moves.add(
                            new PieceMove(
                                newRow,
                                newColumn,
                                true /* isTake */,
                                new Position(myRow, newColumn) /* enPassantTakenPosition */,
                            ),
                        );
                    }
                }
            }
        }

        return moves;
    };
}
