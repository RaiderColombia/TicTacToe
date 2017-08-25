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
	
	public static final int BOARD_SIZE = 9;
	
	// Characters used to represent the human, computer, and open spots
	public static final char HUMAN_PLAYER = 'X';
	public static final char COMPUTER_PLAYER = 'O';
	public static final char OPEN_SPOT = ' ';
	
	// Random number generator
	private Random mRand; 
		
	// Represents the game board
	private char mBoard[];
	
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
	public void setMove(char player, int location) {
		if (location >= 0 && location < BOARD_SIZE &&
				mBoard[location] == OPEN_SPOT) {
			mBoard[location] = player;
		}
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
		
		int move;

		// First see if there's a move O can make to win
		for (int i = 0; i < BOARD_SIZE; i++) {
			if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
				char curr = mBoard[i];
				mBoard[i] = COMPUTER_PLAYER;
				if (checkForWinner() == 3) {
					mBoard[i] = OPEN_SPOT;   // Restore space
					return i;
				}
				else
					mBoard[i] = curr;
			}
		}

		// See if there's a move O can make to block X from winning
		for (int i = 0; i < BOARD_SIZE; i++) {
			if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
				char curr = mBoard[i];   // Save the current number
				mBoard[i] = HUMAN_PLAYER;
				if (checkForWinner() == 2) {
					mBoard[i] = OPEN_SPOT;   // Restore space
					return i;
				}
				else
					mBoard[i] = curr;
			}
		}

		// Generate random move
		do {
			move = mRand.nextInt(BOARD_SIZE);
		} while (mBoard[move] != OPEN_SPOT);
			
		return move;
		
	}
	
}

