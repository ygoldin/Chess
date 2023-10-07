// Copyright (c) Microsoft Corporation. All rights reserved.

import { useCallback, useEffect, useState } from 'react';
import { ChessEventTypes } from '../events';
import { ChessBoard } from '../sdk';

export function useChessEventListener<TEvent extends Event>(
    chessBoard: ChessBoard,
    eventType: ChessEventTypes,
    callback?: (event: TEvent) => void,
): [TEvent | undefined, () => void] {
    const [event, reset] = useEventListener(chessBoard, eventType, callback);

    return [event, reset];
}

function useEventListener<TEvent extends Event>(
    emitter: Pick<EventTarget, 'addEventListener' | 'removeEventListener'> | undefined,
    type: string,
    callback?: (event: TEvent) => void,
): [TEvent | undefined, () => void] {
    const [event, setEvent] = useState<TEvent>();

    useEffect(() => {
        function eventHandler(nextEvent: TEvent) {
            if (callback != null) {
                callback(nextEvent);
            }

            setEvent(nextEvent);
        }

        emitter?.addEventListener(type, eventHandler as EventListener);

        return () => {
            emitter?.removeEventListener(type, eventHandler as EventListener);
        };
    }, [emitter, type, callback]);

    const reset = useCallback(() => {
        setEvent(undefined);
    }, []);

    return [event, reset];
}
