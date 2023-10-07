export enum ChessErrorCode {
    NoPiece = 0x80070015 >> 0,
    NoLegalMoves = 0x8000000a >> 0,
    NotALegalMove = 0x8000000b >> 0,
    CastleWithNonExistentRook = 0x8000000e >> 0,
}

export class ChessError extends Error {
    public code: ChessErrorCode;

    public constructor(code: ChessErrorCode, message?: string) {
        super(message);

        this.code = code;

        // Grab the call stack
        if (typeof Error.captureStackTrace === 'function') {
            Error.captureStackTrace(this, this.constructor);
        } else {
            this.stack = new Error(`ChessError: ${code}`).stack;
        }

        // Necessary due to the arcana of how Errors work
        Object.setPrototypeOf(this, ChessError.prototype);
    }

    public toString() {
        return `ChessError: ${ChessErrorCode[this.code]}: ${this.message}`;
    }
}
