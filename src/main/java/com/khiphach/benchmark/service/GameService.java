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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class GameService {
    @Autowired
    private GameDAO gameDAO;
    @Autowired
    private BenchMarkService benchMarkService;
    @Autowired
    private GameMapper gameMapper;
    private final Map<String, String> type = new HashMap<>();
    private List<String> gameNames;

    @PostConstruct
    public void postConstruct() {
        gameNames = StreamSupport.stream(gameDAO.findAll().spliterator(), false)
                .collect(Collectors.toList()).stream().map(Game::getName).collect(Collectors.toList());
    }

    public List<String> searchGame(String text) {
        //[Gg].*\s[tT].*\s[aA].*
        StringBuilder regex = new StringBuilder("[");
        for (int i = 0; i < text.length(); i++) {
            regex.append(String.valueOf(text.charAt(i)).toUpperCase(Locale.ROOT));
            regex.append(String.valueOf(text.charAt(i)).toLowerCase(Locale.ROOT));
            if (i != (text.length() - 1)) {
                regex.append("].*\\s[");
            }
        }
        regex.append("].*");
        String pattern = regex.toString();
        return gameNames.stream().filter(s -> s.toLowerCase(Locale.ROOT).startsWith(text)
                || Pattern.matches(pattern, s)).limit(5).collect(Collectors.toList());
    }

    public List<Game> getAllGames() {
        return StreamSupport.stream(gameDAO.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Game createGame(String link) throws IOException {
        GameDTO game = new GameDTO();
        Document document = Jsoup.connect(link).userAgent("Mozilla").timeout(1000000).get();
        String gameName = document.getElementsByAttributeValue("itemprop", "name").get(0).text().replace("System Requirements", "").trim();
        game.setName(gameName);
        String translatedType = Stream.of(document.getElementsByClass("gameGenreRow").get(0).text().split(",")).map(s -> type.get(s.trim()))
                .filter(Objects::nonNull).collect(Collectors.joining(", "));
        game.setType(translatedType);
        game.setCode(gameName.replace(" ", "").toLowerCase(Locale.ROOT));
        boolean minExtracted = extractedMin(game, document);
        boolean maxExtracted = extractedMax(game, document);
        if (minExtracted || maxExtracted) {
            return createGame(game);
        }
        return null;
    }

    public Game createGame(GameDTO gameDTO) {
        if (!StringUtils.hasText(gameDTO.getCpuMin()) && !StringUtils.hasText(gameDTO.getCpuMax())) {
            return null;
        }
        Game game = gameMapper.gameDTOtoGame(gameDTO);
        Result resultMin = benchMarkService.getBenchMark(gameDTO.getCpuMin(), gameDTO.getGpuMin());
        Result resultMax = benchMarkService.getBenchMark(gameDTO.getCpuMax(), gameDTO.getGpuMax());
        game.setCpuMin(resultMin.getCpu());
        game.setCpuMax(resultMax.getCpu());
        game.setGpuMin(resultMin.getGpu());
        game.setGpuMax(resultMax.getGpu());
        return gameDAO.save(game);
    }

    public void deleteGame(String code) {
        gameDAO.deleteById(code);
    }

    public CheckResponse checkGame(String code, String cpu, String gpu, int ram, Windows windows) {
        Game game = gameDAO.findById(code).orElse(null);
        if (game == null) {
            throw new IllegalIdentifierException("Không tìm ra game đã nhập!");
        }
        Result result = benchMarkService.getBenchMark(cpu, gpu);
        return getCheckResponse(ram, windows, game, result);
    }

    private CheckResponse getCheckResponse(int ram, Windows windows, Game game, Result result) {
        List<Status> checkList = Arrays.asList(checkCPU(result.getCpu(), game), checkGPU(result.getGpu(), game)
                , checkWindows(windows, game), checkRAM(ram, game));
        checkList.sort(Comparator.comparingInt(Enum::ordinal));
        CheckResponse checkResponse = new CheckResponse();
        Status status = checkList.get(0);
        checkResponse.setStatus(status);
        checkResponse.setGame(game.getName());
        checkResponse.setType(game.getType());
        if (status == Status.CANNOT) {
            checkResponse.setMessage("Bác không thể chiến game này rồi!");
        } else {
            checkResponse.setMessage("Bác có thể chiến game này với " + status + " settings");
        }
        return checkResponse;
    }

    public List<CheckResponse> canPlayGame(String cpu, String gpu, int ram, Windows windows, int page) {
        Result result = benchMarkService.getBenchMark(cpu, gpu);
        Pageable pageable = PageRequest.of(page, 10);
        return gameDAO.findAllByGpuMinLessThanEqualAndCpuMinLessThanEqualAndRamMinLessThanEqualAndWindowsMin(result.getGpu(), result.getCpu(), ram, windows, pageable)
                .stream().map(game -> getCheckResponse(ram, windows, game, result)).collect(Collectors.toList());
    }

    private boolean extractedMax(GameDTO game, Document document) {
        Elements devDefSysReqRecWrapper = document.getElementsByClass("devDefSysReqRecWrapper");
        if (devDefSysReqRecWrapper.isEmpty()) {
            return false;
        }
        Elements max = devDefSysReqRecWrapper.get(0)
                .getElementsByTag("li");
        AtomicBoolean osFound = new AtomicBoolean(false);
        max.forEach(element -> {
            String text = element.text();
            if (text.contains("OS: ") || text.contains("Windows")) {
                if (text.contains("32")) {
                    game.setWindowsMax(Windows.BOTH);
                } else {
                    game.setWindowsMax(Windows.X64);
                }
                game.setOsMaxDesc(text.replace("OS: ", ""));
                osFound.set(true);
            }
            if (text.contains("Processor: ") || text.contains("PROCESSOR: ") || text.contains("Processor (Intel): ")
                    || text.contains("CPU : ") || text.contains("Core i") || text.contains("CPU: ")) {
                String desc = text.replace("Processor: ", "")
                        .replace("PROCESSOR: ", "")
                        .replace("CPU : ", "")
                        .replace("CPU: ", "")
                        .replace("Processor (Intel): ", "").trim();
                game.setCpuMax(desc.split("/")[0].trim().split(" or ")[0].trim().split(" OR ")[0].trim());
                game.setCpuMaxDesc(desc);
            }
            if (text.contains("Graphics: ") || text.contains("VIDEO CARD: ") || text.contains("Graphics card (NVIDIA): ") || text.contains("GPU : ")
                    || text.contains("NVIDIA") || text.contains("GeForce") || text.contains("GTX")) {
                String desc = text.replace("Graphics: ", "")
                        .replace("VIDEO CARD: ", "")
                        .replace("GPU : ", "")
                        .replace("Graphics card (NVIDIA): ", "")
                        .trim();
                game.setGpuMax(desc.split(" or ")[0].trim().split(" / ")[0].trim());
                game.setGpuMaxDesc(desc);
            }
            if (text.contains("System Memory: ") || text.contains("Memory: ")
                    || text.contains("SYSTEM RAM: ")
                    || text.contains("RAM : ") || text.contains("RAM: ")
                    || text.contains(" RAM")
                    || text.contains("GB Ram")
                    || text.contains("GB RAM")) {
                String desc = text.replace("System Memory: ", "")
                        .replace("Memory: ", "")
                        .replace("SYSTEM RAM: ", "")
                        .replace("RAM : ", "")
                        .replace("RAM: ", "")
                        .replace(" GB RAM", "")
                        .replace("GB RAM", "")
                        .replace("GB Ram", "")
                        .replace("GB", "")
                        .replace("RAM", "")
                        .replace("GB*", "")
                        .replace("MB", "").trim();
                int ramMax = Integer.parseInt(desc);
                if (text.contains("GB")) {
                    ramMax = ramMax * 1024;
                }
                game.setRamMax(ramMax);
            }
            if (text.contains("Storage: ")) {
                String desc = text.replace("Storage: ", "")
                        .replace(" GB Hard drive space", "")
                        .replace(" GB available space", "")
                        .replace(" MB Hard drive space", "").trim();
                int storage = (int) Float.parseFloat(desc);
                if (text.contains(" GB ")) {
                    storage = storage * 1024;
                }
                game.setStorage(storage);
            }
        });
        if (!osFound.get()) {
            game.setWindowsMax(Windows.BOTH);
        }
        return true;
    }

    private boolean extractedMin(GameDTO game, Document document) {
        Elements devDefSysReqMinWrapper = document.getElementsByClass("devDefSysReqMinWrapper");
        if (devDefSysReqMinWrapper.isEmpty()) {
            return false;
        }
        Elements min = devDefSysReqMinWrapper.get(0)
                .getElementsByTag("li");
        AtomicBoolean osFound = new AtomicBoolean(false);
        min.forEach(element -> {
            String text = element.text();
            if (text.contains("OS: ")) {
                if (text.contains("32")) {
                    game.setWindowsMin(Windows.BOTH);
                } else {
                    game.setWindowsMin(Windows.X64);
                }
                game.setOsMinDesc(text.replace("OS: ", ""));
                osFound.set(true);
            }
            if (text.contains("Processor: ") || text.contains("PROCESSOR: ") || text.contains("Processor (Intel): ")
                    || text.contains("CPU : ") || text.contains("CPU: ")
                    || text.contains("Core i")) {
                String desc = text.replace("Processor: ", "")
                        .replace("PROCESSOR: ", "")
                        .replace("CPU : ", "")
                        .replace("CPU: ", "")
                        .replace("Processor (Intel): ", "")
                        .trim();
                game.setCpuMin(desc.split("/")[0].trim().split(" or ")[0].trim().split(" OR ")[0].trim());
                game.setCpuMinDesc(desc);
            }
            if (text.contains("Graphics: ") || text.contains("VIDEO CARD: ") || text.contains("Graphics card (NVIDIA): ")
                    || text.contains("GPU : ") || text.contains("NVIDIA") || text.contains("GeForce") || text.contains("GTX")) {
                String desc = text.replace("Graphics: ", "")
                        .replace("VIDEO CARD: ", "")
                        .replace("GPU : ", "")
                        .replace("Graphics card (NVIDIA): ", "")
                        .trim();
                game.setGpuMin(desc.split(" or ")[0].trim().split(" / ")[0].trim());
                game.setGpuMinDesc(desc);
            }
            if (text.contains("System Memory: ") || text.contains("SYSTEM RAM: ")
                    || text.contains("Memory: ") || text.contains("RAM : ")
                    || text.contains(" RAM")
                    || text.contains("RAM: ")
                    || text.contains("GB Ram")
                    || text.contains("GB RAM")) {
                String desc = text.replace("System Memory: ", "")
                        .replace("SYSTEM RAM: ", "")
                        .replace("Memory: ", "")
                        .replace("RAM : ", "")
                        .replace("RAM: ", "")
                        .replace(" GB RAM", "")
                        .replace("GB RAM", "")
                        .replace("GB", "")
                        .replace("GB*", "")
                        .replace("GB Ram", "")
                        .replace("RAM", "")
                        .replace(" MB RAM", "").trim();
                int ramMin = Integer.parseInt(desc);
                if (text.contains("GB")) {
                    ramMin = ramMin * 1024;
                }
                game.setRamMin(ramMin);
            }
            if (text.contains("Storage: ")) {
                String desc = text.replace("Storage: ", "")
                        .replace(" GB Hard drive space", "")
                        .replace(" GB available space", "")
                        .replace(" MB Hard drive space", "").trim();
                int storage = (int) Float.parseFloat(desc);
                if (text.contains(" GB ")) {
                    storage = storage * 1024;
                }
                game.setStorage(storage);
            }
        });
        if (!osFound.get()) {
            game.setWindowsMin(Windows.BOTH);
        }
        return true;
    }

    private Status checkWindows(Windows windows, Game game) {
        if (game.getWindowsMin() == windows || game.getWindowsMin() == Windows.BOTH) {
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
