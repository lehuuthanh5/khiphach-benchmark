package com.khiphach.benchmark.mapper;

import com.khiphach.benchmark.entity.Game;
import com.khiphach.benchmark.model.GameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(target = "gpuMin", ignore = true)
    @Mapping(target = "gpuMax", ignore = true)
    @Mapping(target = "cpuMin", ignore = true)
    @Mapping(target = "cpuMax", ignore = true)
    Game gameDTOtoGame(GameDTO gameDTO);
}
