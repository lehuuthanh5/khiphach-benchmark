package com.khiphach.benchmark;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import info.debatty.java.stringsimilarity.Cosine;

@SpringBootApplication
@RestController
@CrossOrigin
public class BenchMarkApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(BenchMarkApplication.class);

	private Properties cpus = new Properties();

	public BenchMarkApplication() throws IOException {
		try (InputStream is = new FileInputStream(ResourceUtils.getFile("classpath:cpu.properties"))) {
			cpus.load(is);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(BenchMarkApplication.class, args);
	}

	@GetMapping("/get")
	public ResponseEntity<Result> getBenchMark(@RequestParam(required = false) String gpu,
			@RequestParam(required = false) String cpu) {
		Cosine jw = new Cosine();
		Result result = new Result();
		final CustomeDouble cd = new CustomeDouble();
		cd.doubleValue = -1;
		cpus.forEach((key, value) -> {
			double temp = jw.similarity(cpu, key.toString());
			LOGGER.info("compare score {} - {}: {}", key, value, temp);
			if (cd.doubleValue < temp) {
				cd.doubleValue = temp;
				result.setCpu(Integer.parseInt(value.toString()));
			}
		});
		return ResponseEntity.ok(result);
	}

	private class CustomeDouble {
		double doubleValue;
	}

	public static class Result {
		private int gpu;
		private int cpu;
		private int total;

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
}
