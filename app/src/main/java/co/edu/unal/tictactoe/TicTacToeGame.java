/*
 * Copyright (C) 2010 By Frank McCown at Harding University
 * 
 * This is the solution to Tutorial 2.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.edu.unal.tictactoe;

import java.util.Random;


public class TicTacToeGame {

    public enum DifficultyLevel {Easy, Harder, Expert}

	public static final int BOARD_SIZE = 9;
	
	// Characters used to represent the human, computer, and open spots
	public static final char HUMAN_PLAYER = 'X';
	public static final char COMPUTER_PLAYER = 'O';
	public static final char OPEN_SPOT = ' ';
	
	// Random number generator
	private Random mRand; 
		
	// Represents the game board
	private char mBoard[];

    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;
	
	public TicTacToeGame() {
		mBoard = new char[BOARD_SIZE];		
		mRand = new Random();	
	}
	
	/** Clear the board of all X's and O's. */
	public void clearBoard() {
		// Reset all buttons
    	for (int i = 0; i < BOARD_SIZE; i++) {
    		mBoard[i] = OPEN_SPOT;    		   
    	}
	}
	
	/** Set the given player at the given location on the game board.
	 *  The location must be available, or the board will not be changed.
	 * 
	 * @param player - The human or computer player
	 * @param location - The location (0-8) to place the move
	 */
	public boolean setMove(char player, int location) {
		if (location >= 0 && location < BOARD_SIZE &&
				mBoard[location] == OPEN_SPOT) {
			mBoard[location] = player;
			return true;
		}
		return false;
	}
	
	/**
	 * Check for a winner.  Return a status value indicating the board status.
	 * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
	 * or 3 if O won.
	 */
	public int checkForWinner() {
		
		// Check horizontal wins
		for (int i = 0; i <= 6; i += 3) {
			if (mBoard[i] == HUMAN_PLAYER && 
				mBoard[i+1] == HUMAN_PLAYER &&
				mBoard[i+2]== HUMAN_PLAYER)
				return 2;
			if (mBoard[i] == COMPUTER_PLAYER && 
				mBoard[i+1]== COMPUTER_PLAYER && 
				mBoard[i+2] == COMPUTER_PLAYER)
				return 3;
		}
	
		// Check vertical wins
		for (int i = 0; i <= 2; i++) {
			if (mBoard[i] == HUMAN_PLAYER && 
				mBoard[i+3] == HUMAN_PLAYER && 
				mBoard[i+6]== HUMAN_PLAYER)
				return 2;
			if (mBoard[i] == COMPUTER_PLAYER && 
				mBoard[i+3] == COMPUTER_PLAYER && 
				mBoard[i+6]== COMPUTER_PLAYER)
				return 3;
		}
	
		// Check for diagonal wins
		if ((mBoard[0] == HUMAN_PLAYER &&
			 mBoard[4] == HUMAN_PLAYER && 
			 mBoard[8] == HUMAN_PLAYER) ||
			(mBoard[2] == HUMAN_PLAYER && 
			 mBoard[4] == HUMAN_PLAYER &&
			 mBoard[6] == HUMAN_PLAYER))
			return 2;
		if ((mBoard[0] == COMPUTER_PLAYER &&
			 mBoard[4] == COMPUTER_PLAYER && 
			 mBoard[8] == COMPUTER_PLAYER) ||
			(mBoard[2] == COMPUTER_PLAYER && 
			 mBoard[4] == COMPUTER_PLAYER &&
			 mBoard[6] == COMPUTER_PLAYER))
			return 3;
	
		// Check for tie
		for (int i = 0; i < BOARD_SIZE; i++) {
			// If we find a number, then no one has won yet
			if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
				return 0;
		}
	
		// If we make it through the previous loop, all places are taken, so it's a tie
		return 1;
	}	
	
	/** Return the best move for the computer to make. You must call setMove() to 
	 * actually make the computer move to that location. 
	 * @return The best move for the computer to make.
	 */
    public int getComputerMove() {
        int move = -1;
        switch (mDifficultyLevel){
            case Easy:
                move = getRandomMove();
                break;
            case Harder:
                move = getWinningMove();
                if (move == -1) {
                    move = getRandomMove();
                }
                break;
            case Expert:
                move = getWinningMove();
                if (move == -1) {
                    move = getBlockingMove();
                }
                if (move == -1) {
                    move = getRandomMove();
                }
                break;
        }
        return move;
    }

    private int getRandomMove() {
        int move;
        do {
            move = mRand.nextInt(9);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
        return move;
    }

    private int getBlockingMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            char curr = mBoard[i];
            if (curr != HUMAN_PLAYER && curr != COMPUTER_PLAYER) {
                // What if X moved here?
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                else
                    mBoard[i] = OPEN_SPOT;
            }
        }
        return -1;
    }

    private int getWinningMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            char curr = mBoard[i];
            if (curr != HUMAN_PLAYER && curr != COMPUTER_PLAYER) {
                // What if O moved here?
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                else
                    mBoard[i] = OPEN_SPOT;
            }
        }
        return -1;
    }

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel mDifficultyLevel) {
        this.mDifficultyLevel = mDifficultyLevel;
    }

	public char getBoardOccupant(int location) {
		if (location >= 0 && location < BOARD_SIZE)
			return mBoard[location];
		return '?';
	}

	public char[] getBoardState() {
		return mBoard;
	}

	public void setBoardState(char[] board) {
		mBoard = board.clone();
	}

    @Override
    public String toString() {
        return mBoard[0] + "|" + mBoard[1] + "|" + mBoard[2] + "\n" +
                mBoard[3] + "|" + mBoard[4] + "|" + mBoard[5] + "\n" +
                mBoard[6] + "|" + mBoard[7] + "|" + mBoard[8];

    }
}

