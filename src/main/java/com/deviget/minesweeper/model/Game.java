package com.deviget.minesweeper.model;

import java.util.Random;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

public class Game {
	
	@Getter
	@Setter
	private Integer cols = 0;

	@Getter
	@Setter
	private Integer rows = 0;

	@Getter
	@Setter
	private Integer mines = 0;

	@Getter
	@Setter
	@Id
	private String id;

	@Getter
	@Setter
	private String user;

	private String board[];

	//CONSTANTS representing the values in the cells
	private final char MINE = 'M';
	private final char COVER = 'C';
	private final char FLAG = 'F';
	private final char QUESTION = 'Q';

	public Game(int cols, int rows, int mines, String user){
		this.cols = cols;
		this.rows = rows;
		this.mines = mines;
		this.id = generateId();
		this.user = user;
		int total = cols*rows;
		
		//init the board
		board = new String[cols*rows];
		
		//initialize everything as covered
		for(int i=0;i<cols*rows;i++) {
			board[i] = "" + COVER;
		}

		//put the mines on random positions
		Random random = new Random();
		for(int i=0;i<mines;) {
			int current = random.nextInt(total-1);
			if(board[current].indexOf(MINE) == -1) {
				board[current] = board[current] + MINE;
				i++;
			}
		}
	}
	
	/**
	 * Generate the random alphanumeric gameID
	 * @return gameId
	 */
	private String generateId() {
	    int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	    return generatedString;
	}

	/**
	 * Display the board in human readable form, comma sepparated rows and columns
	*/
	public String displayBoard() {
		StringBuilder bld = new StringBuilder();
		for(int y=0;y<rows;y++) {
			for(int x=0;x<cols;x++) {
				int pos = y * rows + x;
				bld.append(board[pos]);
				if(x+1<cols)
					bld.append(",");
			}
			bld.append("\n");
		}
		return bld.toString();
	}

	public static void main(String args[]) {
		Game game = new Game(5,5,1,"martin");
		System.out.println(game.displayBoard());
	}
}
