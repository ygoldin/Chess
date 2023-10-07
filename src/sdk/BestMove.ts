import { PieceMove } from './PieceMove';

export class BestMove {
    constructor(
        public readonly pieceRow: number,
        public readonly pieceColumn: number,
        public readonly move: PieceMove,
    ) {}
}
