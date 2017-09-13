package co.edu.unal.tictactoe;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToeActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    private BoardView mBoardView;
    private TextView mInfoTextView;
    private TextView mTieScoreTextView;
    private TextView mHumanScoreTextView;
    private TextView mComputerScoreTextView;

    private boolean mGameOver = false;
    private boolean mThinking = false;
    private char mTurn = TicTacToeGame.COMPUTER_PLAYER;
    private int mHumanWins = 0;
    private int mComputerWins = 0;
    private int mTies = 0;

    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mTieScoreTextView = (TextView) findViewById(R.id.tie_score);
        mHumanScoreTextView = (TextView) findViewById(R.id.player_score);
        mComputerScoreTextView = (TextView) findViewById(R.id.computer_score);

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        startNewGame();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.player_move);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.android_move);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_new_game:
                startNewGame();
                return true;
            case R.id.difficulty_level:
                GeneralDialogFragment.newInstance(GeneralDialogFragment.DIALOG_DIFFICULTY_ID).show(getFragmentManager(), "dialog_fragment_difficulty_level");
                return true;
            case R.id.quit:
                GeneralDialogFragment.newInstance(GeneralDialogFragment.DIALOG_QUIT_ID).show(getFragmentManager(), "dialog_fragment_quit");
                return true;
            case R.id.about:
                GeneralDialogFragment.newInstance(GeneralDialogFragment.DIALOG_ABOUT).show(getFragmentManager(), "dialog_fragment_about");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();
        if (mTurn == TicTacToeGame.HUMAN_PLAYER) {
            mTurn = TicTacToeGame.COMPUTER_PLAYER;
            mInfoTextView.setText(getApplicationContext().getResources().getString(R.string.first_computer));
            androidMove();
        }
        else {
            mTurn = TicTacToeGame.HUMAN_PLAYER;
            mInfoTextView.setText(R.string.first_human);
        }
        mGameOver = false;
    }

    private boolean setMove(char player, int location) {
        boolean moved = mGame.setMove(player, location);
        if (moved){
            mBoardView.invalidate();
        }
        return moved;
    }

    private void androidMove(){
        mThinking = true;
        Toast.makeText(getApplicationContext(), "Thinking...", Toast.LENGTH_SHORT).show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            int move = mGame.getComputerMove();
            if(setMove(TicTacToeGame.COMPUTER_PLAYER, move)) {
                mComputerMediaPlayer.start();
                updateWinner(mGame.checkForWinner());
            }
            mThinking = false;
            }
        }, 2000);
    }

    private void changeTurn(){
        int turn;
        if (mTurn == TicTacToeGame.COMPUTER_PLAYER){
            mTurn = TicTacToeGame.HUMAN_PLAYER;
            turn = R.string.turn_human;
        }else{
            mTurn = TicTacToeGame.COMPUTER_PLAYER;
            turn = R.string.turn_computer;
        }
        mInfoTextView.setText(getApplicationContext().getResources().getString(turn));
    }

    private void updateWinner(int winner){
        if (winner > 0) {
            switch (winner){
                case 1:
                    mTies++;
                    mTieScoreTextView.setText(Integer.toString(mTies));
                    mInfoTextView.setText(R.string.result_tie);
                    break;
                case 2:
                    mHumanWins++;
                    mHumanScoreTextView.setText(Integer.toString(mHumanWins));
                    mInfoTextView.setText(R.string.result_human_wins);
                    break;
                case 3:
                    mComputerWins++;
                    mComputerScoreTextView.setText(Integer.toString(mComputerWins));
                    mInfoTextView.setText(R.string.result_computer_wins);
                    break;
            }
            Toast.makeText(getApplicationContext(), mInfoTextView.getText(), Toast.LENGTH_SHORT).show();
            mGameOver = true;
        }else{
            changeTurn();
        }
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if(!mThinking){
                if (!mGameOver && setMove(mTurn, pos)){
                    mHumanMediaPlayer.start();
                    updateWinner(mGame.checkForWinner());
                    if(!mGameOver){
                        androidMove();
                    }
                }
            }
            return false;
        }
    };

    public TicTacToeGame getGame(){
        return mGame;
    }
}
