import { ChessBoard, GameState } from '../ChessBoard';
import { ChessPiece, PieceColor } from './ChessPiece';
import { PieceMove } from '../PieceMove';
import { Position } from '../Position';
import { Rook } from './Rook';
import { kingEvalBlack, kingEvalWhite } from '../boardEvaluation';

export class King implements ChessPiece {
    public static readonly VALUE = 900;
    public static readonly SYMBOL = 'K';
    private numTimesMoved = 0;

    constructor(private readonly color: PieceColor) {}

    public getValue = (row: number, column: number) => {
        if (this.color === PieceColor.White) {
            return King.VALUE + kingEvalWhite[row][column];
        } else {
            return -1 * (King.VALUE + kingEvalBlack[row][column]);
        }
    };

    public getSymbol = () => King.SYMBOL;

    public getColor = () => this.color;

    public isSameTeam = (otherPiece: ChessPiece) => this.color === otherPiece.getColor();

    public increaseNumTimesMoved = () => {
        this.numTimesMoved += 1;
    };

    public decreaseNumTimesMoved = () => {
        this.numTimesMoved -= 1;
    };

    public getNumTimesMoved = () => this.numTimesMoved;

    public getPossibleMoves = (board: ChessBoard, myRow: number, myColumn: number) => {
        const moves = new Set<PieceMove>();

        const possiblePositionChanges = new Array<Position>();
        possiblePositionChanges.push(new Position(1, -1));
        possiblePositionChanges.push(new Position(1, 0));
        possiblePositionChanges.push(new Position(1, 1));
        possiblePositionChanges.push(new Position(0, -1));
        possiblePositionChanges.push(new Position(0, 1));
        possiblePositionChanges.push(new Position(-1, -1));
        possiblePositionChanges.push(new Position(-1, 0));
        possiblePositionChanges.push(new Position(-1, 1));

        for (const possiblePositionChange of possiblePositionChanges) {
            const newRow = myRow + possiblePositionChange.row;
            const newColumn = myColumn + possiblePositionChange.column;
            if (board.isValidPosition(newRow, newColumn)) {
                const pieceAtNewPosition = board.getPiece(newRow, newColumn);
                if (!pieceAtNewPosition) {
                    if (!this.isNewPositionNextToOtherKing(newRow, newColumn, board, possiblePositionChanges)) {
                        moves.add(new PieceMove(newRow, newColumn, false /* isTake */));
                    }
                } else if (
                    !this.isSameTeam(pieceAtNewPosition) &&
                    !this.isNewPositionNextToOtherKing(newRow, newColumn, board, possiblePositionChanges)
                ) {
                    moves.add(new PieceMove(newRow, newColumn, true /* isTake */));
                }
            }
        }

        // Castling
        if (this.numTimesMoved === 0 && board.getGameState() !== GameState.Check) {
            for (const rookColumn of [0, ChessBoard.SIZE - 1]) {
                const rook = board.getPiece(myRow, rookColumn);
                if (rook && rook instanceof Rook && rook.getNumTimesMoved() === 0) {
                    if (!this.doExistPiecesBetweenMeAndRook(myRow, myColumn, rookColumn, board)) {
                        // No pieces between king and rook, can castle
                        // We will check that we don't pass through check inside of ChessBoard
                        moves.add(new PieceMove(myRow, rookColumn < myColumn ? myColumn - 2 : myColumn + 2, false));
                    }
                }
            }
        }

        return moves;
    };

    private doExistPiecesBetweenMeAndRook(
        myRow: number,
        myColumn: number,
        rookColumn: number,
        board: ChessBoard,
    ): boolean {
        for (let column = Math.min(rookColumn, myColumn) + 1; column < Math.max(rookColumn, myColumn); column++) {
            if (board.getPiece(myRow, column)) {
                return true;
            }
        }

        return false;
    }

    private isNewPositionNextToOtherKing(
        newRow: number,
        newColumn: number,
        board: ChessBoard,
        possiblePositionChanges: Array<Position>,
    ): boolean {
        for (const possiblePositionChange of possiblePositionChanges) {
            const potentialKingRow = newRow + possiblePositionChange.row;
            const potentialKingColumn = newColumn + possiblePositionChange.column;
            if (board.isValidPosition(potentialKingRow, potentialKingColumn)) {
                const pieceAtNewPosition = board.getPiece(potentialKingRow, potentialKingColumn);
                if (pieceAtNewPosition && !this.isSameTeam(pieceAtNewPosition) && pieceAtNewPosition instanceof King) {
                    return true;
                }
            }
        }

        return false;
    }
}
