import { FullMove } from './FullMove';
import { ChessError, ChessErrorCode } from './ChessError';
import { Bishop, ChessPiece, King, Knight, Pawn, PieceColor, Queen, Rook, getOppositeColor } from './pieces';
import { PieceMove } from './PieceMove';
import { Position } from './Position';
import { TurnChangedEvent } from '../events/TurnChangedEvent';
import { CheckEvent } from '../events/CheckEvent';
import { CheckmateEvent } from '../events/CheckmateEvent';
import { StalemateEvent } from '../events/StalemateEvent';
import { ChessEventTypes } from '../events';
import { BestMove } from './BestMove';

export enum GameState {
    Ongoing = 'ongoing',
    Check = 'check',
    Checkmate = 'checkmate',
    Stalemate = 'stalemate',
}

export class ChessBoard {
    public static SIZE = 8;
    public static COLUMN_SYMBOLS = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
    private board: Array<Array<ChessPiece | undefined>>;
    private whiteKing: Position;
    private blackKing: Position;
    private turn: PieceColor;
    private currentLegalMoves: Map<number, Map<number, Set<PieceMove>>>;
    private allMoves: FullMove[];
    private eventTarget: EventTarget;
    private notation: string[];

    constructor() {
        this.board = new Array<Array<ChessPiece>>(ChessBoard.SIZE);
        for (let i = 0; i < ChessBoard.SIZE; i++) {
            this.board[i] = new Array<ChessPiece | undefined>(ChessBoard.SIZE);
        }

        this.turn = PieceColor.White;
        this.initPieces(PieceColor.White);
        this.initPieces(PieceColor.Black);
        this.whiteKing = new Position(ChessBoard.SIZE - 1, 4);
        this.blackKing = new Position(0, 4);

        this.currentLegalMoves = new Map<number, Map<number, Set<PieceMove>>>();
        this.allMoves = new Array<FullMove>();
        this.eventTarget = new EventTarget();
        this.notation = new Array<string>();
        this.calculateLegalMoves();
    }

    public addEventListener = (
        type: ChessEventTypes,
        listener: Parameters<EventTarget['addEventListener']>[1],
        options?: Parameters<EventTarget['addEventListener']>[2],
    ): void => {
        this.eventTarget.addEventListener(type, listener, options);
    };

    public removeEventListener = (
        type: ChessEventTypes,
        listener: Parameters<EventTarget['removeEventListener']>[1],
        options?: Parameters<EventTarget['removeEventListener']>[2],
    ): void => {
        this.eventTarget.removeEventListener(type, listener, options);
    };

    public getGameState(): GameState {
        if (this.currentLegalMoves.size === 0) {
            if (this.isCheck()) {
                return GameState.Checkmate;
            } else {
                return GameState.Stalemate;
            }
        } else {
            if (this.isCheck()) {
                return GameState.Check;
            } else {
                return GameState.Ongoing;
            }
        }
    }

    public getTurn(): PieceColor {
        return this.turn;
    }

    public getNumMovesMade(): number {
        return this.allMoves.length;
    }

    public isValidPosition(row: number, column: number): boolean {
        return row >= 0 && column >= 0 && row < ChessBoard.SIZE && column < ChessBoard.SIZE;
    }

    public getPiece(row: number, column: number): ChessPiece | undefined {
        if (!this.isValidPosition(row, column)) {
            return undefined;
        }
        return this.board[row][column];
    }

    /**
     * @returns a map from row -> column -> legal moves by the piece at that position
     */
    public getLegalMoves(): Map<number, Map<number, Set<PieceMove>>> {
        return this.currentLegalMoves;
    }

    public getNotation(): string[] {
        return this.notation;
    }

    public getBestMoveForBlack() {
        if (this.turn !== PieceColor.Black) {
            throw new Error('not black turn');
        }

        let bestMove: BestMove | undefined;
        let bestValue = -9999;

        const start = Date.now();

        // Need to clone since we change what this.currentLegalMoves is set to when we make a move
        const currentLegalMovesClone = new Map<number, Map<number, Set<PieceMove>>>(this.currentLegalMoves);
        for (let [row, columnMap] of currentLegalMovesClone) {
            for (let [column, moves] of columnMap) {
                for (let move of moves) {
                    const legalMovesToResetTo = new Map<number, Map<number, Set<PieceMove>>>(this.currentLegalMoves);
                    this.makeMoveInternal(row, column, move, false);

                    try {
                        const boardValue = this.minmax(3, PieceColor.White, -10000, 10000);

                        if (boardValue >= bestValue) {
                            bestMove = {
                                pieceRow: row,
                                pieceColumn: column,
                                move,
                            };
                            bestValue = boardValue;
                        }
                    } catch (e) {}

                    this.undoInternal(legalMovesToResetTo);
                }
            }
        }

        const end = Date.now();
        console.log(`time to determine best move: ${end - start}ms`);
        return bestMove;
    }

    private minmax(depth: number, color: PieceColor, alpha: number, beta: number) {
        if (depth === 0) {
            return -1 * this.evaluateBoard();
        }

        if (color === PieceColor.White) {
            let bestValue = 9999;
            let newBeta = beta;

            // Need to clone since we change what this.currentLegalMoves is set to when we make a move
            const currentLegalMovesClone = new Map<number, Map<number, Set<PieceMove>>>(this.currentLegalMoves);
            for (let [row, columnMap] of currentLegalMovesClone) {
                for (let [column, moves] of columnMap) {
                    for (let move of moves) {
                        const legalMovesToResetTo = new Map<number, Map<number, Set<PieceMove>>>(
                            this.currentLegalMoves,
                        );
                        this.makeMoveInternal(row, column, move, false);

                        try {
                            const deeperBestMoveValue = this.minmax(depth - 1, getOppositeColor(color), alpha, newBeta);
                            bestValue = Math.min(deeperBestMoveValue, bestValue);
                        } catch (e) {}

                        this.undoInternal(legalMovesToResetTo);

                        newBeta = Math.min(newBeta, bestValue);
                        if (newBeta <= alpha) {
                            return bestValue;
                        }
                    }
                }
            }

            return bestValue;
        } else {
            let bestValue = -9999;
            let newAlpha = alpha;

            // Need to clone since we change what this.currentLegalMoves is set to when we make a move
            const currentLegalMovesClone = new Map<number, Map<number, Set<PieceMove>>>(this.currentLegalMoves);
            for (let [row, columnMap] of currentLegalMovesClone) {
                for (let [column, moves] of columnMap) {
                    for (let move of moves) {
                        const legalMovesToResetTo = new Map<number, Map<number, Set<PieceMove>>>(
                            this.currentLegalMoves,
                        );
                        this.makeMoveInternal(row, column, move, false);

                        try {
                            const deeperBestMoveValue = this.minmax(depth - 1, getOppositeColor(color), newAlpha, beta);
                            bestValue = Math.max(deeperBestMoveValue, bestValue);
                        } catch (e) {}

                        this.undoInternal(legalMovesToResetTo);

                        newAlpha = Math.max(alpha, bestValue);
                        if (beta <= newAlpha) {
                            return bestValue;
                        }
                    }
                }
            }

            return bestValue;
        }
    }

    private evaluateBoard() {
        let boardValue = 0;
        for (let row = 0; row < ChessBoard.SIZE; row++) {
            for (let column = 0; column < ChessBoard.SIZE; column++) {
                const piece = this.getPiece(row, column);
                if (piece) {
                    boardValue += piece.getValue(row, column);
                }
            }
        }

        if (this.isCheck()) {
            if (this.currentLegalMoves.size === 0) {
                if (this.turn === PieceColor.White) {
                    boardValue -= 2000;
                } else {
                    boardValue += 2000;
                }
            } else {
                if (this.turn === PieceColor.White) {
                    boardValue -= 1000;
                } else {
                    boardValue += 1000;
                }
            }
        }

        return boardValue;
    }

    public makeMove(pieceRow: number, pieceColumn: number, move: PieceMove) {
        this.makeMoveInternal(pieceRow, pieceColumn, move, true);
    }

    private makeMoveInternal(pieceRow: number, pieceColumn: number, move: PieceMove, isForReal: boolean) {
        const piece = this.board[pieceRow][pieceColumn];
        if (!piece || piece.getColor() !== this.turn) {
            throw new ChessError(ChessErrorCode.NoPiece, 'No piece of this color here');
        }

        const columnMap = this.currentLegalMoves.get(pieceRow);
        if (!columnMap) {
            throw new ChessError(ChessErrorCode.NoLegalMoves, 'No moves at this row');
        }

        const possibleMoves = columnMap.get(pieceColumn);
        if (!possibleMoves) {
            throw new ChessError(ChessErrorCode.NoLegalMoves, 'No moves at this position');
        }

        if (!this.moveExists(move, possibleMoves)) {
            throw new ChessError(ChessErrorCode.NotALegalMove, 'This is not a legal move');
        }

        const initialNotation = this.generateMoveNotation(pieceRow, pieceColumn, move, piece);

        const takenPiece = move.isTake
            ? move.enPassantTakenPosition
                ? this.board[move.enPassantTakenPosition.row][move.enPassantTakenPosition.column]
                : this.board[move.destinationRow][move.destinationColumn]
            : undefined;

        this.board[pieceRow][pieceColumn] = undefined;
        this.board[move.destinationRow][move.destinationColumn] = piece;

        if (piece instanceof King) {
            if (this.turn === PieceColor.White) {
                this.whiteKing = new Position(move.destinationRow, move.destinationColumn);
            } else {
                this.blackKing = new Position(move.destinationRow, move.destinationColumn);
            }
            piece.increaseNumTimesMoved();

            if (move.destinationColumn === pieceColumn + 2) {
                // Castled right
                const rookColumn = ChessBoard.SIZE - 1;
                const rook = this.board[move.destinationRow][rookColumn];
                if (!rook || !(rook instanceof Rook)) {
                    throw new ChessError(ChessErrorCode.CastleWithNonExistentRook, 'No rook to the right');
                }
                this.board[move.destinationRow][move.destinationColumn - 1] = rook;
                this.board[move.destinationRow][rookColumn] = undefined;
                rook.increaseNumTimesMoved();
            } else if (move.destinationColumn === pieceColumn - 2) {
                // Castled left
                const rookColumn = 0;
                const rook = this.board[move.destinationRow][rookColumn];
                if (!rook || !(rook instanceof Rook)) {
                    throw new ChessError(ChessErrorCode.CastleWithNonExistentRook, 'No rook to the left');
                }
                this.board[move.destinationRow][move.destinationColumn + 1] = rook;
                this.board[move.destinationRow][rookColumn] = undefined;
                rook.increaseNumTimesMoved();
            }
        } else if (piece instanceof Pawn) {
            if (Math.abs(pieceRow - move.destinationRow) === 2) {
                piece.setIsOpenToEnPassant(true);
            } else {
                piece.setIsOpenToEnPassant(false);
            }

            if (move.enPassantTakenPosition) {
                this.board[move.enPassantTakenPosition.row][move.enPassantTakenPosition.column] = undefined;
            } else if (move.promotion) {
                if (move.promotion === 'rook') {
                    this.board[move.destinationRow][move.destinationColumn] = new Rook(this.turn);
                } else if (move.promotion === 'queen') {
                    this.board[move.destinationRow][move.destinationColumn] = new Queen(this.turn);
                } else if (move.promotion === 'bishop') {
                    this.board[move.destinationRow][move.destinationColumn] = new Bishop(this.turn);
                } else if (move.promotion === 'knight') {
                    this.board[move.destinationRow][move.destinationColumn] = new Knight(this.turn);
                }
            }
        } else if (piece instanceof Rook) {
            piece.increaseNumTimesMoved();
        }

        // Set all other pawns to not be open to en passant
        for (const pawnRow of [3, ChessBoard.SIZE - 4]) {
            for (let column = 0; column < ChessBoard.SIZE; column++) {
                const pieceAtPosition = this.board[pawnRow][column];
                if (
                    pieceAtPosition &&
                    pieceAtPosition instanceof Pawn &&
                    (pawnRow !== move.destinationRow || column !== move.destinationColumn)
                ) {
                    pieceAtPosition.setIsOpenToEnPassant(false);
                }
            }
        }

        this.turn = getOppositeColor(this.turn);

        if (this.doesMoveCauseSelfCheck(0, 0, new PieceMove(0, 0, false))) {
            // Make a fake move that does nothing to see if we are currently in check
            this.allMoves.push(new FullMove(pieceRow, pieceColumn, move, true, takenPiece));
        } else {
            this.allMoves.push(new FullMove(pieceRow, pieceColumn, move, false, takenPiece));
        }

        this.calculateLegalMoves();

        if (isForReal) {
            const gameState = this.getGameState();
            switch (gameState) {
                case GameState.Check:
                    this.notation.push(initialNotation + '+');
                    this.eventTarget.dispatchEvent(new CheckEvent());
                    break;
                case GameState.Checkmate:
                    this.notation.push(initialNotation + '#');
                    this.eventTarget.dispatchEvent(new CheckmateEvent());
                    break;
                case GameState.Stalemate:
                    this.notation.push(initialNotation + '$');
                    this.eventTarget.dispatchEvent(new StalemateEvent());
                    break;
                default:
                    this.notation.push(initialNotation);
                    this.eventTarget.dispatchEvent(new TurnChangedEvent(this.turn));
                    break;
            }
        }
    }

    public undo() {
        this.undoInternal();
    }

    private undoInternal(legalMovesToResetTo?: Map<number, Map<number, Set<PieceMove>>>) {
        if (this.allMoves.length === 0) {
            return;
        }

        const lastMove = this.allMoves[this.allMoves.length - 1];
        const lastMovedPiece = this.board[lastMove.move.destinationRow][lastMove.move.destinationColumn];
        if (!lastMovedPiece) {
            throw new Error('No piece at the destination of the last move');
        }
        this.board[lastMove.move.destinationRow][lastMove.move.destinationColumn] = undefined;
        this.board[lastMove.pieceRow][lastMove.pieceColumn] = lastMovedPiece;

        if (lastMove.takenPiece) {
            if (lastMove.move.enPassantTakenPosition) {
                this.board[lastMove.move.enPassantTakenPosition.row][lastMove.move.enPassantTakenPosition.column] =
                    lastMove.takenPiece;
            } else {
                this.board[lastMove.move.destinationRow][lastMove.move.destinationColumn] = lastMove.takenPiece;
            }
        }

        if (lastMovedPiece instanceof King) {
            if (this.turn === PieceColor.White) {
                // Currently white's turn, so we're undoing black's move
                this.blackKing = new Position(lastMove.pieceRow, lastMove.pieceColumn);
            } else {
                this.whiteKing = new Position(lastMove.pieceRow, lastMove.pieceColumn);
            }
            lastMovedPiece.decreaseNumTimesMoved();

            if (lastMove.move.destinationColumn === lastMove.pieceColumn + 2) {
                // Castled right
                const rookColumn = ChessBoard.SIZE - 1;
                const rook = this.board[lastMove.move.destinationRow][lastMove.move.destinationColumn - 1];
                if (!rook || !(rook instanceof Rook)) {
                    throw new ChessError(
                        ChessErrorCode.CastleWithNonExistentRook,
                        'Performed a castle with no rook to the right',
                    );
                }
                this.board[lastMove.move.destinationRow][lastMove.move.destinationColumn - 1] = undefined;
                this.board[lastMove.move.destinationRow][rookColumn] = rook;
                rook.decreaseNumTimesMoved();
            } else if (lastMove.move.destinationColumn === lastMove.pieceColumn - 2) {
                // Castled left
                const rookColumn = 0;
                const rook = this.board[lastMove.move.destinationRow][lastMove.move.destinationColumn + 1];
                if (!rook || !(rook instanceof Rook)) {
                    throw new ChessError(
                        ChessErrorCode.CastleWithNonExistentRook,
                        'Performed a castle with no rook to the left',
                    );
                }
                this.board[lastMove.move.destinationRow][lastMove.move.destinationColumn + 1] = undefined;
                this.board[lastMove.move.destinationRow][rookColumn] = rook;
                rook.decreaseNumTimesMoved();
            }
        } else if (lastMove.move.promotion) {
            this.board[lastMove.pieceRow][lastMove.pieceColumn] = new Pawn(getOppositeColor(this.turn));
        } else if (lastMovedPiece instanceof Rook) {
            lastMovedPiece.decreaseNumTimesMoved();
        }

        // Check the move before the last move to see if that pawn needs to be set back to open to en passant
        if (this.allMoves.length >= 2) {
            const secondToLastMove = this.allMoves[this.allMoves.length - 2];
            const secondToLastMovedPiece =
                this.board[secondToLastMove.move.destinationRow][secondToLastMove.move.destinationColumn];
            if (!secondToLastMovedPiece) {
                throw new Error('No piece at the destination of the second to last move');
            }

            if (
                secondToLastMovedPiece instanceof Pawn &&
                Math.abs(secondToLastMove.move.destinationRow - secondToLastMove.pieceRow) === 2
            ) {
                secondToLastMovedPiece.setIsOpenToEnPassant(true);
            }
        }

        // Remove this move
        this.allMoves.pop();

        // Change color back
        this.turn = getOppositeColor(this.turn);

        if (legalMovesToResetTo) {
            // Only during minmax
            this.currentLegalMoves = legalMovesToResetTo;
        } else {
            // Recalculate legal moves
            this.calculateLegalMoves();

            this.notation.pop();
            const gameState = this.getGameState();
            // Dispatch any events
            switch (gameState) {
                case GameState.Check:
                    this.eventTarget.dispatchEvent(new CheckEvent());
                    break;
                default:
                    this.eventTarget.dispatchEvent(new TurnChangedEvent(this.turn));
                    break;
            }
        }
    }

    private moveExists(move: PieceMove, legalMoves: Set<PieceMove>): boolean {
        let foundMove = false;
        legalMoves.forEach((legalMove: PieceMove) => {
            if (
                legalMove.destinationRow === move.destinationRow &&
                legalMove.destinationColumn === move.destinationColumn &&
                legalMove.isTake === move.isTake &&
                legalMove.enPassantTakenPosition?.row === move.enPassantTakenPosition?.row &&
                legalMove.enPassantTakenPosition?.column === move.enPassantTakenPosition?.column &&
                legalMove.promotion === move.promotion
            ) {
                foundMove = true;
            }
        });

        return foundMove;
    }

    private doesMoveCauseSelfCheck(pieceRow: number, pieceColumn: number, move: PieceMove): boolean {
        const piece = this.board[pieceRow][pieceColumn];

        if (this.doesMoveCauseSelfCheckHelper(pieceRow, pieceColumn, move, piece)) {
            return true;
        }

        // If castling, check that we don't also pass through check
        if (piece instanceof King && Math.abs(move.destinationColumn - pieceColumn) === 2) {
            return (
                (move.destinationColumn < pieceColumn &&
                    this.doesMoveCauseSelfCheckHelper(
                        pieceRow,
                        pieceColumn,
                        new PieceMove(pieceRow, move.destinationColumn + 1, false),
                        piece,
                    )) ||
                (move.destinationColumn > pieceColumn &&
                    this.doesMoveCauseSelfCheckHelper(
                        pieceRow,
                        pieceColumn,
                        new PieceMove(pieceRow, pieceColumn + 1, false),
                        piece,
                    ))
            );
        } else {
            return false;
        }
    }

    /**
     * @returns whether making the move would put/keep that team's king in check
     * Returns false if there is no piece at that location
     */
    private doesMoveCauseSelfCheckHelper(
        pieceRow: number,
        pieceColumn: number,
        move: PieceMove,
        piece?: ChessPiece,
    ): boolean {
        // Copy board
        const boardClone = new Array<Array<ChessPiece | undefined>>();
        for (let i = 0; i < ChessBoard.SIZE; i++) {
            boardClone[i] = new Array<ChessPiece | undefined>(ChessBoard.SIZE);
            for (let j = 0; j < ChessBoard.SIZE; j++) {
                boardClone[i][j] = this.board[i][j];
            }
        }

        if (piece) {
            // Make move on board copy
            boardClone[pieceRow][pieceColumn] = undefined;
            boardClone[move.destinationRow][move.destinationColumn] = piece;
            if (move.enPassantTakenPosition) {
                boardClone[move.enPassantTakenPosition.row][move.enPassantTakenPosition.column] = undefined;
            } else if (move.promotion) {
                if (move.promotion === 'rook') {
                    boardClone[move.destinationRow][move.destinationColumn] = new Rook(this.turn);
                } else if (move.promotion === 'queen') {
                    boardClone[move.destinationRow][move.destinationColumn] = new Queen(this.turn);
                } else if (move.promotion === 'bishop') {
                    boardClone[move.destinationRow][move.destinationColumn] = new Bishop(this.turn);
                } else if (move.promotion === 'knight') {
                    boardClone[move.destinationRow][move.destinationColumn] = new Knight(this.turn);
                }
            }
        }

        const selfKing =
            this.turn === PieceColor.White
                ? piece instanceof King
                    ? new Position(move.destinationRow, move.destinationColumn)
                    : this.whiteKing
                : piece instanceof King
                ? new Position(move.destinationRow, move.destinationColumn)
                : this.blackKing;

        // Check if knights are causing check
        const possibleKnightPositionChanges = new Array<Position>();
        possibleKnightPositionChanges.push(new Position(2, 1));
        possibleKnightPositionChanges.push(new Position(2, -1));
        possibleKnightPositionChanges.push(new Position(-2, 1));
        possibleKnightPositionChanges.push(new Position(-2, -1));
        possibleKnightPositionChanges.push(new Position(1, 2));
        possibleKnightPositionChanges.push(new Position(1, -2));
        possibleKnightPositionChanges.push(new Position(-1, 2));
        possibleKnightPositionChanges.push(new Position(-1, -2));

        for (const possiblePositionChange of possibleKnightPositionChanges) {
            const newRow = selfKing.row + possiblePositionChange.row;
            const newColumn = selfKing.column + possiblePositionChange.column;
            if (this.isValidPosition(newRow, newColumn)) {
                const pieceAtNewPosition = boardClone[newRow][newColumn];
                if (
                    pieceAtNewPosition &&
                    pieceAtNewPosition.getColor() !== this.turn &&
                    pieceAtNewPosition instanceof Knight
                ) {
                    return true;
                }
            }
        }

        // Check if pawns are causing check
        for (const pawnColumnChange of [-1, 1]) {
            const pawnRow = this.turn === PieceColor.White ? selfKing.row - 1 : selfKing.row + 1;
            const pawnColumn = selfKing.column + pawnColumnChange;
            if (this.isValidPosition(pawnRow, pawnColumn)) {
                const pieceAtPosition = boardClone[pawnRow][pawnColumn];
                if (pieceAtPosition && pieceAtPosition.getColor() !== this.turn && pieceAtPosition instanceof Pawn) {
                    return true;
                }
            }
        }

        // Check if bishops or queens are causing check on the diagonals
        for (const rowChange of [-1, 1]) {
            for (const columnChange of [-1, 1]) {
                for (let spotsAway = 1; spotsAway < ChessBoard.SIZE; spotsAway++) {
                    const newRow = selfKing.row + rowChange * spotsAway;
                    const newColumn = selfKing.column + columnChange * spotsAway;
                    if (!this.isValidPosition(newRow, newColumn)) {
                        break;
                    }

                    const pieceAtNewPosition = boardClone[newRow][newColumn];
                    if (!pieceAtNewPosition) {
                        continue;
                    } else if (pieceAtNewPosition.getColor() === this.turn) {
                        // There is a piece on our team at this location so we can't keep searching in this direction
                        break;
                    } else if (pieceAtNewPosition instanceof Bishop || pieceAtNewPosition instanceof Queen) {
                        return true;
                    } else {
                        // There is a non-Bishop and non-Queen piece on the other team at this location so we can't keep searching in this direction
                        break;
                    }
                }
            }
        }

        // Check if rooks or queens are causing check on the straights
        const possibleStraightPositionChanges = new Array<Position>();
        possibleStraightPositionChanges.push(new Position(1, 0));
        possibleStraightPositionChanges.push(new Position(-1, 0));
        possibleStraightPositionChanges.push(new Position(0, 1));
        possibleStraightPositionChanges.push(new Position(0, -1));

        for (const possiblePositionChange of possibleStraightPositionChanges) {
            for (let spotsAway = 1; spotsAway < ChessBoard.SIZE; spotsAway++) {
                const newRow = selfKing.row + possiblePositionChange.row * spotsAway;
                const newColumn = selfKing.column + possiblePositionChange.column * spotsAway;
                if (!this.isValidPosition(newRow, newColumn)) {
                    break;
                }

                const pieceAtNewPosition = boardClone[newRow][newColumn];
                if (!pieceAtNewPosition) {
                    continue;
                } else if (pieceAtNewPosition.getColor() === this.turn) {
                    // There is a piece on our team at this location so we can't keep searching in this direction
                    break;
                } else if (pieceAtNewPosition instanceof Rook || pieceAtNewPosition instanceof Queen) {
                    return true;
                } else {
                    // There is a non-Rook and non-Queen piece on the other team at this location so we can't keep searching in this direction
                    break;
                }
            }
        }

        return false;
    }

    private calculateLegalMoves() {
        this.currentLegalMoves.clear();

        for (let row = 0; row < ChessBoard.SIZE; row++) {
            for (let column = 0; column < ChessBoard.SIZE; column++) {
                const pieceAtPosition = this.getPiece(row, column);
                if (!pieceAtPosition || pieceAtPosition.getColor() !== this.turn) {
                    continue;
                }

                const movesForPiece = pieceAtPosition.getPossibleMoves(this, row, column);
                const movesThatDoNotCauseCheck = new Set<PieceMove>();
                movesForPiece.forEach((move: PieceMove) => {
                    if (!this.doesMoveCauseSelfCheck(row, column, move)) {
                        movesThatDoNotCauseCheck.add(move);
                    }
                });

                if (movesThatDoNotCauseCheck.size > 0) {
                    if (!this.currentLegalMoves.has(row)) {
                        this.currentLegalMoves.set(row, new Map<number, Set<PieceMove>>());
                    }
                    this.currentLegalMoves.get(row)?.set(column, movesThatDoNotCauseCheck);
                }
            }
        }
    }

    private initPieces(color: PieceColor) {
        const pawnRow = color === PieceColor.White ? ChessBoard.SIZE - 2 : 1;
        for (let i = 0; i < ChessBoard.SIZE; i++) {
            this.board[pawnRow][i] = new Pawn(color);
        }

        const otherPiecesRow = color === PieceColor.White ? ChessBoard.SIZE - 1 : 0;
        this.board[otherPiecesRow][0] = new Rook(color);
        this.board[otherPiecesRow][1] = new Knight(color);
        this.board[otherPiecesRow][2] = new Bishop(color);
        this.board[otherPiecesRow][3] = new Queen(color);
        this.board[otherPiecesRow][4] = new King(color);
        this.board[otherPiecesRow][5] = new Bishop(color);
        this.board[otherPiecesRow][6] = new Knight(color);
        this.board[otherPiecesRow][7] = new Rook(color);
    }

    private isCheck() {
        if (this.allMoves.length === 0) {
            return false;
        }

        return this.allMoves[this.allMoves.length - 1].causesCheck;
    }

    private generateMoveNotation(pieceRow: number, pieceColumn: number, move: PieceMove, piece: ChessPiece) {
        if (piece instanceof King) {
            if (move.destinationColumn === pieceColumn + 2) {
                return '0-0';
            } else if (move.destinationColumn === pieceColumn - 2) {
                return '0-0-0';
            }
        }

        const pieceSymbol = piece.getSymbol();
        const startRow = ChessBoard.SIZE - pieceRow;
        const startColumn = ChessBoard.COLUMN_SYMBOLS[pieceColumn];

        const endRow = ChessBoard.SIZE - move.destinationRow;
        const endColumn = ChessBoard.COLUMN_SYMBOLS[move.destinationColumn];
        const endNotation = endColumn + endRow;

        if (piece instanceof King) {
            return pieceSymbol + endNotation;
        }

        const otherSimilarPieceMoves = new Array<Position>();
        for (let [row, columnMap] of this.currentLegalMoves) {
            for (let [column, moves] of columnMap) {
                if (row === pieceRow && column === pieceColumn) {
                    continue;
                }
                const pieceAtPosition = this.board[row][column];
                if (!pieceAtPosition) {
                    throw new Error('no piece');
                }

                if (
                    (piece instanceof Pawn && pieceAtPosition instanceof Pawn) ||
                    (piece instanceof Rook && pieceAtPosition instanceof Rook) ||
                    (piece instanceof Knight && pieceAtPosition instanceof Knight) ||
                    (piece instanceof Bishop && pieceAtPosition instanceof Bishop) ||
                    (piece instanceof Queen && pieceAtPosition instanceof Queen)
                ) {
                    for (let innerMove of moves) {
                        if (
                            move.destinationRow === innerMove.destinationRow &&
                            move.destinationColumn === innerMove.destinationColumn
                        ) {
                            otherSimilarPieceMoves.push(new Position(row, column));
                        }
                    }
                }
            }
        }

        let finalNotation = pieceSymbol;
        if (otherSimilarPieceMoves.length > 0) {
            let pieceFromSameRowExists = false;
            let pieceFromSameColumnExists = false;
            for (const otherSimilarPiece of otherSimilarPieceMoves) {
                if (pieceRow === otherSimilarPiece.row) {
                    pieceFromSameRowExists = true;
                } else if (pieceColumn === otherSimilarPiece.column) {
                    pieceFromSameColumnExists = true;
                }
            }

            if (pieceFromSameRowExists) {
                if (pieceFromSameColumnExists) {
                    finalNotation += startColumn + startRow;
                } else {
                    finalNotation += startColumn;
                }
            } else if (pieceFromSameColumnExists) {
                finalNotation += startRow;
            } else {
                finalNotation += startColumn;
            }
        }

        if (move.isTake) {
            finalNotation += 'x';
        }

        finalNotation += endNotation;

        if (move.promotion === 'queen') {
            finalNotation += 'Q';
        } else if (move.promotion === 'rook') {
            finalNotation += 'R';
        } else if (move.promotion === 'bishop') {
            finalNotation += 'B';
        } else if (move.promotion === 'knight') {
            finalNotation += 'N';
        }

        // Calculate check/checkmate/stalemate after we know that state

        return finalNotation;
    }
}
