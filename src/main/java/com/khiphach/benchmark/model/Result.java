package com.khiphach.benchmark.model;

public class Result {
    private int gpu;
    private int cpu;
    private int total;

    public Result() {
        gpu = 0;
        cpu = 0;
        total = 0;
    }

    public int getGpu() {
        return gpu;
    }

    public void setGpu(int gpu) {
        this.gpu = gpu;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void generateTotal() {
        this.total = this.cpu + this.gpu;
    }
}
