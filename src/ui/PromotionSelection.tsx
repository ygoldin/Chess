import { PieceColor } from '../sdk';
import whiteRook from './assets/white_rook.png';
import blackRook from './assets/black_rook.png';
import whiteKnight from './assets/white_knight.png';
import blackKnight from './assets/black_knight.png';
import whiteBishop from './assets/white_bishop.png';
import blackBishop from './assets/black_bishop.png';
import whiteQueen from './assets/white_queen.png';
import blackQueen from './assets/black_queen.png';
import './PromotionSelection.css';

export type PromotionSelectionProps = Readonly<{
    color: PieceColor;
    onRook: () => void;
    onQueen: () => void;
    onKnight: () => void;
    onBishop: () => void;
}>;

export const PromotionSelection = ({ color, onRook, onQueen, onKnight, onBishop }: PromotionSelectionProps) => (
    <div className={'promotionContainer'}>
        <p>What do you want to promote your pawn to?</p>
        <div className={'promotionButtonContainer'}>
            <div className={'promotionButtonRow'}>
                <button onClick={onQueen} style={{ backgroundColor: 'white' }} className={'buttonContainer'}>
                    <img
                        src={color === PieceColor.White ? whiteQueen : blackQueen}
                        alt={'Queen'}
                        className={'square'}
                    />
                </button>
                <button onClick={onRook} style={{ backgroundColor: 'white' }} className={'buttonContainer'}>
                    <img src={color === PieceColor.White ? whiteRook : blackRook} alt={'Rook'} className={'square'} />
                </button>
            </div>
            <div className={'promotionButtonRow'}>
                <button onClick={onBishop} style={{ backgroundColor: 'white' }} className={'buttonContainer'}>
                    <img
                        src={color === PieceColor.White ? whiteBishop : blackBishop}
                        alt={'Bishop'}
                        className={'square'}
                    />
                </button>
                <button onClick={onKnight} style={{ backgroundColor: 'white' }} className={'buttonContainer'}>
                    <img
                        src={color === PieceColor.White ? whiteKnight : blackKnight}
                        alt={'Knight'}
                        className={'square'}
                    />
                </button>
            </div>
        </div>
    </div>
);
