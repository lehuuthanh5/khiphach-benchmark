package com.khiphach.benchmark;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@CrossOrigin
public class BenchMarkApplication {

	private static final String GOOGLE_BASE_QUERY = "https://www.google.com.vn/search?q=%s passmark";
	private static final String TAG_A = "a";
	private static final String TAG_STYLE = "style";
	private static final String PASSMARK_BENCHMARK_FIELD = "font-family: Arial, Helvetica, sans-serif;font-size: 35px;	font-weight: bold; color: red;";
	private static final String AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
	private static final Map<String, Integer> CACHE_GPU = new HashMap<>();
	private static final Map<String, Integer> CACHE_CPU = new HashMap<>();
	private boolean isCached = false;

	public static void main(String[] args) {
		SpringApplication.run(BenchMarkApplication.class, args);
	}

	@GetMapping("/get")
	public ResponseEntity<Result> getBenchMark(@RequestParam(required = false) String gpu,
			@RequestParam(required = false) String cpu) {
		Result result = new Result();
		getGPUBenchmark(gpu, result);
		getCPUBenchmark(cpu, result);
		result.generateTotal();
		return ResponseEntity.ok(result);
	}

	private void getGPUBenchmark(String gpu, Result result) {
		try {
			if (gpu != null) {
				if (isCached) {
					String gpuLowerCase = gpu.toLowerCase();
					if (CACHE_GPU.containsKey(gpuLowerCase)) {
						result.setGpu(CACHE_GPU.get(gpuLowerCase));
					} else {
						int tmp = Integer.parseInt(getPassmarkBenchmark(getFirstResultOfGoogleResult(gpu)));
						result.setGpu(tmp);
						CACHE_GPU.put(gpuLowerCase, tmp);
					}
				} else {
					result.setGpu(Integer.parseInt(getPassmarkBenchmark(getFirstResultOfGoogleResult(gpu))));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setGpu(0);
		}
	}

	private void getCPUBenchmark(String cpu, Result result) {
		try {
			if (cpu != null) {
				if (isCached) {
					String cpuLowerCase = cpu.toLowerCase();
					if (CACHE_CPU.containsKey(cpuLowerCase)) {
						result.setCpu(CACHE_CPU.get(cpuLowerCase));
					} else {
						int tmp = Integer.parseInt(getPassmarkBenchmark(getFirstResultOfGoogleResult(cpu)));
						result.setCpu(tmp);
						CACHE_CPU.put(cpuLowerCase, tmp);
					}
				} else {
					result.setCpu(Integer.parseInt(getPassmarkBenchmark(getFirstResultOfGoogleResult(cpu))));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setCpu(0);
		}
	}

	private String getFirstResultOfGoogleResult(String q) throws IOException {
		return Jsoup.connect(String.format(GOOGLE_BASE_QUERY, q)).userAgent(AGENT).get()
				.getElementsByAttributeValue("class", "r").get(0).getElementsByTag(TAG_A).get(0).attr("href");
	}

	@SuppressWarnings("unused")
	private String decodeAndGetResultLink(String input) throws UnsupportedEncodingException {
		String decodeInput = URLDecoder.decode(input, StandardCharsets.UTF_8.toString());
		return decodeInput.substring(0, decodeInput.indexOf('&', decodeInput.indexOf('&') + 1));
	}

	private String getPassmarkBenchmark(String link) throws IOException {
		return Jsoup.connect(link).userAgent(AGENT).get()
				.getElementsByAttributeValue(TAG_STYLE, PASSMARK_BENCHMARK_FIELD).get(0).text();
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
