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
    private String gpuMinDesc;
    private String gpuMaxDesc;
    private String cpuMinDesc;
    private String cpuMaxDesc;
    private String osDesc;
    private int storage;
    private String type;

    public String getGpuMinDesc() {
        return gpuMinDesc;
    }

    public void setGpuMinDesc(String gpuMinDesc) {
        this.gpuMinDesc = gpuMinDesc;
    }

    public String getGpuMaxDesc() {
        return gpuMaxDesc;
    }

    public void setGpuMaxDesc(String gpuMaxDesc) {
        this.gpuMaxDesc = gpuMaxDesc;
    }

    public String getCpuMinDesc() {
        return cpuMinDesc;
    }

    public void setCpuMinDesc(String cpuMinDesc) {
        this.cpuMinDesc = cpuMinDesc;
    }

    public String getCpuMaxDesc() {
        return cpuMaxDesc;
    }

    public void setCpuMaxDesc(String cpuMaxDesc) {
        this.cpuMaxDesc = cpuMaxDesc;
    }

    public String getOsDesc() {
        return osDesc;
    }

    public void setOsDesc(String osDesc) {
        this.osDesc = osDesc;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
