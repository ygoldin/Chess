import { useCallback, useEffect, useMemo, useState } from 'react';
import { ChessBoard, GameState, Pawn, PieceColor } from '../sdk';
import { Position } from '../sdk/Position';
import { PieceMove } from '../sdk/PieceMove';
import './ChessBoardComponent.css';
import { ChessBoardSquare } from './ChessBoardSquare';
import { WelcomeModal } from './WelcomeModal';
import { useGameState } from './useGameState';
import { useTurn } from './useTurn';
import { PromotionSelection, PromotionSelectionProps } from './PromotionSelection';
import { Notation } from './Notation';

export const ChessBoardComponent = () => {
    const [chessBoard, setChessBoard] = useState(new ChessBoard());
    const [pieceSelected, setPieceSelected] = useState<Position | undefined>();
    const [playTypeChosen, setPlayTypeChosen] = useState(false);
    const [playAgainstComputer, setPlayAgainstComputer] = useState(false);
    const [showUndoButton, setShowUndoButton] = useState(false);
    const [showPromotionSelection, setShowPromotionSelection] = useState<PromotionSelectionProps | undefined>();

    const gameState = useGameState(chessBoard);
    const turn = useTurn(chessBoard);

    const promptToPlayAgain = useCallback((isCheckmate: boolean) => {
        const phrase = isCheckmate ? 'Checkmate' : 'Stalemate';
        // eslint-disable-next-line no-restricted-globals
        const result = confirm(`${phrase}! Do you want to play again?`);
        if (result) {
            const newBoard = new ChessBoard();
            setChessBoard(newBoard);
            setPlayTypeChosen(false);
            setPieceSelected(undefined);
        }
    }, []);

    useEffect(() => {
        setTimeout(() => {
            switch (gameState) {
                case GameState.Check:
                    alert('Check!');
                    break;
                case GameState.Checkmate:
                    promptToPlayAgain(true);
                    break;
                case GameState.Stalemate:
                    promptToPlayAgain(false);
                    break;
            }
        }, 50);
    }, [gameState, promptToPlayAgain]);

    useEffect(() => {
        setTimeout(() => {
            if (
                turn === PieceColor.Black &&
                gameState !== GameState.Checkmate &&
                gameState !== GameState.Stalemate &&
                playAgainstComputer
            ) {
                const bestMoveForBlack = chessBoard.getBestMoveForBlack();
                if (!bestMoveForBlack) {
                    throw new Error('Black should have a move');
                }

                chessBoard.makeMove(bestMoveForBlack.pieceRow, bestMoveForBlack.pieceColumn, bestMoveForBlack.move);
            } else if ((!playAgainstComputer || turn === PieceColor.White) && chessBoard.getNumMovesMade() > 0) {
                setShowUndoButton(true);
            } else {
                setShowUndoButton(false);
            }
        }, 50);
    }, [chessBoard, gameState, playAgainstComputer, turn]);

    const legalMoves = useMemo(() => {
        const array = new Array<PieceMove>();
        if (!pieceSelected) {
            return array;
        }

        const set = chessBoard.getLegalMoves().get(pieceSelected.row)?.get(pieceSelected.column);
        if (!set) {
            return array;
        }

        set.forEach((move: PieceMove) => {
            array.push(move);
        });

        return array;
    }, [chessBoard, pieceSelected]);

    const buttonDisabled = useCallback(
        (row: number, column: number) => {
            if (!playTypeChosen) {
                return true;
            } else if (!pieceSelected) {
                return chessBoard.getLegalMoves().get(row)?.get(column) === undefined;
            } else {
                for (const move of legalMoves) {
                    if (move.destinationRow === row && move.destinationColumn === column) {
                        return false; // Move exists, don't disable
                    }
                }

                return true; // No move exists to this location, disable
            }
        },
        [pieceSelected, chessBoard, legalMoves, playTypeChosen],
    );

    const onClick = useCallback(
        (row: number, column: number) => () => {
            if (!pieceSelected) {
                setPieceSelected(new Position(row, column));
                return;
            }

            if (pieceSelected.row === row && pieceSelected.column === column) {
                setPieceSelected(undefined);
                return;
            }

            if (
                (row === 0 || row === ChessBoard.SIZE - 1) &&
                chessBoard.getPiece(pieceSelected.row, pieceSelected.column) instanceof Pawn
            ) {
                let queenMove: PieceMove | undefined;
                let rookMove: PieceMove | undefined;
                let bishopMove: PieceMove | undefined;
                let knightMove: PieceMove | undefined;

                for (const move of legalMoves) {
                    if (move.destinationRow === row && move.destinationColumn === column && move.promotion) {
                        if (move.promotion === 'queen') {
                            queenMove = move;
                        } else if (move.promotion === 'rook') {
                            rookMove = move;
                        } else if (move.promotion === 'bishop') {
                            bishopMove = move;
                        } else {
                            knightMove = move;
                        }
                    }
                }

                if (
                    queenMove !== undefined &&
                    rookMove !== undefined &&
                    bishopMove !== undefined &&
                    knightMove !== undefined
                ) {
                    const props: PromotionSelectionProps = {
                        color: row === 0 ? PieceColor.White : PieceColor.Black,
                        onQueen: () => {
                            chessBoard.makeMove(pieceSelected.row, pieceSelected.column, queenMove!);
                            setPieceSelected(undefined);
                            setShowPromotionSelection(undefined);
                        },
                        onRook: () => {
                            chessBoard.makeMove(pieceSelected.row, pieceSelected.column, rookMove!);
                            setPieceSelected(undefined);
                            setShowPromotionSelection(undefined);
                        },
                        onBishop: () => {
                            chessBoard.makeMove(pieceSelected.row, pieceSelected.column, bishopMove!);
                            setPieceSelected(undefined);
                            setShowPromotionSelection(undefined);
                        },
                        onKnight: () => {
                            chessBoard.makeMove(pieceSelected.row, pieceSelected.column, knightMove!);
                            setPieceSelected(undefined);
                            setShowPromotionSelection(undefined);
                        },
                    };
                    setShowPromotionSelection(props);
                    return;
                }
            }

            for (const move of legalMoves) {
                if (move.destinationRow === row && move.destinationColumn === column) {
                    chessBoard.makeMove(pieceSelected.row, pieceSelected.column, move);
                    setPieceSelected(undefined);
                    return;
                }
            }

            // Should never reach here because buttons should be disabled if no move exists
            return;
        },
        [chessBoard, legalMoves, pieceSelected],
    );

    const onUndo = useCallback(() => {
        chessBoard.undo();
        if (playAgainstComputer) {
            // We previously undid the computer's move, we need to undo our move too
            chessBoard.undo();
        }
    }, [chessBoard, playAgainstComputer]);

    const renderChessBoard = useCallback(() => {
        const result = new Array<JSX.Element>();
        for (let row = 0; row < ChessBoard.SIZE; row++) {
            const rowButtons = new Array<JSX.Element>();
            for (let column = 0; column < ChessBoard.SIZE; column++) {
                const pieceAtPosition = chessBoard.getPiece(row, column);
                const onClickAtPosition = onClick(row, column);
                const squareColor = (row + column) % 2 === 0 ? 'white' : 'brown';
                const disabled = buttonDisabled(row, column);
                rowButtons.push(
                    <ChessBoardSquare
                        key={`${row}-${column}`}
                        piece={pieceAtPosition}
                        onClick={onClickAtPosition}
                        squareColor={squareColor}
                        disabled={disabled}
                        selected={pieceSelected?.row === row && pieceSelected?.column === column}
                        somethingIsSelected={pieceSelected !== undefined}
                    />,
                );
            }
            result.push(
                <div key={row} style={{ display: 'flex', flexDirection: 'row' }}>
                    {rowButtons}
                </div>,
            );
        }

        if (showUndoButton) {
            result.push(
                <div key={'undo'} style={{ display: 'flex', justifyContent: 'center', marginTop: 20 }}>
                    <button onClick={onUndo}>Undo</button>
                </div>,
            );
        }

        return result;
    }, [buttonDisabled, chessBoard, onClick, pieceSelected, showUndoButton, onUndo]);

    const onPlayAgainstComputer = useCallback((playAgainstComputer: boolean) => {
        setPlayAgainstComputer(playAgainstComputer);
        setPlayTypeChosen(true);
    }, []);

    return (
        <div className={'container'}>
            {turn === PieceColor.White ? 'White turn' : 'Black turn'}
            {renderChessBoard()}
            {!playTypeChosen && <WelcomeModal onPlayAgainstComputer={onPlayAgainstComputer} />}
            {showPromotionSelection && <PromotionSelection {...showPromotionSelection} />}
            <Notation chessBoard={chessBoard} />
        </div>
    );
};
