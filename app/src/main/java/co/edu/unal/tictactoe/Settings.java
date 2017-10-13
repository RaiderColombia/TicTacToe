package co.edu.unal.tictactoe;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * TicTacToe
 * Created by Jhon Ramirez on 10/12/17.
 * Universidad Nacional de Colombia
 */
public class Settings extends PreferenceFragment {

    public static final String SOUND_PREFERENCE_KEY = "sound";
    public static final String DIFFICULTY_PREFERENCE_KEY = "difficulty_level";
    public static final String GOES_FIRST_PREFERENCE_KEY = "goes_first";
    public static final String VICTORY_MESSAGE_PREFERENCE_KEY = "victory_message";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        final ListPreference goesFirstPref = (ListPreference) findPreference(GOES_FIRST_PREFERENCE_KEY);
        String goesFirst = prefs.getString(GOES_FIRST_PREFERENCE_KEY, "Alternate");
        goesFirstPref.setSummary(goesFirst);
        goesFirstPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                goesFirstPref.setSummary((CharSequence) newValue);
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString(GOES_FIRST_PREFERENCE_KEY, newValue.toString());
                ed.commit();

                return true;
            }
        });

        final ListPreference difficultyLevelPref = (ListPreference)
                findPreference(DIFFICULTY_PREFERENCE_KEY);
        String difficulty = prefs.getString(DIFFICULTY_PREFERENCE_KEY,
                getResources().getString(R.string.difficulty_expert));
        difficultyLevelPref.setSummary(difficulty);
        difficultyLevelPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                difficultyLevelPref.setSummary((CharSequence) newValue);
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString(DIFFICULTY_PREFERENCE_KEY, newValue.toString());
                ed.commit();

                return true;
            }
        });

        final EditTextPreference victoryMessagePref = (EditTextPreference)
                findPreference(VICTORY_MESSAGE_PREFERENCE_KEY);
        String victoryMessage = prefs.getString(VICTORY_MESSAGE_PREFERENCE_KEY, getResources().getString(R.string.result_human_wins));
        victoryMessagePref.setSummary("\"" + victoryMessage + "\"");
        victoryMessagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                victoryMessagePref.setSummary((CharSequence) newValue);

                SharedPreferences.Editor ed = prefs.edit();
                ed.putString(VICTORY_MESSAGE_PREFERENCE_KEY, newValue.toString());
                ed.commit();

                return true;
            }
        });
    }

}
