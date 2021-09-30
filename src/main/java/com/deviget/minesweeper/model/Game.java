package com.deviget.minesweeper.model;

import java.util.ArrayList;
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

	@Getter
	private Boolean playable;

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
	}

	private Game(int cols, int rows, int mines, String user,String id, String [] board,Boolean playable){
		this.cols = cols;
		this.rows = rows;
		this.mines = mines;
		this.id = id;
		this.board = board;
		this.playable = playable;
	}

	public void clear(int x, int y) throws Exception{
		if(x >= 0 && x < cols && y >= 0 && y < rows && playable) {
			int pos = y*rows + x;
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
		String board[] ={"C","C","C","C","C","C2","C2","C1","C","C","CM","CM","C1","C","C","CM","C3","C1","C","C","C1","C1","C","C","C"}; 
		Game custom = new Game(5,5,3,"martin","sdsds1sd",board,true);
		try {
			custom.clear(0, 0);
			System.out.println(custom.displayBoard());
			custom.clear(1, 0);
			System.out.println(custom.displayBoard());
			custom.clear(0, 4);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println(custom.displayBoard());
		}
	}
}
