package com.khiphach.benchmark;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BenchMarkApplication {

	private static final String GOOGLE_BASE_QUERY = "https://www.google.com.vn/search?q=%s passmark";
	private static final String TAG_H3 = "h3";
	private static final String TAG_A = "a";
	private static final String TAG_HREF = "href";
	private static final String TAG_STYLE = "style";
	private static final String PASSMARK_BENCHMARK_FIELD = "font-family: Arial, Helvetica, sans-serif;font-size: 35px;	font-weight: bold; color: red;";

	public static void main(String[] args) {
		SpringApplication.run(BenchMarkApplication.class, args);
	}

	@GetMapping("/get")
	public ResponseEntity<Integer> getBenchMark(@RequestParam(required = false) String gpu,
			@RequestParam(required = false) String cpu) {
		int totalBenchmark = 0;
		try {
			if (gpu != null) {
				totalBenchmark += Integer
						.valueOf(getPassmarkBenchmark(getFirstResultOfGoogleResult(gpu)));
			}
			if (cpu != null) {
				totalBenchmark += Integer.valueOf(getFirstResultOfGoogleResult(cpu));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(0);
		}
		return ResponseEntity.ok(totalBenchmark);
	}

	private String getFirstResultOfGoogleResult(String q) throws IOException {
		return Jsoup.connect(String.format(GOOGLE_BASE_QUERY, q)).userAgent(
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				.get().getElementsByAttributeValue("class", "r").get(0).getElementsByTag(TAG_A).get(0).attr("href");
	}

	@SuppressWarnings("unused")
	private String decodeAndGetResultLink(String input) throws UnsupportedEncodingException {
		String decodeInput = URLDecoder.decode(input, StandardCharsets.UTF_8.toString());
		return decodeInput.substring(0, decodeInput.indexOf('&', decodeInput.indexOf('&') + 1));
	}

	private String getPassmarkBenchmark(String link) throws IOException {
		return Jsoup.connect(link).userAgent(
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				.get().getElementsByAttributeValue(TAG_STYLE, PASSMARK_BENCHMARK_FIELD).get(0).text();
	}
}
