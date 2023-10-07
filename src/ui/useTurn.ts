import { ChessBoard } from '../sdk';
import { useChessEventListener } from './useChessEventListener';

export const useTurn = (chessBoard: ChessBoard) => {
    useChessEventListener(chessBoard, 'turnChanged');

    return chessBoard.getTurn();
};
