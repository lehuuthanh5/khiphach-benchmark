package com.khiphach.benchmark;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class GenerateProperties {

	public static Map<String, Integer> values = new HashMap<>();

	public static void genereteCPU() throws IOException {
		Elements elements = Jsoup.parse(new File("C:/Users/ADMINS/Desktop/cpu.html"), StandardCharsets.UTF_8.toString())
				.getElementById("cputable").getElementById("100body").getElementsByTag("tr");
		elements.forEach(e -> {
			if (e.hasAttr("role") && e.hasAttr("id")) {
				Elements es = e.getElementsByTag("td");
				values.put(es.get(0).getElementsByTag("a").get(1).text(), Integer.parseInt(es.get(2).text()));
			}
		});
		System.out.println(values);
	}

	public static void genereteGPU() throws IOException {
		Elements elements = Jsoup
				.parse(new File("C:/Users/ADMINS/Desktop/gpu.html"), StandardCharsets.UTF_8.toString())
				.getElementById("cputable").getElementById("100body").getElementsByTag("tr");
		elements.forEach(e -> {
			if (e.hasAttr("id") && e.hasAttr("role")) {
				Elements es = e.getElementsByTag("td");
				values.put(es.get(0).getElementsByTag("a").get(1).text(), Integer.parseInt(es.get(2).text()));
			}
		});
		System.out.println(values);
	}
}
