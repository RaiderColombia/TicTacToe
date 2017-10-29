package co.edu.unal.tictactoe.graphics.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.edu.unal.tictactoe.R;
import co.edu.unal.tictactoe.graphics.fragments.GeneralDialogFragment;
import co.edu.unal.tictactoe.graphics.fragments.SettingsFragment;
import co.edu.unal.tictactoe.graphics.views.BoardView;
import co.edu.unal.tictactoe.logic.Game;
import co.edu.unal.tictactoe.logic.TicTacToeGame;

public class AndroidTicTacToeActivity extends AppCompatActivity {

    private static String TAG = "TicTacToeUnal";

    private TicTacToeGame mGame;
    private BoardView mBoardView;
    private TextView mInfoTextView;

    private boolean mGameOver = false;
    private char mTurn = TicTacToeGame.COMPUTER_PLAYER;

    private boolean mSoundOn;
    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;

    private SharedPreferences mPrefs;
    private DatabaseReference gameRef;
    private Game game;
    private String gameKey;
    private String player;

    private ValueEventListener turnListener;
    private ValueEventListener boardListener;
    private ValueEventListener winnerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        mInfoTextView = (TextView) findViewById(R.id.information);

        gameKey = getIntent().getStringExtra(LoginActivity.EXTRA_GAMEkEY);
        player = getIntent().getStringExtra(LoginActivity.EXTRA_PLAYER);
        gameRef = FirebaseDatabase.getInstance().getReference().child("games").child(gameKey);

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean(SettingsFragment.SOUND_PREFERENCE_KEY, true);

        if (savedInstanceState == null) {
            startNewGame();
        }else{
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mTurn = savedInstanceState.getChar("mTurn");
        }
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
                newGame();
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CANCELED) {
            mSoundOn = mPrefs.getBoolean(SettingsFragment.SOUND_PREFERENCE_KEY, true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mTurn", mTurn);
    }

    private void newGame(){
        String board = "";
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            board = board.concat(String.valueOf(TicTacToeGame.OPEN_SPOT));
        }
        game.setBoard(board);
        game.setWinner(0);
        game.setTurn(player);
        removeListeners();
        gameRef.setValue(game);
        mTurn = player.charAt(0);
        startNewGame();
    }

    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();
        mGameOver = false;

        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(Game.class);
                String playerTurn;
                playerTurn = player.equals(game.getTurn()) ? "You" : game.getPlayerTwo();
                mInfoTextView.setText(playerTurn + " " + getApplicationContext().getResources().getString(R.string.first_computer));

                gameRef.child("turn").addValueEventListener(turnListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {changeTurn(dataSnapshot.getValue(String.class));}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                gameRef.child("board").addValueEventListener(boardListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mGame.setBoardState(dataSnapshot.getValue(String.class).toCharArray());
                        mBoardView.invalidate();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                gameRef.child("winner").addValueEventListener(winnerListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int winner = dataSnapshot.getValue(Integer.class);
                        String resultMessage = "Game Over: You lost!";
                        if (winner > 0) {
                            mGameOver = true;
                            switch (winner) {
                                case 1:
                                    mInfoTextView.setText(R.string.result_tie);
                                    break;
                                case 2:
                                    if (player.equals(String.valueOf(TicTacToeGame.HUMAN_PLAYER))) {
                                        resultMessage = mPrefs.getString("victory_message", getResources().getString(R.string.result_human_wins));
                                    }
                                    mInfoTextView.setText(resultMessage);
                                    break;
                                case 3:
                                    if (player.equals(String.valueOf(TicTacToeGame.COMPUTER_PLAYER))) {
                                        resultMessage = mPrefs.getString("victory_message", getResources().getString(R.string.result_human_wins));
                                    }
                                    mInfoTextView.setText(resultMessage);
                                    break;
                            }
                        }else if(mGameOver){
                            newGame();
                        }
                        Toast.makeText(getApplicationContext(), mInfoTextView.getText(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private boolean setMove(char player, int location) {
        boolean moved = mGame.setMove(player, location);
        if (moved){
            String board = "";
            for(char c : mGame.getBoardState()) { board += String.valueOf(c);}
            gameRef.child("board").setValue(board);
        }
        return moved;
    }

    private void changeTurn(String newTurn){
        String turn;
        mTurn = newTurn.equals(String.valueOf(TicTacToeGame.COMPUTER_PLAYER)) ? TicTacToeGame.COMPUTER_PLAYER : TicTacToeGame.HUMAN_PLAYER;
        if (mTurn == player.toCharArray()[0]){
            turn = "It is your turn";
        }else{
            turn = "It is "+(mTurn == TicTacToeGame.COMPUTER_PLAYER ? game.getPlayerTwo() : game.getPlayerOne()) + "'s turn";
        }
        mInfoTextView.setText(turn);
    }

    private void updateWinner(int winner){
        if (winner > 0) {
            mGameOver = true;
            gameRef.child("winner").setValue(winner);
            gameRef.child("gameOver").setValue(true);
        }else{
            gameRef.child("turn").setValue(String.valueOf(mTurn == TicTacToeGame.COMPUTER_PLAYER ? TicTacToeGame.HUMAN_PLAYER : TicTacToeGame.COMPUTER_PLAYER));
        }
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && player.equals(String.valueOf(mTurn)) && setMove(mTurn, pos)){
                if (mSoundOn)
                    mHumanMediaPlayer.start();
                updateWinner(mGame.checkForWinner());
            }
            return false;
        }
    };

    public void removeListeners(){
        gameRef.child("turn").removeEventListener(turnListener);
        gameRef.child("board").removeEventListener(boardListener);
        gameRef.child("winner").removeEventListener(winnerListener);
    }
}
