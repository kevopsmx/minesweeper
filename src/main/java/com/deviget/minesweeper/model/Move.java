package com.deviget.minesweeper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
/**
 * Class representing a move
 * @author Martín Cobián
 *
 */

public class Move {
	
	private String userId;
	private String gameId;
	
	private Integer x;
	private Integer y;
	
	public boolean validate() {
		
		return userId!=null &&
				gameId !=null &&
				x!=null &&
				y!=null;
	}
}

