import { ChessPiece } from './pieces';
import { PieceMove } from './PieceMove';

export class FullMove {
    constructor(
        public readonly pieceRow: number,
        public readonly pieceColumn: number,
        public readonly move: PieceMove,
        public readonly causesCheck: boolean,
        public readonly takenPiece?: ChessPiece,
    ) {}
}
