package com.khiphach.benchmark.entity;

import com.khiphach.benchmark.enumeration.Windows;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Game {
    @Id
    @Column(length = 4)
    private String code;
    private String name;
    private int gpuMin;
    private int gpuMax;
    private int cpuMin;
    private int cpuMax;
    private int ramMin;
    private int ramMax;
    @Column(length = 4)
    private Windows windows;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGpuMin() {
        return gpuMin;
    }

    public void setGpuMin(int gpuMin) {
        this.gpuMin = gpuMin;
    }

    public int getGpuMax() {
        return gpuMax;
    }

    public void setGpuMax(int gpuMax) {
        this.gpuMax = gpuMax;
    }

    public int getCpuMin() {
        return cpuMin;
    }

    public void setCpuMin(int cpuMin) {
        this.cpuMin = cpuMin;
    }

    public int getCpuMax() {
        return cpuMax;
    }

    public void setCpuMax(int cpuMax) {
        this.cpuMax = cpuMax;
    }

    public int getRamMin() {
        return ramMin;
    }

    public void setRamMin(int ramMin) {
        this.ramMin = ramMin;
    }

    public int getRamMax() {
        return ramMax;
    }

    public void setRamMax(int ramMax) {
        this.ramMax = ramMax;
    }

    public Windows getWindows() {
        return windows;
    }

    public void setWindows(Windows windows) {
        this.windows = windows;
    }
}
