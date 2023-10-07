import { ChessBoard } from '../ChessBoard';
import { PieceMove } from '../PieceMove';

export enum PieceColor {
    White = 'white',
    Black = 'black',
}

export const getOppositeColor = (color: PieceColor) => {
    switch (color) {
        case PieceColor.White:
            return PieceColor.Black;
        default:
            return PieceColor.White;
    }
};

export interface ChessPiece {
    /** returns the numberical value of the piece at that location */
    getValue: (row: number, column: number) => number;
    /** returns a string representation of the piece */
    getSymbol: () => string;
    /** returns what team the piece is on */
    getColor: () => PieceColor;
    /** returns true if the other piece is on the same piece as this team */
    isSameTeam: (otherPiece: ChessPiece) => boolean;
    /** returns all possible moves the piece can take. Does not filter based on preventing check */
    getPossibleMoves: (board: ChessBoard, myRow: number, myColumn: number) => Set<PieceMove>;
}
