package com.deviget.minesweeper.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

/**
 * Game representation
 * @author Martín Cobián
 *
 */
@Entity
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

	@Getter
	private Boolean playable;

	//the game board, in an unidimensional array, representing the multidimensional cells in "x"
	private String board[];
	
	private Long lastMoveTime;

	@Getter
	private Long elapsedTime;

	//CONSTANTS representing the values in the cells
	private final char MINE = 'M';
	private final char COVER = 'C';
	private final char FLAG = 'F';
	private final char QUESTION = 'Q';

	/**
	 * Initialize a new game
	 * Set mines randomly
	 * Compute adjacent mines for each cell
	 * @param cols Number of columns
	 * @param rows Number of rows
	 * @param mines Number of mines
	 * @param user UserId for the game
	 */
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

		//put the adjacencies
		for(int i=0;i<board.length;i++) {
			if(board[i].indexOf(MINE)!=-1) {
				//1st -1y -1x
				//2nd -1y -0x
				//3rd -1y +1x
				//4th -0y -1x
				//5th -0y +1x
				//6th +1y -1x
				//7th +1y +0x
				//8th +1y +1x

				ArrayList<Integer> adjacents = new ArrayList<Integer>();
				int x= i % cols;
				int y= i / cols;

				//-1y
				if(y-1 >= 0) {
					if(x-1 >= 0) {
						adjacents.add(i - rows - 1);
					}
					adjacents.add(i - rows);
					if(x+1 < cols)
						adjacents.add(i - rows +1);
				}
				//+0y
				if(x-1 >= 0)
					adjacents.add(i - 1);
				if(x+1 < cols)
					adjacents.add(i + 1);
				
				//+1y
				if(y+1 < rows) {
					if(x-1 >= 0)
						adjacents.add(i + rows -1);
					adjacents.add(i + rows);
					if(x + 1 < cols)
						adjacents.add(i + rows +1);
				}
				
				adjacents.forEach(currentAdj -> {
					if(board[currentAdj].indexOf(MINE)==-1) {
						String currentAdjString = board[currentAdj].substring(1);
						
						if(currentAdjString.length()==0)
							board[currentAdj] = board[currentAdj] + "1";
						else {
							int currentAdjInt = Integer.parseInt(currentAdjString) + 1;
							board[currentAdj] = COVER + "" + currentAdjInt;
						}						
					}
				});
			}
		}
		lastMoveTime = Calendar.getInstance().getTimeInMillis();
		elapsedTime =0l;
	}

	/**
	 * Init an custom game, TEST ONLY
	 * @param cols Number of columns
	 * @param rows Number of rows
	 * @param mines Number of mines
	 * @param user User owning the game
	 * @param id Game id
	 * @param board Board array
	 * @param elapsedTime Elapsed time
	 * @param lastMoveTime Time of the last move
	 * @param playable if it is playable or not
	 */
	private Game(int cols, int rows, int mines, String user,String id, String [] board, Long elapsedTime, Long lastMoveTime,Boolean playable){
		this.cols = cols;
		this.rows = rows;
		this.mines = mines;
		this.id = id;
		this.board = board;
		this.elapsedTime = elapsedTime;
		this.lastMoveTime = lastMoveTime;
		this.playable = playable;
	}

	/**
	 * Method used to adjust the elapsed time
	 */
	private void adjustTime() {
		Long currentTime = Calendar.getInstance().getTimeInMillis();
		Long timeFromLastMove = currentTime - lastMoveTime;
		elapsedTime = elapsedTime + timeFromLastMove;
		lastMoveTime = currentTime;
	}
	
	/**
	 * Make a "clearing" move in given ("x","y") position
	 * Only valid in COVERED positions having no FLAG or QUESTION
	 * @param x column, starts in 0
	 * @param y row, starts in 0 
	 * @throws Exception
	 */
	public void clear(int x, int y) throws Exception{
		if(x >= 0 && x < cols && y >= 0 && y < rows && playable) {
			int pos = y*rows + x;
			adjustTime();
			if(board[pos].indexOf(COVER)!=-1 && board[pos].indexOf(FLAG) ==-1 && board[pos].indexOf(QUESTION)==-1) {
				//clear this one
				board[pos] = board[pos].replace(""+COVER, "");
				//if it is a MINE, the game finishes
				if(board[pos].indexOf(MINE)!=-1) {
					playable = false;
					throw new Exception("lost");
				}else {
					//Check if the user has won
					if(checkForWin()) {
						playable = false;
						throw new Exception ("you won!");
					}
					
					//clear adjacents, if the cell is empty after clearing
					if(board[pos].length()==0) {
						//upper row
						//x-1,y-1
						clear(x-1,y-1);
						//x,y-1
						clear(x,y-1);
						//x+1,y-1
						clear(x+1,y-1);
						
						//same row
						//x-1,y
						clear(x-1,y);
						//x+1, y
						clear(x + 1,y);
						
						//next row
						//x-1,y+1
						clear(x-1,y+1);
						//x,y+1
						clear(x,y+1);
						//x+1,y+1
						clear(x+1,y+1);
					}
				}
			}
		}
	}

	/**
	 * Put a flag in a given position
	 * Only when position is COVERED
	 * Replaces any existing QUESTION with a FLAG
	 * @param x , column to put the FLAG on
	 * @param y, row to put the FLAG on
	 */
	public void putFlag(int x, int y) {
		if(x >= 0 && x < cols && y >= 0 && y < rows && playable) {
			adjustTime();
			int pos = y*rows + x;
			if(board[pos].indexOf(COVER)!=-1) {
				board[pos] = board[pos].replace(""+QUESTION,"");
				if(board[pos].indexOf(FLAG)==-1)
					board[pos] = board[pos] + FLAG;
				else {
					board[pos] = board[pos].replace(""+FLAG,"");
				}	
			}
		}
	}

	/**
	 * Put a QUESTION in a given position
	 * Only when position is COVERED
	 * Replaces any existing FLAG with a QUESTION
	 * @param x , column to put the QUESTION on
	 * @param y, row to put the QUESTION on
	 */
	public void putQuestion(int x, int y) {
		if(x >= 0 && x < cols && y >= 0 && y < rows && playable) {
			adjustTime();
			int pos = y*rows + x;
			if(board[pos].indexOf(COVER)!=-1) {
				board[pos] = board[pos].replace(""+FLAG,"");
				if(board[pos].indexOf(QUESTION)==-1)
					board[pos] = board[pos] + QUESTION;
				else
					board[pos] = board[pos].replace(""+QUESTION,"");
			}
		}
	}

	/**
	 * Count covered positions, if value equals mines it is a win
	 * @return if covered positions are equals to mine, it means a win
	 */
	public boolean checkForWin() {
		//check for wins
		int covered = 0;
		for(int i=0;i<board.length;i++) {
			if(board[i].indexOf("C")!=-1) 
				covered++;
		}
		return covered==mines;
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
	 * C covered
	 * M mine
	 * # nearby mines
	 * F User Flag
	 * Q User Question
	 * blank. Being uncovered with no flag,question,mine or adjacent mine
	 * 
	 * combinations:
	 * CM Covered mine
	 * C# Covered adjacent mine counter
	 * CQM Covered and Question marked mine
	 * CFM Covered and Flag marked mine
	 * CQ# Covered and Question marked mine counter
	 * CF# Covered and Flagged mine counter
	 * # Uncovered mine counter 
	 * blank No mine and no counter uncovered position
	 * @return the board
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

	/**
	 * Displays the playable board, can show it to the user
	 * Shows only FLAGS
	 * Shows only QUESTIONS
	 * Shows only ADJACENT MINES
	 * Shows cleared cells
	 * Shows COVERED cells
	*/
	public String displayPlayableBoard() {
		StringBuilder bld = new StringBuilder();
		for(int y=0;y<rows;y++) {
			for(int x=0;x<cols;x++) {
				int pos = y * rows + x;
				if(board[pos].indexOf("C")==-1) {
					if(board[pos].length()==0) {
						bld.append(" ");
					} else {
						bld.append(board[pos]);
					}
				} else {
					if(board[pos].indexOf("Q")!=-1) {
						bld.append("Q");
					}else {
						if(board[pos].indexOf("F")!=-1) {
							bld.append("F");
						} else {
							bld.append("C");	
						}
					}
				}
				if(x+1<cols)
					bld.append(",");
			}
			bld.append("\n");
		}
		return bld.toString();
	}
	
	/**
	 * Main method to test the logic, not used in the SpringBoot/API implementation
	 * @param args
	 */
	public static void main(String args[]) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY,12);
		c.set(Calendar.MINUTE, 16);
		c.set(Calendar.MONTH, Calendar.SEPTEMBER);
		c.set(Calendar.DATE,30);
		c.set(Calendar.YEAR, 2021);
		
		String board[] ={"C","C","C","C","C","C2","C2","C1","C","C","CM","CM","C1","C","C","CM","C3","C1","C","C","C1","C1","C","C","C"}; 
		Game custom = new Game(5,5,3,"martin","sdsds1sd",board,0l,c.getTimeInMillis(),true);
		try {
			System.out.println(custom.displayPlayableBoard());
			
			custom.clear(0, 0);
			System.out.println("elapsed:" + custom.getElapsedTime());
			System.out.println(custom.displayBoard());
			System.out.println("-----");
			System.out.println(custom.displayPlayableBoard());
			
			
			custom.putFlag(0, 2);
			System.out.println(custom.displayBoard());
			System.out.println("-----");
			System.out.println(custom.displayPlayableBoard());
			custom.putQuestion(1, 2);
			System.out.println(custom.displayBoard());
			System.out.println("----");
			System.out.println(custom.displayPlayableBoard());
			custom.putFlag(0, 3);
			System.out.println(custom.displayBoard());
			System.out.println("----");
			System.out.println(custom.displayPlayableBoard());
			custom.clear(0, 4);
			System.out.println(custom.displayPlayableBoard());
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println(custom.displayBoard());
		}
	}
}
