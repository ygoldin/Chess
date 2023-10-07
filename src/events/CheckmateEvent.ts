export class CheckmateEvent extends Event {
    public static readonly type = 'checkmate';

    public constructor() {
        super(CheckmateEvent.type);
    }
}
