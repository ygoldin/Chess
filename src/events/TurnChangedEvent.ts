import { PieceColor } from '../sdk';

export class TurnChangedEvent extends Event {
    public static readonly type = 'turnChanged';

    public constructor(public whoseMove: PieceColor) {
        super(TurnChangedEvent.type);
    }
}
