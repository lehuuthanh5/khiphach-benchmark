package com.khiphach.benchmark.service;

import com.khiphach.benchmark.entity.Game;
import com.khiphach.benchmark.enumeration.Status;
import com.khiphach.benchmark.enumeration.Windows;
import com.khiphach.benchmark.mapper.GameMapper;
import com.khiphach.benchmark.model.CheckResponse;
import com.khiphach.benchmark.model.GameDTO;
import com.khiphach.benchmark.model.Result;
import com.khiphach.benchmark.repository.GameDAO;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class GameService {
    @Autowired
    private GameDAO gameDAO;
    @Autowired
    private BenchMarkService benchMarkService;
    @Autowired
    private GameMapper gameMapper;

    public List<Game> getAllGames() {
        return gameDAO.findAll();
    }

    public Game createGame(GameDTO gameDTO) {
        Game game = gameMapper.gameDTOtoGame(gameDTO);
        Result resultMin = benchMarkService.getBenchMark(gameDTO.getCpuMin(), gameDTO.getGpuMin());
        Result resultMax = benchMarkService.getBenchMark(gameDTO.getCpuMax(), gameDTO.getGpuMax());
        game.setCpuMin(resultMin.getCpu());
        game.setCpuMax(resultMax.getCpu());
        game.setGpuMin(resultMin.getGpu());
        game.setGpuMax(resultMax.getGpu());
        if (gameDAO.existsById(game.getCode())) {
            throw new IllegalIdentifierException("Game code is existing");
        }
        return gameDAO.save(game);
    }

    public void deleteGame(String code) {
        gameDAO.deleteById(code);
    }


    public CheckResponse checkGame(String code, String cpu, String gpu, int ram, Windows windows) {
        Game game = gameDAO.findById(code).orElse(null);
        if (game == null) {
            throw new IllegalIdentifierException("Invalid game code");
        }
        Result result = benchMarkService.getBenchMark(cpu, gpu);
        List<Status> checkList = Arrays.asList(checkCPU(result.getCpu(), game), checkGPU(result.getGpu(), game)
                , checkWindows(windows, game), checkRAM(ram, game));
        checkList.sort(Comparator.comparingInt(Enum::ordinal));
        CheckResponse checkResponse = new CheckResponse();
        Status status = checkList.get(0);
        checkResponse.setStatus(status);
        if (status == Status.CANNOT) {
            checkResponse.setMessage("Bác không thể chiến game này rồi!");
        } else {
            checkResponse.setMessage("Bác có thể chiến game này với " + status + " settings");
        }
        return checkResponse;
    }

    private Status checkWindows(Windows windows, Game game) {
        if (game.getWindows() == windows || game.getWindows() == Windows.BOTH) {
            return Status.HIGH;
        }
        return Status.CANNOT;
    }

    private Status checkCPU(int cpu, Game game) {
        int avg = (game.getCpuMax() + game.getCpuMin()) / 2;
        if (cpu < game.getCpuMin()) {
            return Status.CANNOT;
        }
        if (cpu >= game.getCpuMax()) {
            return Status.HIGH;
        }
        if (cpu < avg) {
            return Status.MINIMUM;
        } else {
            return Status.MEDIUM;
        }
    }

    private Status checkGPU(int gpu, Game game) {
        int avg = (game.getGpuMax() + game.getGpuMin()) / 2;
        if (gpu < game.getGpuMin()) {
            return Status.CANNOT;
        }
        if (gpu >= game.getGpuMax()) {
            return Status.HIGH;
        }
        if (gpu < avg) {
            return Status.MINIMUM;
        } else {
            return Status.MEDIUM;
        }
    }

    private Status checkRAM(int ram, Game game) {
        if (ram < game.getRamMin()) {
            return Status.CANNOT;
        }
        if (ram >= game.getRamMax()) {
            return Status.HIGH;
        }
        return Status.MEDIUM;
    }
}
