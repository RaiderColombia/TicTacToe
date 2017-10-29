package co.edu.unal.tictactoe.graphics.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import co.edu.unal.tictactoe.R;
import co.edu.unal.tictactoe.logic.Game;
import co.edu.unal.tictactoe.logic.TicTacToeGame;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference database;

    public static final String EXTRA_GAMEkEY = "co.edu.unal.tictactoe.GAMEKEY";
    public static final String EXTRA_PLAYER = "co.edu.unal.tictactoe.PLAYER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance().getReference();
    }

    private void createGame(){
        String gameKey = database.child("games").push().getKey();
        final Game game = new Game(gameKey);
        game.setGameOver(true);
        game.setTurn(String.valueOf(TicTacToeGame.COMPUTER_PLAYER));
        game.setWinner(0);
        String board = "";
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            board = board.concat(String.valueOf(TicTacToeGame.OPEN_SPOT));
        }
        game.setBoard(board);
        game.setPlayerOne(((EditText)findViewById(R.id.nicknameEditText)).getText().toString());

        final DatabaseReference gameRef = database.child("games").child(gameKey);
        gameRef.setValue(game);
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game gameResult = dataSnapshot.getValue(Game.class);
                if(!gameResult.isGameOver()){
                    gameRef.removeEventListener(this);
                    redirect(gameResult.getUuid(), "X");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        ((TextView)findViewById(R.id.loginTextView)).setText("Waiting for another player...");

    }

    private void joinGame(String gameKey){
        DatabaseReference gameRef = database.child("games").child(gameKey);
        gameRef.child("gameOver").setValue(false);
        gameRef.child("playerTwo").setValue(((EditText)findViewById(R.id.nicknameEditText)).getText().toString());
        redirect(gameKey, "O");
    }

    private void redirect(String gameKey, String player){
        Intent intent = new Intent(this, AndroidTicTacToeActivity.class);
        intent.putExtra(EXTRA_GAMEkEY, gameKey);
        intent.putExtra(EXTRA_PLAYER, player);
        startActivity(intent);
        finish();
    }

    public void joinGameAction(View view){
        final DatabaseReference gameRef = database.child("games");
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game gameResult = dataSnapshot.getValue(Game.class);
                if(gameResult == null){
                    createGame();
                }else{
                    HashMap hashMap = (HashMap) dataSnapshot.getValue();
                    Map.Entry<String, HashMap> entry = (Map.Entry<String, HashMap>) hashMap.entrySet().toArray()[0];
                    gameRef.removeEventListener(this);
                    joinGame(entry.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
