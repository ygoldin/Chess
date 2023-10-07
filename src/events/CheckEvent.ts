export class CheckEvent extends Event {
    public static readonly type = 'check';

    public constructor() {
        super(CheckEvent.type);
    }
}
