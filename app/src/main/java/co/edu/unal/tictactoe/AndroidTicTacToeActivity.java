package co.edu.unal.tictactoe;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToeActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    private Button mBoardButtons[];
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

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mTieScoreTextView = (TextView) findViewById(R.id.tie_score);
        mHumanScoreTextView = (TextView) findViewById(R.id.player_score);
        mComputerScoreTextView = (TextView) findViewById(R.id.computer_score);

        mGame = new TicTacToeGame();

        startNewGame();
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
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        if (mTurn == TicTacToeGame.HUMAN_PLAYER) {
            mTurn = TicTacToeGame.COMPUTER_PLAYER;
            androidMove(getApplicationContext().getResources().getString(R.string.first_computer));
        }
        else {
            mTurn = TicTacToeGame.HUMAN_PLAYER;
            mInfoTextView.setText(R.string.first_human);
        }

        mGameOver = false;
    }

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    private void androidMove(String message){
        mInfoTextView.setText(message);
        mThinking = true;
        Toast.makeText(getApplicationContext(), "Thinking...", Toast.LENGTH_SHORT).show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int move = mGame.getComputerMove();
                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                mThinking = false;
                updateWinner(mGame.checkForWinner());
            }
        }, 2000);
    }

    private void updateWinner(int winner){
        if (winner == 0)
            mInfoTextView.setText(R.string.turn_human);
        else {
            if (winner == 1) {
                mTies++;
                mTieScoreTextView.setText(Integer.toString(mTies));
                mInfoTextView.setText(R.string.result_tie);
            }
            else if (winner == 2) {
                mHumanWins++;
                mHumanScoreTextView.setText(Integer.toString(mHumanWins));
                mInfoTextView.setText(R.string.result_human_wins);
            }
            else if (winner == 3) {
                mComputerWins++;
                mComputerScoreTextView.setText(Integer.toString(mComputerWins));
                mInfoTextView.setText(R.string.result_computer_wins);
            }
            Toast.makeText(getApplicationContext(), mInfoTextView.getText(), Toast.LENGTH_SHORT).show();
            mGameOver = true;
        }
    }

    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (!mGameOver && mBoardButtons[location].isEnabled() && !mThinking) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    androidMove(getApplicationContext().getResources().getString(R.string.turn_computer));
                }else {
                    updateWinner(mGame.checkForWinner());
                }
            }
        }
    }

    public TicTacToeGame getGame(){
        return mGame;
    }
}
