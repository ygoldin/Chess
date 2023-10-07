import { Bishop, King, Knight, Pawn, PieceColor, Queen, Rook } from './pieces';
import { ChessBoard } from './ChessBoard';
import { PieceMove } from './PieceMove';
import { Position } from './Position';

test('only valid positions are allowed', () => {
    const board = new ChessBoard();
    expect(board.isValidPosition(-1, 0)).toBeFalsy();
    expect(board.isValidPosition(0, -1)).toBeFalsy();
    expect(board.isValidPosition(0, 0)).toBeTruthy();
    expect(board.isValidPosition(5, 6)).toBeTruthy();
    expect(board.isValidPosition(7, 7)).toBeTruthy();
    expect(board.isValidPosition(7, 8)).toBeFalsy();
    expect(board.isValidPosition(8, 7)).toBeFalsy();
});

test('board is setup correctly', () => {
    const board = new ChessBoard();
    // Pawns are in the right spot
    for (let column = 0; column < ChessBoard.SIZE; column++) {
        const blackPawn = board.getPiece(1, column);
        const whitePawn = board.getPiece(ChessBoard.SIZE - 2, column);

        expect(blackPawn).toBeDefined();
        expect(whitePawn).toBeDefined();

        expect(blackPawn instanceof Pawn).toBeTruthy();
        expect(whitePawn instanceof Pawn).toBeTruthy();

        expect(blackPawn?.getColor()).toBe(PieceColor.Black);
        expect(whitePawn?.getColor()).toBe(PieceColor.White);
    }

    // Rooks are in the right spot
    const blackRook1 = board.getPiece(0, 0);
    const blackRook2 = board.getPiece(0, ChessBoard.SIZE - 1);
    const whiteRook1 = board.getPiece(ChessBoard.SIZE - 1, 0);
    const whiteRook2 = board.getPiece(ChessBoard.SIZE - 1, ChessBoard.SIZE - 1);
    for (const rook of [blackRook1, blackRook2, whiteRook1, whiteRook2]) {
        expect(rook).toBeDefined();
        expect(rook instanceof Rook).toBeTruthy();
    }
    expect(blackRook1?.getColor()).toBe(PieceColor.Black);
    expect(blackRook2?.getColor()).toBe(PieceColor.Black);
    expect(whiteRook1?.getColor()).toBe(PieceColor.White);
    expect(whiteRook2?.getColor()).toBe(PieceColor.White);

    // Knights are in the right spot
    const blackKnight1 = board.getPiece(0, 1);
    const blackKnight2 = board.getPiece(0, ChessBoard.SIZE - 2);
    const whiteKnight1 = board.getPiece(ChessBoard.SIZE - 1, 1);
    const whiteKnight2 = board.getPiece(ChessBoard.SIZE - 1, ChessBoard.SIZE - 2);
    for (const knight of [blackKnight1, blackKnight2, whiteKnight1, whiteKnight2]) {
        expect(knight).toBeDefined();
        expect(knight instanceof Knight).toBeTruthy();
    }
    expect(blackKnight1?.getColor()).toBe(PieceColor.Black);
    expect(blackKnight2?.getColor()).toBe(PieceColor.Black);
    expect(whiteKnight1?.getColor()).toBe(PieceColor.White);
    expect(whiteKnight2?.getColor()).toBe(PieceColor.White);

    // Bishops are in the right spot
    const blackBishop1 = board.getPiece(0, 2);
    const blackBishop2 = board.getPiece(0, ChessBoard.SIZE - 3);
    const whiteBishop1 = board.getPiece(ChessBoard.SIZE - 1, 2);
    const whiteBishop2 = board.getPiece(ChessBoard.SIZE - 1, ChessBoard.SIZE - 3);
    for (const bishop of [blackBishop1, blackBishop2, whiteBishop1, whiteBishop2]) {
        expect(bishop).toBeDefined();
        expect(bishop instanceof Bishop).toBeTruthy();
    }
    expect(blackBishop1?.getColor()).toBe(PieceColor.Black);
    expect(blackBishop2?.getColor()).toBe(PieceColor.Black);
    expect(whiteBishop1?.getColor()).toBe(PieceColor.White);
    expect(whiteBishop2?.getColor()).toBe(PieceColor.White);

    // Queens are in the right spot
    const blackQueen = board.getPiece(0, 3);
    const whiteQueen = board.getPiece(ChessBoard.SIZE - 1, 3);
    for (const queen of [blackQueen, whiteQueen]) {
        expect(queen).toBeDefined();
        expect(queen instanceof Queen).toBeTruthy();
    }
    expect(blackQueen?.getColor()).toBe(PieceColor.Black);
    expect(whiteQueen?.getColor()).toBe(PieceColor.White);

    // Kings are in the right spot
    const blackKing = board.getPiece(0, 4);
    const whiteKing = board.getPiece(ChessBoard.SIZE - 1, 4);
    for (const king of [blackKing, whiteKing]) {
        expect(king).toBeDefined();
        expect(king instanceof King).toBeTruthy();
    }
    expect(blackKing?.getColor()).toBe(PieceColor.Black);
    expect(whiteKing?.getColor()).toBe(PieceColor.White);

    // Everything else is blank
    for (let row = 2; row < ChessBoard.SIZE - 2; row++) {
        for (let column = 0; column < ChessBoard.SIZE; column++) {
            expect(board.getPiece(row, column)).not.toBeDefined();
        }
    }
});

test('initial legal moves are correct', () => {
    const board = new ChessBoard();
    const legalMoves = board.getLegalMoves();

    // Should be a row for white pawns and other white pieces
    const pawnRow = ChessBoard.SIZE - 2;
    const otherPieceRow = ChessBoard.SIZE - 1;
    expect(legalMoves.has(pawnRow)).toBeTruthy();
    expect(legalMoves.has(otherPieceRow)).toBeTruthy();
    expect(legalMoves.size).toBe(2);

    const pawnMoves = legalMoves.get(pawnRow);
    const otherMoves = legalMoves.get(otherPieceRow);
    expect(pawnMoves).toBeDefined();
    expect(otherMoves).toBeDefined();

    // All pawns should have the ability to move one or two steps forward
    expect(pawnMoves?.size).toBe(ChessBoard.SIZE);
    for (let column = 0; column < ChessBoard.SIZE - 1; column++) {
        expect(pawnMoves?.has(column)).toBeTruthy();
        const movesForThisPawn = pawnMoves?.get(column);
        expect(movesForThisPawn).toBeDefined();
        expect(movesForThisPawn?.size).toBe(2);
        let moveForwardOneFound = false;
        let moveForwardTwoFound = false;
        movesForThisPawn?.forEach((move: PieceMove) => {
            if (
                move.destinationRow === pawnRow - 1 &&
                move.destinationColumn === column &&
                !move.isTake &&
                !move.enPassantTakenPosition
            ) {
                moveForwardOneFound = true;
            } else if (
                move.destinationRow === pawnRow - 2 &&
                move.destinationColumn === column &&
                !move.isTake &&
                !move.enPassantTakenPosition
            ) {
                moveForwardTwoFound = true;
            }
        });
        expect(moveForwardOneFound).toBeTruthy();
        expect(moveForwardTwoFound).toBeTruthy();
    }

    // Rooks, bishops, the queen, and king should not have any moves
    for (const column of [0, 2, 3, 4, 5, 7]) {
        expect(otherMoves?.has(column)).toBeFalsy();
    }

    // Both knights should have two moves each
    expect(otherMoves?.size).toBe(2);
    for (const column of [1, 6]) {
        expect(otherMoves?.has(column)).toBeTruthy();
        const thisKnightMoves = otherMoves?.get(column);
        expect(thisKnightMoves).toBeDefined();
        expect(thisKnightMoves?.size).toBe(2);
        let moveLeftFound = false;
        let moveRightFound = false;
        thisKnightMoves?.forEach((move: PieceMove) => {
            if (
                move.destinationRow === otherPieceRow - 2 &&
                move.destinationColumn === column - 1 &&
                !move.isTake &&
                !move.enPassantTakenPosition
            ) {
                moveLeftFound = true;
            } else if (
                move.destinationRow === otherPieceRow - 2 &&
                move.destinationColumn === column + 1 &&
                !move.isTake &&
                !move.enPassantTakenPosition
            ) {
                moveRightFound = true;
            }
        });

        expect(moveLeftFound).toBeTruthy();
        expect(moveRightFound).toBeTruthy();
    }
});

test('en passant works', () => {
    const board = new ChessBoard();
    board.makeMove(6, 2, new PieceMove(4, 2, false)); // White pawn to c4
    board.makeMove(1, 1, new PieceMove(3, 1, false)); // Black pawn to b5
    board.makeMove(4, 2, new PieceMove(3, 2, false)); // White pawn to c5

    const blackPawn = board.getPiece(3, 1);
    expect(blackPawn).toBeDefined();
    expect(blackPawn instanceof Pawn).toBeTruthy();

    const whitePawn = board.getPiece(3, 2);
    expect(whitePawn).toBeDefined();
    expect(whitePawn instanceof Pawn).toBeTruthy();

    const blackPawnConverted = blackPawn as Pawn;
    expect(blackPawnConverted.getIsOpenToEnPassant()).toBeFalsy();
    const whitePawnConverted = whitePawn as Pawn;
    expect(whitePawnConverted.getIsOpenToEnPassant()).toBeFalsy();

    board.makeMove(1, 3, new PieceMove(3, 3, false)); // Black pawn to d5
    const secondBlackPawn = board.getPiece(3, 3);
    expect(secondBlackPawn).toBeDefined();
    expect(secondBlackPawn instanceof Pawn).toBeTruthy();

    const secondBlackPawnConverted = secondBlackPawn as Pawn;
    expect(secondBlackPawnConverted.getIsOpenToEnPassant()).toBeTruthy();

    // White pawn to d6, en passant. Should not throw
    expect(() => board.makeMove(3, 2, new PieceMove(2, 3, true, new Position(3, 3)))).not.toThrow();
});

test('allow castling at the right time', () => {
    const board = new ChessBoard();
    board.makeMove(6, 4, new PieceMove(4, 4, false)); // White pawn to e4
    board.makeMove(1, 3, new PieceMove(3, 3, false)); // Black pawn to d5
    board.makeMove(7, 5, new PieceMove(6, 4, false)); // White bishop to e2
    board.makeMove(0, 2, new PieceMove(2, 4, false)); // Black bishop to e6
    board.makeMove(7, 6, new PieceMove(5, 5, false)); // White knight to f3
    board.makeMove(0, 3, new PieceMove(1, 3, false)); // Black queen to d7

    // White king castles to the right
    expect(() => board.makeMove(7, 4, new PieceMove(7, 6, false))).not.toThrow();

    board.makeMove(0, 1, new PieceMove(2, 2, false)); // Black knight to c6
    board.makeMove(6, 0, new PieceMove(4, 0, false)); // White pawn to a4

    // Black king castles to the left
    expect(() => board.makeMove(0, 4, new PieceMove(0, 2, false))).not.toThrow();
});

test('do not allow castling with pieces in the way', () => {
    const board = new ChessBoard();
    expect(() => board.makeMove(7, 4, new PieceMove(7, 2, false))).toThrow();
});

test('do not allow castling while in check', () => {
    const board = new ChessBoard();
    board.makeMove(6, 3, new PieceMove(4, 3, false)); // White pawn to d4
    board.makeMove(0, 1, new PieceMove(2, 2, false)); // Black knight to c6
    board.makeMove(7, 2, new PieceMove(5, 4, false)); // White bishop to e3
    board.makeMove(2, 2, new PieceMove(4, 1, false)); // Black knight to b4
    board.makeMove(7, 3, new PieceMove(5, 3, false)); // White queen to d3
    board.makeMove(1, 0, new PieceMove(3, 0, false)); // Black pawn to a5
    board.makeMove(7, 1, new PieceMove(5, 2, false)); // White knight to c3
    board.makeMove(4, 1, new PieceMove(6, 2, true)); // Black knight takes pawn at c2, check

    expect(() => board.makeMove(7, 4, new PieceMove(7, 2, false))).toThrow();
});

test('do not allow castling if passing through check', () => {
    const board = new ChessBoard();
    board.makeMove(6, 6, new PieceMove(4, 6, false)); // White pawn to g4
    board.makeMove(1, 4, new PieceMove(3, 4, false)); // Black pawn to e5
    board.makeMove(7, 5, new PieceMove(5, 7, false)); // White bishop to h2
    board.makeMove(0, 5, new PieceMove(3, 2, false)); // Black bishop to c5
    board.makeMove(7, 6, new PieceMove(5, 5, false)); // White knight to f3
    board.makeMove(0, 4, new PieceMove(0, 5, false)); // Black king to f8
    board.makeMove(4, 6, new PieceMove(3, 6, false)); // White pawn to g5
    board.makeMove(0, 3, new PieceMove(3, 6, true)); // Black queen takes pawn at g5

    expect(() => board.makeMove(7, 4, new PieceMove(7, 2, false))).toThrow();
});

test('do not allow castling if king has moved', () => {
    const board = new ChessBoard();
    board.makeMove(6, 4, new PieceMove(4, 4, false)); // White pawn to e4
    board.makeMove(1, 3, new PieceMove(3, 3, false)); // Black pawn to d5
    board.makeMove(7, 5, new PieceMove(6, 4, false)); // White bishop to e2
    board.makeMove(0, 2, new PieceMove(2, 4, false)); // Black bishop to e6
    board.makeMove(7, 6, new PieceMove(5, 5, false)); // White knight to f3
    board.makeMove(0, 3, new PieceMove(1, 3, false)); // Black queen to d7
    board.makeMove(7, 4, new PieceMove(7, 5, false)); // White king to f1
    board.makeMove(1, 3, new PieceMove(0, 3, false)); // Black queen to d8
    board.makeMove(7, 5, new PieceMove(7, 4, false)); // White king to e1
    board.makeMove(0, 3, new PieceMove(1, 3, false)); // Black queen to d7

    expect(() => board.makeMove(7, 4, new PieceMove(7, 6, false))).toThrow();
});

test('do not allow castling if rook has moved', () => {
    const board = new ChessBoard();
    board.makeMove(6, 4, new PieceMove(4, 4, false)); // White pawn to e4
    board.makeMove(1, 3, new PieceMove(3, 3, false)); // Black pawn to d5
    board.makeMove(7, 5, new PieceMove(6, 4, false)); // White bishop to e2
    board.makeMove(0, 2, new PieceMove(2, 4, false)); // Black bishop to e6
    board.makeMove(7, 6, new PieceMove(5, 5, false)); // White knight to f3
    board.makeMove(0, 3, new PieceMove(1, 3, false)); // Black queen to d7
    board.makeMove(7, 7, new PieceMove(7, 5, false)); // White rook to f1
    board.makeMove(1, 3, new PieceMove(0, 3, false)); // Black queen to d8
    board.makeMove(7, 5, new PieceMove(7, 7, false)); // White rook to h1
    board.makeMove(0, 3, new PieceMove(1, 3, false)); // Black queen to d7

    expect(() => board.makeMove(7, 4, new PieceMove(7, 6, false))).toThrow();
});

test('only allow moves to stop check from a queen', () => {
    const board = new ChessBoard();
    board.makeMove(6, 4, new PieceMove(4, 4, false)); // White pawn to e4
    board.makeMove(1, 4, new PieceMove(3, 4, false)); // Black pawn to e5
    board.makeMove(7, 3, new PieceMove(3, 7, false)); // White queen to h5
    board.makeMove(1, 0, new PieceMove(3, 0, false)); // Black pawn to a5
    board.makeMove(3, 7, new PieceMove(1, 5, true)); // White queen takes pawn at f7, check

    // The only legal move should be black king takes white queen
    const legalMoves = board.getLegalMoves();
    expect(legalMoves.size).toBe(1);
    expect(legalMoves.has(0)).toBeTruthy();
    const movesFrom0thRowPotential = legalMoves.get(0);
    expect(movesFrom0thRowPotential).toBeDefined();
    const movesFrom0thRow = movesFrom0thRowPotential as Map<number, Set<PieceMove>>;

    expect(movesFrom0thRow.size).toBe(1);
    expect(movesFrom0thRow.has(4)).toBeTruthy();
    const kingMovesPotential = movesFrom0thRow.get(4);
    expect(kingMovesPotential).toBeDefined();
    const kingMoves = kingMovesPotential as Set<PieceMove>;

    expect(kingMoves.size).toBe(1);
    kingMoves.forEach((move: PieceMove) => {
        expect(move.destinationRow).toBe(1);
        expect(move.destinationColumn).toBe(5);
        expect(move.isTake).toBeTruthy();
    });
});

test('only allow moves to stop check from a knight', () => {
    const board = new ChessBoard();
    board.makeMove(7, 1, new PieceMove(5, 2, false)); // White knight to c3
    board.makeMove(0, 1, new PieceMove(2, 2, false)); // Black knight to c6
    board.makeMove(5, 2, new PieceMove(3, 3, false)); // White knight to d5
    board.makeMove(2, 2, new PieceMove(4, 3, false)); // Black knight to d4
    board.makeMove(3, 3, new PieceMove(1, 2, true)); // White knight takes pawn at c7, check

    // The only legal move should be black queen takes knight
    const legalMoves = board.getLegalMoves();
    expect(legalMoves.size).toBe(1);
    expect(legalMoves.has(0)).toBeTruthy();
    const movesFrom0thRowPotential = legalMoves.get(0);
    expect(movesFrom0thRowPotential).toBeDefined();
    const movesFrom0thRow = movesFrom0thRowPotential as Map<number, Set<PieceMove>>;

    expect(movesFrom0thRow.size).toBe(1);
    expect(movesFrom0thRow.has(3)).toBeTruthy();
    const queenMovesPotential = movesFrom0thRow.get(3);
    expect(queenMovesPotential).toBeDefined();
    const queenMoves = queenMovesPotential as Set<PieceMove>;

    expect(queenMoves.size).toBe(1);
    queenMoves.forEach((move: PieceMove) => {
        expect(move.destinationRow).toBe(1);
        expect(move.destinationColumn).toBe(2);
        expect(move.isTake).toBeTruthy();
    });
});

test('only allow moves to stop check from a pawn', () => {
    const board = new ChessBoard();
    board.makeMove(6, 2, new PieceMove(4, 2, false)); // White pawn to c4
    board.makeMove(1, 6, new PieceMove(3, 6, false)); // Black pawn to g5
    board.makeMove(4, 2, new PieceMove(3, 2, false)); // White pawn to c5
    board.makeMove(3, 6, new PieceMove(4, 6, false)); // Black pawn to g4
    board.makeMove(3, 2, new PieceMove(2, 2, false)); // White pawn to c6
    board.makeMove(4, 6, new PieceMove(5, 6, false)); // Black pawn to g3
    board.makeMove(2, 2, new PieceMove(1, 3, true)); // White pawn takes pawn at d7, check

    // The only legal moves should be black king, queen, bishop, or knight takes white pawn
    const legalMoves = board.getLegalMoves();
    expect(legalMoves.size).toBe(1);
    expect(legalMoves.has(0)).toBeTruthy();
    const movesFrom0thRowPotential = legalMoves.get(0);
    expect(movesFrom0thRowPotential).toBeDefined();
    const movesFrom0thRow = movesFrom0thRowPotential as Map<number, Set<PieceMove>>;

    expect(movesFrom0thRow.size).toBe(4);

    expect(movesFrom0thRow.has(1)).toBeTruthy();
    const knightMovesPotential = movesFrom0thRow.get(1);
    expect(knightMovesPotential).toBeDefined();
    const knightMoves = knightMovesPotential as Set<PieceMove>;

    expect(movesFrom0thRow.has(2)).toBeTruthy();
    const bishopMovesPotential = movesFrom0thRow.get(2);
    expect(bishopMovesPotential).toBeDefined();
    const bishopMoves = bishopMovesPotential as Set<PieceMove>;

    expect(movesFrom0thRow.has(3)).toBeTruthy();
    const queenMovesPotential = movesFrom0thRow.get(3);
    expect(queenMovesPotential).toBeDefined();
    const queenMoves = queenMovesPotential as Set<PieceMove>;

    expect(movesFrom0thRow.has(4)).toBeTruthy();
    const kingMovesPotential = movesFrom0thRow.get(4);
    expect(kingMovesPotential).toBeDefined();
    const kingMoves = kingMovesPotential as Set<PieceMove>;

    for (const movesSet of [knightMoves, bishopMoves, queenMoves, kingMoves]) {
        expect(movesSet.size).toBe(1);
        movesSet.forEach((move: PieceMove) => {
            expect(move.destinationRow).toBe(1);
            expect(move.destinationColumn).toBe(3);
            expect(move.isTake).toBeTruthy();
        });
    }
});

test('allow pawn promotion to cause check', () => {
    const board = new ChessBoard();
    board.makeMove(6, 1, new PieceMove(4, 1, false)); // White pawn to b4
    board.makeMove(1, 6, new PieceMove(3, 6, false)); // Black pawn to g5
    board.makeMove(4, 1, new PieceMove(3, 1, false)); // White pawn to b5
    board.makeMove(3, 6, new PieceMove(4, 6, false)); // Black pawn to g4
    board.makeMove(3, 1, new PieceMove(2, 1, false)); // White pawn to b6
    board.makeMove(4, 6, new PieceMove(5, 6, false)); // Black pawn to g3
    board.makeMove(2, 1, new PieceMove(1, 2, true)); // White pawn takes pawn at c7
    board.makeMove(5, 6, new PieceMove(6, 7, true)); // Black pawn takes pawn at h2
    // White pawn takes queen at d8, promotes to rook, check
    board.makeMove(1, 2, new PieceMove(0, 3, true, undefined, 'rook'));

    // The only legal move should be black king takes white rook
    const legalMoves = board.getLegalMoves();
    expect(legalMoves.size).toBe(1);
    expect(legalMoves.has(0)).toBeTruthy();
    const movesFrom0thRowPotential = legalMoves.get(0);
    expect(movesFrom0thRowPotential).toBeDefined();
    const movesFrom0thRow = movesFrom0thRowPotential as Map<number, Set<PieceMove>>;

    expect(movesFrom0thRow.size).toBe(1);
    expect(movesFrom0thRow.has(4)).toBeTruthy();
    const kingMovesPotential = movesFrom0thRow.get(4);
    expect(kingMovesPotential).toBeDefined();
    const kingMoves = kingMovesPotential as Set<PieceMove>;

    expect(kingMoves.size).toBe(1);
    kingMoves.forEach((move: PieceMove) => {
        expect(move.destinationRow).toBe(0);
        expect(move.destinationColumn).toBe(3);
        expect(move.isTake).toBeTruthy();
    });
});

test('correctly undoes pawn promotion and capture', () => {
    const board = new ChessBoard();
    board.makeMove(6, 1, new PieceMove(4, 1, false)); // White pawn to b4
    board.makeMove(1, 6, new PieceMove(3, 6, false)); // Black pawn to g5
    board.makeMove(4, 1, new PieceMove(3, 1, false)); // White pawn to b5
    board.makeMove(3, 6, new PieceMove(4, 6, false)); // Black pawn to g4
    board.makeMove(3, 1, new PieceMove(2, 1, false)); // White pawn to b6
    board.makeMove(4, 6, new PieceMove(5, 6, false)); // Black pawn to g3
    board.makeMove(2, 1, new PieceMove(1, 2, true)); // White pawn takes pawn at c7
    board.makeMove(5, 6, new PieceMove(6, 7, true)); // Black pawn takes pawn at h2
    // White pawn takes queen at d8, promotes to rook, check
    board.makeMove(1, 2, new PieceMove(0, 3, true, undefined, 'rook'));

    board.undo();
    const whitePawn = board.getPiece(1, 2);
    const blackQueen = board.getPiece(0, 3);
    expect(whitePawn).toBeDefined();
    expect(blackQueen).toBeDefined();
    expect(whitePawn instanceof Pawn).toBeTruthy();
    expect(blackQueen instanceof Queen).toBeTruthy();
    expect(whitePawn!.getColor()).toBe(PieceColor.White);
    expect(blackQueen!.getColor()).toBe(PieceColor.Black);

    // Can redo the move
    expect(() => board.makeMove(1, 2, new PieceMove(0, 3, true, undefined, 'rook'))).not.toThrow();
});

test('correctly undoes castle', () => {
    const board = new ChessBoard();
    board.makeMove(6, 4, new PieceMove(4, 4, false)); // White pawn to e4
    board.makeMove(1, 3, new PieceMove(3, 3, false)); // Black pawn to d5
    board.makeMove(7, 5, new PieceMove(6, 4, false)); // White bishop to e2
    board.makeMove(0, 2, new PieceMove(2, 4, false)); // Black bishop to e6
    board.makeMove(7, 6, new PieceMove(5, 5, false)); // White knight to f3
    board.makeMove(0, 3, new PieceMove(1, 3, false)); // Black queen to d7
    board.makeMove(7, 4, new PieceMove(7, 6, false)); // White king castles to the right

    board.undo();
    const whiteKing = board.getPiece(7, 4);
    const whiteRook = board.getPiece(7, 7);
    expect(whiteKing).toBeDefined();
    expect(whiteRook).toBeDefined();
    expect(whiteKing instanceof King).toBeTruthy();
    expect(whiteRook instanceof Rook).toBeTruthy();
    const whiteKingConfirmed = whiteKing as King;
    const whiteRookConfirmed = whiteRook as Rook;
    expect(whiteKingConfirmed.getNumTimesMoved()).toBe(0);
    expect(whiteRookConfirmed.getNumTimesMoved()).toBe(0);

    // Can redo the move
    expect(() => board.makeMove(7, 4, new PieceMove(7, 6, false))).not.toThrow();
});

test('correctly resets en passant', () => {
    const board = new ChessBoard();
    board.makeMove(6, 2, new PieceMove(4, 2, false)); // White pawn to c4
    board.makeMove(1, 1, new PieceMove(3, 1, false)); // Black pawn to b5
    board.makeMove(4, 2, new PieceMove(3, 2, false)); // White pawn to c5
    board.makeMove(1, 3, new PieceMove(3, 3, false)); // Black pawn to d5
    board.makeMove(3, 2, new PieceMove(2, 3, true, new Position(3, 3))); // White pawn to d4, en passant

    board.undo();
    const whitePawn = board.getPiece(3, 2);
    const blackPawn = board.getPiece(3, 3);
    expect(whitePawn).toBeDefined();
    expect(blackPawn).toBeDefined();
    expect(whitePawn instanceof Pawn).toBeTruthy();
    expect(blackPawn instanceof Pawn).toBeTruthy();
    const blackPawnConfirmed = blackPawn as Pawn;
    expect(blackPawnConfirmed.getIsOpenToEnPassant()).toBeTruthy();

    // Can redo the move
    expect(() => board.makeMove(3, 2, new PieceMove(2, 3, true, new Position(3, 3)))).not.toThrow();
});

test('correct notation for taking, check, and promotion', () => {
    const board = new ChessBoard();
    board.makeMove(6, 2, new PieceMove(4, 2, false)); // White pawn to c4
    board.makeMove(0, 6, new PieceMove(2, 5, false)); // Black knight to f6
    board.makeMove(4, 2, new PieceMove(3, 2, false)); // White pawn to c5
    board.makeMove(2, 5, new PieceMove(3, 3, false)); // Black knight to d5
    board.makeMove(3, 2, new PieceMove(2, 2, false)); // White pawn to c6
    board.makeMove(1, 5, new PieceMove(2, 5, false)); // Black pawn to f6
    board.makeMove(2, 2, new PieceMove(1, 3, true)); // White pawn takes d7, check
    board.makeMove(0, 4, new PieceMove(1, 5, false)); // Black king to f7
    board.makeMove(1, 3, new PieceMove(0, 2, true, undefined, 'queen')); // White pawn takes c8, promotes to queen

    const notation = board.getNotation();
    expect(notation.length).toBe(9);
    const expectedNotation = ['c4', 'Nf6', 'c5', 'Nd5', 'c6', 'f6', 'xd7+', 'Kf7', 'xc8Q'];
    for (let i = 0; i < 9; i++) {
        expect(notation[i]).toBe(expectedNotation[i]);
    }
});

test('correct notation for checkmate', () => {
    const board = new ChessBoard();
    board.makeMove(6, 4, new PieceMove(4, 4, false)); // White pawn to e4
    board.makeMove(1, 0, new PieceMove(2, 0, false)); // Black pawn to a6
    board.makeMove(7, 5, new PieceMove(4, 2, false)); // White bishop to c4
    board.makeMove(2, 0, new PieceMove(3, 0, false)); // Black pawn to a5
    board.makeMove(7, 3, new PieceMove(5, 5, false)); // White queen to f3
    board.makeMove(0, 0, new PieceMove(1, 0, false)); // Black rook to a7
    board.makeMove(5, 5, new PieceMove(1, 5, true)); // Queen takes f7, checkmate

    const notation = board.getNotation();
    expect(notation.length).toBe(7);
    const expectedNotation = ['e4', 'a6', 'Bc4', 'a5', 'Qf3', 'Ra7', 'Qxf7#'];
    for (let i = 0; i < 7; i++) {
        expect(notation[i]).toBe(expectedNotation[i]);
    }
});

test('correct notation when two pieces can make the same move', () => {
    const board = new ChessBoard();
    board.makeMove(6, 2, new PieceMove(4, 2, false)); // White pawn to c4
    board.makeMove(0, 6, new PieceMove(2, 5, false)); // Black knight to f6
    board.makeMove(4, 2, new PieceMove(3, 2, false)); // White pawn to c5
    board.makeMove(2, 5, new PieceMove(3, 3, false)); // Black knight to d5
    board.makeMove(3, 2, new PieceMove(2, 2, false)); // White pawn to c6
    board.makeMove(3, 3, new PieceMove(4, 1, false)); // Black knight to b4
    board.makeMove(6, 4, new PieceMove(4, 4, false)); // White pawn to e4
    board.makeMove(4, 1, new PieceMove(2, 2, true)); // Black knight from row 4 takes c6

    const notation = board.getNotation();
    expect(notation.length).toBe(8);
    const expectedNotation = ['c4', 'Nf6', 'c5', 'Nd5', 'c6', 'Nb4', 'e4', 'N4xc6'];
    for (let i = 0; i < 8; i++) {
        expect(notation[i]).toBe(expectedNotation[i]);
    }
});

test('correctly undoes moves when calculating best move', () => {
    const board = new ChessBoard();
    board.makeMove(6, 2, new PieceMove(4, 2, false)); // White pawn to c4
    const legalMovesBeforeCalculation = new Map<number, Map<number, Set<PieceMove>>>(board.getLegalMoves());
    const bestMove = board.getBestMoveForBlack();
    expect(bestMove).toBeDefined();
    const legalMovesAfterCalculation = board.getLegalMoves();

    expect(legalMovesBeforeCalculation.size).toBe(legalMovesAfterCalculation.size);
    for (let [row, columnMap] of legalMovesBeforeCalculation) {
        expect(legalMovesAfterCalculation.has(row)).toBeTruthy();
        const legalMovesAfterCalculationColumnMap = legalMovesAfterCalculation.get(row) as Map<number, Set<PieceMove>>;
        expect(columnMap.size).toBe(legalMovesAfterCalculationColumnMap.size);
        for (let [column, moves] of columnMap) {
            expect(legalMovesAfterCalculationColumnMap.has(column)).toBeTruthy();
            const legalMovesAfterCalculationMovesSet = legalMovesAfterCalculationColumnMap.get(
                column,
            ) as Set<PieceMove>;
            expect(moves.size).toBe(legalMovesAfterCalculationMovesSet.size);
        }
    }

    expect(() => board.makeMove(bestMove!.pieceRow, bestMove!.pieceColumn, bestMove!.move)).not.toThrow();
});
