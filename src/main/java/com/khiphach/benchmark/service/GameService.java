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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GameService {
    @Autowired
    private GameDAO gameDAO;
    @Autowired
    private BenchMarkService benchMarkService;
    @Autowired
    private GameMapper gameMapper;
    private final Map<String, String> type = new HashMap<>();

    public List<Game> getAllGames() {
        return gameDAO.findAll();
    }

    public Game createGame(String link) throws IOException {
        GameDTO game = new GameDTO();
        Document document = Jsoup.connect(link).userAgent("Mozilla").get();
        String gameName = document.getElementsByAttributeValue("itemprop", "name").get(0).text().replace("System Requirements", "").trim();
        game.setName(gameName);
        String translatedType = Stream.of(document.getElementsByClass("gameGenreRow").get(0).text().split(",")).map(s -> type.get(s.trim()))
                .filter(Objects::nonNull).collect(Collectors.joining(", "));
        game.setType(translatedType);
        game.setCode(gameName.replace(" ", "").toLowerCase(Locale.ROOT));
        extractedMin(game, document);
        extractedMax(game, document);
        return createGame(game);
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

    public List<Game> canPlayGame(String cpu, String gpu, int ram, Windows windows) {
        Result result = benchMarkService.getBenchMark(cpu, gpu);
        return gameDAO.findAllByGpuMinLessThanEqualAndCpuMinLessThanEqualAndRamMinLessThanEqualAndWindows(result.getGpu(), result.getCpu(), ram, windows);
    }

    private void extractedMax(GameDTO game, Document document) {
        Elements devDefSysReqRecWrapper = document.getElementsByClass("devDefSysReqRecWrapper");
        if (devDefSysReqRecWrapper.isEmpty()) {
            return;
        }
        Elements max = devDefSysReqRecWrapper.get(0)
                .getElementsByTag("li");
        max.forEach(element -> {
            String text = element.text();
            if (text.contains("OS: ")) {
                if (text.contains("32")) {
                    game.setWindows(Windows.BOTH);
                } else {
                    game.setWindows(Windows.X64);
                }
                game.setOsDesc(text.replace("OS: ", ""));
            }
            if (text.contains("Processor: ")) {
                String desc = text.replace("Processor: ", "").trim();
                game.setCpuMax(desc.split(" / ")[0].trim());
                game.setCpuMaxDesc(desc);
            }
            if (text.contains("Graphics: ")) {
                String desc = text.replace("Graphics: ", "").trim();
                game.setGpuMax(desc.split(" or ")[0].trim());
                game.setGpuMaxDesc(desc);
            }
            if (text.contains("System Memory: ")) {
                String desc = text.replace("System Memory: ", "")
                        .replace(" GB RAM", "")
                        .replace(" MB RAM", "").trim();
                int ramMax = Integer.parseInt(desc);
                if (text.contains(" GB ")) {
                    ramMax = ramMax * 1024;
                }
                game.setRamMax(ramMax);
            }
            if (text.contains("Storage: ")) {
                String desc = text.replace("Storage: ", "")
                        .replace(" GB Hard drive space", "")
                        .replace(" MB Hard drive space", "").trim();
                int storage = Integer.parseInt(desc);
                if (text.contains(" GB ")) {
                    storage = storage * 1024;
                }
                game.setStorage(storage);
            }
        });
    }

    private void extractedMin(GameDTO game, Document document) {
        Elements devDefSysReqMinWrapper = document.getElementsByClass("devDefSysReqMinWrapper");
        if (devDefSysReqMinWrapper.isEmpty()) {
            return;
        }
        Elements min = devDefSysReqMinWrapper.get(0)
                .getElementsByTag("li");
        min.forEach(element -> {
            String text = element.text();
            if (text.contains("OS: ")) {
                if (text.contains("32")) {
                    game.setWindows(Windows.BOTH);
                } else {
                    game.setWindows(Windows.X64);
                }
                game.setOsDesc(text.replace("OS: ", ""));
            }
            if (text.contains("Processor: ")) {
                String desc = text.replace("Processor: ", "").trim();
                game.setCpuMin(desc.split(" / ")[0].trim());
                game.setCpuMinDesc(desc);
            }
            if (text.contains("Graphics: ")) {
                String desc = text.replace("Graphics: ", "").trim();
                game.setGpuMin(desc.split(" or ")[0].trim());
                game.setGpuMinDesc(desc);
            }
            if (text.contains("System Memory: ")) {
                String desc = text.replace("System Memory: ", "")
                        .replace(" GB RAM", "")
                        .replace(" MB RAM", "").trim();
                int ramMin = Integer.parseInt(desc);
                if (text.contains(" GB ")) {
                    ramMin = ramMin * 1024;
                }
                game.setRamMin(ramMin);
            }
            if (text.contains("Storage: ")) {
                String desc = text.replace("Storage: ", "")
                        .replace(" GB Hard drive space", "")
                        .replace(" MB Hard drive space", "").trim();
                int storage = Integer.parseInt(desc);
                if (text.contains(" GB ")) {
                    storage = storage * 1024;
                }
                game.setStorage(storage);
            }
        });
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

    public GameService() {
        type.put("Action", "Hành Động");
        type.put("Adventure", "Phiêu Lưu");
        type.put("Arcade", "Giải Trí");
        type.put("Battle Royale", "Battle Royale");
        type.put("Co-op", "Co-op");
        type.put("Crime", "Tội Phạm");
        type.put("Fantasy", "Thần Thoại");
        type.put("Fighting", "Đối Kháng");
        type.put("Historic", "Lịch Sử");
        type.put("Horror", "Kinh Dị");
        type.put("Management", "Quản Lý");
        type.put("Metroidvania", "Metroidvania");
        type.put("MOBA", "MOBA");
        type.put("Mystery", "Huyền Bí");
        type.put("Platformer", "Đi Cảnh");
        type.put("Puzzler", "Giải Đố");
        type.put("PvP", "PvP");
        type.put("Racing", "Đua Xe");
        type.put("Retro", "Cổ Điển");
        type.put("Rhythm", "Âm Nhạc");
        type.put("RPG", "Nhập Vai");
        type.put("Shooter", "Bắn Súng");
        type.put("Sim", "Mô Phỏng");
        type.put("Social", "Xã Hội");
        type.put("Sport", "Thể Thao");
        type.put("Strategy", "Chiến Thuật");
        type.put("Survival", "Sinh Tồn");
        type.put("Turn-based", "Turn-based");
    }
}
