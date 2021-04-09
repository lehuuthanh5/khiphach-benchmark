package com.khiphach.benchmark.controller;

import com.khiphach.benchmark.entity.Game;
import com.khiphach.benchmark.enumeration.Windows;
import com.khiphach.benchmark.model.CheckResponse;
import com.khiphach.benchmark.service.BenchMarkService;
import com.khiphach.benchmark.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Game createGame(@RequestBody Game game) {
        return gameService.createGame(game);
    }

    @PutMapping
    public Game updateGame(@RequestBody Game game) {
        return gameService.updateGame(game);
    }

    @GetMapping("/check")
    public CheckResponse checkGame(@RequestParam String code, @RequestParam String cpu, @RequestParam String gpu, @RequestParam int ram, @RequestParam Windows windows) {
        return gameService.checkGame(code, cpu, gpu, ram, windows);
    }

}
