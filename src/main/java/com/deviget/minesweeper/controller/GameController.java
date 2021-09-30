package com.deviget.minesweeper.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deviget.minesweeper.model.Game;
import com.deviget.minesweeper.model.Move;
import com.deviget.minesweeper.model.Response;
import com.deviget.minesweeper.model.User;
import com.deviget.minesweeper.repository.GameRepository;
import com.deviget.minesweeper.repository.UserRepository;

@RestController
public class GameController {
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	GameRepository gameRepo;
	
	@PostMapping("/newGame")
	public Response createGame(@RequestBody Game gameParam) {
		if(gameParam.getUser()!=null) {
			Optional<User> user = userRepo.findById(gameParam.getUser());
			if(user.isPresent()) {
				Game toSave = new Game(gameParam.getRows(), gameParam.getCols(), gameParam.getMines(), gameParam.getUser());
				Game saved = gameRepo.save(toSave);
				Optional<Game> found = gameRepo.findById(saved.getId());
				if(found.isPresent()) {
					Response response = new Response();
					response.setCode(200);
					response.setMessage(found.get().getId());
					return response;
				}
				Response error = new Response();
				error.setCode(500);
				error.setMessage("Error ocurred, game not created");
				return error;
			}else {
				Response userError = new Response();
				userError.setCode(500);
				userError.setMessage("Please check user");
				return userError;
			}
		}else {
			Response paramError = new Response();
			paramError.setCode(500);
			paramError.setMessage("Invalid params");
			return paramError;
		}
	}
	
	@GetMapping("/fullGameInfo")
	public Object findGameInfo(@RequestParam String gameId, @RequestParam String userId) {
		Optional<Game> game = gameRepo.findById(gameId);
		if(game.isPresent()) {
			if(game.get().getUser().equals(userId)) {
				return game.get();
			}
		}
		Response response = new Response("Game not found",500);
		return response;
	}
	
	
	@GetMapping("/displayBoard")
	public Object displayBoard(@RequestParam String gameId, @RequestParam String userId) {
		Optional<Game> game = gameRepo.findById(gameId);
		if(game.isPresent()) {
			if(game.get().getUser().equals(userId)) {
				return game.get().displayBoard();
			}
			return new Response("Game not found",500);
		}
		return new Response("Game not found",500);
	}
	
	@GetMapping("/playableBoard")
	public Object playableBoard(@RequestParam String gameId, @RequestParam String userId) {
		Optional<Game> game = gameRepo.findById(gameId);
		if(game.isPresent()) {
			if(game.get().getUser().equals(userId)) {
				return game.get().displayPlayableBoard();
			}
			return new Response("Game not found",500);
		}
		return new Response("Game not found",500);
	}
	
	@PostMapping("/makeMove")
	public Object makeMove(@RequestBody Move move) {
		Optional<Game> game = gameRepo.findById(move.getGameId());
		if(game.isPresent()) {
			if(game.get().getUser().equals(move.getUserId())) {
				Game realGame = game.get();
				try {
					if(realGame.getPlayable()) {
						realGame.clear(move.getX(), move.getY());
						gameRepo.save(realGame);
						return realGame.displayPlayableBoard();						
					}else {
						return new Response("Already Lost",500);
					}
				} catch(Exception e) {
					if(e.getMessage().equals("you won!")) {
						gameRepo.save(realGame);
						Long seconds = realGame.getElapsedTime();
						seconds = seconds / 1000;
						return new Response("Congratulations you won! in "+seconds +" seconds",200);
					}else {
						gameRepo.save(realGame);
						Long seconds = realGame.getElapsedTime();
						seconds = seconds / 1000;
						return new Response("Sorry you Loose, mine found in "+ seconds,500);
					}
				}
			}else {
				return new Response("Game not found",500);
			}
		}
		return new Response("Game not found",500);
	}

	@PostMapping("/markFlag")
	public Object markFlag(@RequestBody Move move) {
		Optional<Game> game = gameRepo.findById(move.getGameId());
		if(game.isPresent()) {
			if(game.get().getUser().equals(move.getUserId())) {
				Game realGame = game.get();
				if(realGame.getPlayable()) {
					realGame.putFlag(move.getX(), move.getY());
					gameRepo.save(realGame);
					return realGame.displayPlayableBoard();
				} else {
					return new Response("Sorry already lost",500);
				}
			} else {
				return new Response("Game not found",500);
			}
		}
		return new Response("Game not found",500);
	}
	
	@PostMapping("/markQuestion")
	public Object markQuestion(@RequestBody Move move) {
		Optional<Game> game = gameRepo.findById(move.getGameId());
		if(game.isPresent()) {
			if(game.get().getUser().equals(move.getUserId())) {
				Game realGame = game.get();
				if(realGame.getPlayable()) {
					realGame.putQuestion(move.getX(), move.getY());
					gameRepo.save(realGame);
					return realGame.displayPlayableBoard();
				} else {
					return new Response("Sorry already lost",500);
				}
			}
			return new Response("Game not found",500);
		}
		return new Response("Game not found",500);
	}
}
