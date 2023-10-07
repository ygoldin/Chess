export class StalemateEvent extends Event {
    public static readonly type = 'stalemate';

    public constructor() {
        super(StalemateEvent.type);
    }
}
