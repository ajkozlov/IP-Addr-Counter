package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * PI counter
 */
public class App {
	private static final String FILE_XL = "\\files\\ip_addresses";
	private static final String FILE_M = "\\files\\ip_list";
	private static final String FILE_S = "\\files\\ip_list_s";

	public static void main(String[] args) {
		Path path = Paths.get(System.getProperty("user.dir") + FILE_M);
		
		String type = args.length > 0 ? args[0] : "";
		if ("generation".equals(type)){
			generateIPsToFile(path);
		} else {
			AbstractCalculator calculator = switch (type) {
				case "m" -> new MemoryEffectiveCalculator();
				case "c" -> new ConcurrentCalculator();
				default -> new NaiveCalculator();
			};
			calculator.start(path);
		}
	}

	private static void generateIPsToFile(Path path) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i <= 5000000; i++) {
			int[] ip = new int[4];
			for (int j = 0; j < 4; j++) {
				ip[j] = (int) Math.round(Math.random() * 255);
			}
			str.append("\n").append(Arrays.stream(ip).mapToObj(Integer::toString).collect(Collectors.joining(".")));
		}
		try {
			Files.writeString(path, str.toString(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
