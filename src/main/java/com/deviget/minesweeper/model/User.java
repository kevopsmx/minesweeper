package com.deviget.minesweeper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * Class representing the user
 */
@Entity
public class User {
	
	@Id
	private String userId;
	private String name;
	private String lastName;
}
