import { useCallback } from 'react';
import { ChessBoard } from '../sdk';
import { useChessEventListener } from './useChessEventListener';
import './Notation.css';

export type NotationProps = Readonly<{
    chessBoard: ChessBoard;
}>;

export const Notation = ({ chessBoard }: NotationProps) => {
    useChessEventListener(chessBoard, 'turnChanged');
    const notation = chessBoard.getNotation();

    const renderNotation = useCallback(() => {
        const result = new Array<JSX.Element>();

        result.push(
            <div className={'notationRow'} key={'teamRow'}>
                <div className={'notationValue'}>White</div>
                <div className={'notationValue'}>Black</div>
            </div>,
        );
        for (let i = 0; i < notation.length; i += 2) {
            const whiteMove = notation[i];
            const blackMove = i === notation.length - 1 ? undefined : notation[i + 1];
            result.push(
                <div className={'notationRow'} key={i}>
                    <div className={'notationValue'}>{whiteMove}</div>
                    <div className={'notationValue'}>{blackMove}</div>
                </div>,
            );
        }

        return result;
    }, [notation]);

    if (notation.length === 0) {
        return null;
    }

    return <>{renderNotation()}</>;
};
