import { CheckEvent } from './CheckEvent';
import { CheckmateEvent } from './CheckmateEvent';
import { StalemateEvent } from './StalemateEvent';
import { TurnChangedEvent } from './TurnChangedEvent';

export type ChessEvents = CheckEvent | CheckmateEvent | StalemateEvent | TurnChangedEvent;

export type ChessEventTypes =
    | (typeof CheckEvent)['type']
    | (typeof CheckmateEvent)['type']
    | (typeof StalemateEvent)['type']
    | (typeof TurnChangedEvent)['type'];
