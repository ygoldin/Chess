import whitePawn from './assets/white_pawn.png';
import blackPawn from './assets/black_pawn.png';
import whiteRook from './assets/white_rook.png';
import blackRook from './assets/black_rook.png';
import whiteKnight from './assets/white_knight.png';
import blackKnight from './assets/black_knight.png';
import whiteBishop from './assets/white_bishop.png';
import blackBishop from './assets/black_bishop.png';
import whiteQueen from './assets/white_queen.png';
import blackQueen from './assets/black_queen.png';
import whiteKing from './assets/white_king.png';
import blackKing from './assets/black_king.png';
import { Bishop, ChessPiece, Knight, Pawn, PieceColor, Queen, Rook } from '../sdk';

export type ChessBoardSquareProps = Readonly<{
    piece?: ChessPiece;
    onClick: () => void;
    squareColor: 'brown' | 'white';
    disabled: boolean;
    selected: boolean;
    somethingIsSelected: boolean;
}>;

export const ChessBoardSquare = ({
    piece,
    onClick,
    squareColor,
    disabled,
    selected,
    somethingIsSelected,
}: ChessBoardSquareProps) => {
    const imageAndAlt = getPieceImageAndAlt(piece);
    const backgroundColor = selected ? 'yellow' : !disabled && somethingIsSelected ? 'teal' : squareColor;
    return (
        <button
            onClick={onClick}
            style={{ backgroundColor }}
            className={'buttonContainer'}
            disabled={disabled && !selected}
        >
            {imageAndAlt && <img src={imageAndAlt[0]} alt={imageAndAlt[1]} className={'square'} />}
        </button>
    );
};

const getPieceImageAndAlt = (piece?: ChessPiece): readonly string[] | undefined => {
    if (!piece) {
        return undefined;
    }

    if (piece instanceof Pawn) {
        if (piece.getColor() === PieceColor.White) {
            return [whitePawn, 'White pawn'] as const;
        } else {
            return [blackPawn, 'Black pawn'] as const;
        }
    } else if (piece instanceof Rook) {
        if (piece.getColor() === PieceColor.White) {
            return [whiteRook, 'White rook'] as const;
        } else {
            return [blackRook, 'Black rook'] as const;
        }
    } else if (piece instanceof Knight) {
        if (piece.getColor() === PieceColor.White) {
            return [whiteKnight, 'White knight'] as const;
        } else {
            return [blackKnight, 'Black knight'] as const;
        }
    } else if (piece instanceof Bishop) {
        if (piece.getColor() === PieceColor.White) {
            return [whiteBishop, 'White bishop'] as const;
        } else {
            return [blackBishop, 'Black bishop'];
        }
    } else if (piece instanceof Queen) {
        if (piece.getColor() === PieceColor.White) {
            return [whiteQueen, 'White queen'] as const;
        } else {
            return [blackQueen, 'Black queen'] as const;
        }
    } else {
        if (piece.getColor() === PieceColor.White) {
            return [whiteKing, 'White king'] as const;
        } else {
            return [blackKing, 'Black king'] as const;
        }
    }
};
