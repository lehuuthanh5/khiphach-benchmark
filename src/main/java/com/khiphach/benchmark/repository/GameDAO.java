package com.khiphach.benchmark.repository;

import com.khiphach.benchmark.entity.Game;
import com.khiphach.benchmark.enumeration.Windows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameDAO extends JpaRepository<Game, String> {
    List<Game> findAllByGpuMinLessThanEqualAndCpuMinLessThanEqualAndRamMinLessThanEqualAndWindows(int gpuMin, int cpuMin, int ramMin, Windows windows);
}
