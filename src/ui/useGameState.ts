import { ChessBoard } from '../sdk';
import { useChessEventListener } from './useChessEventListener';

export const useGameState = (chessBoard: ChessBoard) => {
    useChessEventListener(chessBoard, 'check');
    useChessEventListener(chessBoard, 'checkmate');
    useChessEventListener(chessBoard, 'stalemate');

    return chessBoard.getGameState();
};
