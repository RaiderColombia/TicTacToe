package co.edu.unal.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
    private char mGoFirst = TicTacToeGame.HUMAN_PLAYER;
    private char mTurn = TicTacToeGame.COMPUTER_PLAYER;
    private int mHumanWins = 0;
    private int mComputerWins = 0;
    private int mTies = 0;

    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;

    private final Handler handler = new Handler();

    private SharedPreferences mPrefs;

    private boolean mSoundOn;

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

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean(Settings.SOUND_PREFERENCE_KEY, true);

        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mComputerWins = mPrefs.getInt("mComputerWins", 0);
        mTies = mPrefs.getInt("mTies", 0);

        String difficultyLevel = mPrefs.getString(Settings.DIFFICULTY_PREFERENCE_KEY,
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        if (savedInstanceState == null) {
            startNewGame();
        }else{
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mTurn = savedInstanceState.getChar("mTurn");
            mGoFirst = savedInstanceState.getChar("mGoFirst");

            if (!mGameOver && mTurn == TicTacToeGame.COMPUTER_PLAYER) {
                androidMove();
            }
        }
        displayScores();
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
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt("mHumanWins", mHumanWins);
        editor.putInt("mComputerWins", mComputerWins);
        editor.putInt("mTies", mTies);
        editor.putInt("difficultyLevel", mGame.getDifficultyLevel().ordinal());
        editor.commit();
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
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 0);
                return true;
            case R.id.quit:
                GeneralDialogFragment.newInstance(GeneralDialogFragment.DIALOG_QUIT_ID).show(getFragmentManager(), "dialog_fragment_quit");
                return true;
            case R.id.reset_scores:
                GeneralDialogFragment.newInstance(GeneralDialogFragment.DIALOG_RESET).show(getFragmentManager(), "dialog_fragment_reset");
                return true;
            case R.id.about:
                GeneralDialogFragment.newInstance(GeneralDialogFragment.DIALOG_ABOUT).show(getFragmentManager(), "dialog_fragment_about");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CANCELED) {
            mSoundOn = mPrefs.getBoolean(Settings.SOUND_PREFERENCE_KEY, true);
            String difficultyLevel = mPrefs.getString(Settings.DIFFICULTY_PREFERENCE_KEY,
                    getResources().getString(R.string.difficulty_harder));
            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mGoFirst", mGoFirst);
        outState.putChar("mTurn", mTurn);
    }

    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();
        if (mGoFirst == TicTacToeGame.COMPUTER_PLAYER) {
            mGoFirst = TicTacToeGame.HUMAN_PLAYER;
            mTurn = TicTacToeGame.COMPUTER_PLAYER;
            mInfoTextView.setText(getApplicationContext().getResources().getString(R.string.first_computer));
            androidMove();
        }
        else {
            mGoFirst = TicTacToeGame.COMPUTER_PLAYER;
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
                try {
                    if (mSoundOn)
                        mComputerMediaPlayer.start();
                }
                catch (IllegalStateException e) {};
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
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
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

    private void displayScores() {
        mHumanScoreTextView.setText(Integer.toString(mHumanWins));
        mComputerScoreTextView.setText(Integer.toString(mComputerWins));
        mTieScoreTextView.setText(Integer.toString(mTies));
    }

    public void resetScores(){
        mHumanWins = 0;
        mComputerWins = 0;
        mTies = 0;
        displayScores();
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if(!mThinking){
                if (!mGameOver && setMove(mTurn, pos)){
                    if (mSoundOn)
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
