import { Position } from './Position';

export class PieceMove {
    constructor(
        public readonly destinationRow: number,
        public readonly destinationColumn: number,
        public readonly isTake: boolean,
        public readonly enPassantTakenPosition?: Position,
        public readonly promotion?: 'rook' | 'queen' | 'knight' | 'bishop',
    ) {}
}
