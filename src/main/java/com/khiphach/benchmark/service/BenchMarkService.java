package com.khiphach.benchmark.service;

import com.khiphach.benchmark.model.CustomDouble;
import com.khiphach.benchmark.model.Result;
import info.debatty.java.stringsimilarity.Cosine;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class BenchMarkService {
    private final Properties cpus = new Properties();
    private final Properties gpus = new Properties();

    public BenchMarkService() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("cpu.properties")) {
            cpus.load(is);
        }
        try (InputStream is = classLoader.getResourceAsStream("gpu.properties")) {
            gpus.load(is);
        }
    }

    public Result getBenchMark(String cpu, String gpu) {
        Result result = new Result();
        Cosine jw = new Cosine();
        final CustomDouble cd = new CustomDouble();
        cd.setDoubleValue(-1);
        if (cpu != null) {
            cpus.forEach((key, value) -> {
                double temp = jw.similarity(cpu, key.toString());
                if (cd.getDoubleValue() < temp) {
                    cd.setDoubleValue(temp);
                    result.setCpu(Integer.parseInt(value.toString()));
                }
            });
        }
        cd.setDoubleValue(-1);
        if (gpu != null) {
            gpus.forEach((key, value) -> {
                double temp = jw.similarity(gpu, key.toString());
                if (cd.getDoubleValue() < temp) {
                    cd.setDoubleValue(temp);
                    result.setGpu(Integer.parseInt(value.toString()));
                }
            });
        }
        result.generateTotal();
        return result;
    }
}
