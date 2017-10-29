package co.edu.unal.tictactoe.graphics.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import co.edu.unal.tictactoe.R;
import co.edu.unal.tictactoe.graphics.activities.AndroidTicTacToeActivity;

/**
 * TicTacToe
 * Created by Jhon Ramirez on 9/1/17.
 * Universidad Nacional de Colombia
 */
public class GeneralDialogFragment extends DialogFragment {

    public static final int DIALOG_DIFFICULTY_ID = 0;
    public static final int DIALOG_QUIT_ID = 1;
    public static final int DIALOG_ABOUT = 2;
    public static final int DIALOG_RESET = 3;

    private int choice;

    public static GeneralDialogFragment newInstance(int choice){
        GeneralDialogFragment fragment = new GeneralDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("choice", choice);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choice = getArguments().getInt("choice");
        final AndroidTicTacToeActivity activity = (AndroidTicTacToeActivity)getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        switch (choice) {
            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.dialog_quit_question)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(activity.getApplicationContext(), "See you soon!", Toast.LENGTH_SHORT).show();
                                activity.removeListeners();
                                activity.finish();
                            }
                        })
                        .setNegativeButton("No", null);
                break;
            case DIALOG_RESET:
                builder.setMessage(R.string.dialog_reset_question)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setNegativeButton("No", null);
                break;
            case DIALOG_ABOUT:
                View layout = activity.getLayoutInflater().inflate(R.layout.dialog_about, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                break;
        }
        return builder.create();
    }
}
