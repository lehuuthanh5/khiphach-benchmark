package com.khiphach.benchmark.entity;

import com.khiphach.benchmark.enumeration.Windows;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class Game {
    @Id
    @Column(length = 40)
    private String code;
    private String name;
    private int gpuMin;
    private int gpuMax;
    private int cpuMin;
    private int cpuMax;
    private int ramMin;
    private int ramMax;
    @Column(length = 4)
    @Enumerated(EnumType.STRING)
    private Windows windows;
    private String gpuMinDesc;
    private String gpuMaxDesc;
    private String cpuMinDesc;
    private String cpuMaxDesc;
    private String osDesc;
    private int storage;
    private String type;

    public String getOsDesc() {
        return osDesc;
    }

    public void setOsDesc(String osDesc) {
        this.osDesc = osDesc;
    }

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
