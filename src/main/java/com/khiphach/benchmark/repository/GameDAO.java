package com.khiphach.benchmark.repository;

import com.khiphach.benchmark.entity.Game;
import com.khiphach.benchmark.enumeration.Windows;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameDAO extends PagingAndSortingRepository<Game, String> {
    List<Game> findAllByGpuMinLessThanEqualAndCpuMinLessThanEqualAndRamMinLessThanEqualAndWindowsMin(int gpuMin, int cpuMin, int ramMin, Windows windowsMin, Pageable pageable);
}
