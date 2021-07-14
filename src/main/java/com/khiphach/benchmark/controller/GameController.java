package com.khiphach.benchmark.controller;

import com.khiphach.benchmark.entity.Game;
import com.khiphach.benchmark.enumeration.Windows;
import com.khiphach.benchmark.model.CheckResponse;
import com.khiphach.benchmark.model.GameDTO;
import com.khiphach.benchmark.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping
    public List<Game> getAllGames() {
        return gameService.getAllGames();
    }

    @PostMapping
    public Game createGame(@RequestBody GameDTO game) {
        return gameService.createGame(game);
    }

    @GetMapping("/create-by-link")
    public Game createGame(@RequestParam String link, @RequestParam String type) throws IOException {
        return gameService.createGame(link, type);
    }

    @GetMapping("/check")
    public CheckResponse checkGame(@RequestParam String code, @RequestParam String cpu, @RequestParam String gpu, @RequestParam int ram, @RequestParam Windows windows) {
        return gameService.checkGame(code, cpu, gpu, ram, windows);
    }

    @DeleteMapping
    public void deleteGame(@RequestParam String code) {
        gameService.deleteGame(code);
    }

}
