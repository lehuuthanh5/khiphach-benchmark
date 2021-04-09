package com.khiphach.benchmark.model;

import com.khiphach.benchmark.enumeration.Windows;

public class GameDTO {
    private String code;
    private String name;
    private String gpuMin;
    private String gpuMax;
    private String cpuMin;
    private String cpuMax;
    private int ramMin;
    private int ramMax;
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

    public String getGpuMin() {
        return gpuMin;
    }

    public void setGpuMin(String gpuMin) {
        this.gpuMin = gpuMin;
    }

    public String getGpuMax() {
        return gpuMax;
    }

    public void setGpuMax(String gpuMax) {
        this.gpuMax = gpuMax;
    }

    public String getCpuMin() {
        return cpuMin;
    }

    public void setCpuMin(String cpuMin) {
        this.cpuMin = cpuMin;
    }

    public String getCpuMax() {
        return cpuMax;
    }

    public void setCpuMax(String cpuMax) {
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
