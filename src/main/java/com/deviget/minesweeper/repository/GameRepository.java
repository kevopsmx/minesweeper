package com.deviget.minesweeper.repository;
import org.springframework.data.repository.CrudRepository;
import com.deviget.minesweeper.model.Game;

public interface GameRepository extends CrudRepository<Game, String>{

}
