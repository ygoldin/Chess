import './WelcomeModal.css';

export type WelcomeModalProps = Readonly<{
    onPlayAgainstComputer: (playAgainstComputer: boolean) => void;
}>;

export const WelcomeModal = ({ onPlayAgainstComputer }: WelcomeModalProps) => (
    <div className={'welcomeModal'}>
        <p>Welcome to chess! Do you want to play against the computer?</p>
        <div className={'welcomeButtonContainer'}>
            <button className={'welcomeButton'} onClick={() => onPlayAgainstComputer(true)}>
                Yes
            </button>
            <button className={'welcomeButton'} onClick={() => onPlayAgainstComputer(false)}>
                No
            </button>
        </div>
    </div>
);
