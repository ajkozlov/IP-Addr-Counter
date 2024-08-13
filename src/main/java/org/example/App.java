package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


/**
 * PI counter
 */
public class App {
	final boolean[][][][] ips = new boolean[256][256][256][256];
	AtomicLong sum = new AtomicLong(0L);
	int batchCounter = 0;
	private static final String FILE_XL = "\\files\\ip_addresses";
	private static final String FILE_M = "\\files\\ip_list";

	public static void main(String[] args) throws IOException {
		Path path = Paths.get(System.getProperty("user.dir") + FILE_M);
		App app = new App();
		long startTime, endTime, count;
		
		startTime = System.currentTimeMillis();
		BufferedReader readerF = Files.newBufferedReader(path);
		count = app.getUniqueIPsFancy(readerF);
		endTime = System.currentTimeMillis();
		System.out.println("Fancy(" + (endTime - startTime) + "): " + count);
		
		startTime = System.currentTimeMillis();
		BufferedReader readerN = Files.newBufferedReader(path);
		count = getUniqueIPsNaive(readerN);
		endTime = System.currentTimeMillis();
		System.out.println("Naive(" + (endTime - startTime) + "): " + count);
		
		//        generateIPsToFile();
	}

	private long getUniqueIPsFancy(BufferedReader reader) {
		int step = 10000000;
		char[] buff = new char[step + 16];
		char[] extBuff = new char[1];
		int read = step;
		ExecutorService executorService = Executors.newFixedThreadPool(50);
		try {
			while (read >= step) {
				read = reader.read(buff, 0, step);
				while (buff[read - 1] != '\n' && read >= step) {
					read += reader.read(extBuff, 0, 1);
					buff[read - 1] = extBuff[0];
				}
				char[] batch = Arrays.copyOfRange(buff, 0, read);
				executorService.execute(() -> {
					calcBatch(prepareIntList(batch));
				});
			}
			executorService.shutdown();
			executorService.awaitTermination(10, TimeUnit.SECONDS);
		} catch (IOException | InterruptedException e) {
			System.out.println(e.getMessage());
		}
		return sum.get();
	}

	private List<int[]> prepareIntList(char[] buff) {
		List<int[]> list = new ArrayList<>();
		int part = 0, j = 0;
		int[] ip = new int[4];
		for (char c : buff) {
			if (c >= '0') {
				part = (part * 10) + c - 48;
			} else if (c == '.') {
				ip[j++] = part;
				part = 0;
			} else if (c == '\n') {
				ip[j] = part;
				list.add(ip);
				ip = new int[4];
				part = 0;
				j = 0;
			}
		}
		return list;
	}
	
	private void calcBatch(List<int[]> ips) {
		ips.forEach(this::calculate);
		System.out.println("batch(" + ips.size() + ")" + batchCounter++);
	}

	private void calculate(int[] ip) {
		if (!ips[ip[0]][ip[1]][ip[2]][ip[3]]) {
			ips[ip[0]][ip[1]][ip[2]][ip[3]] = true;
			sum.getAndIncrement();
		}
	}

	private static long getUniqueIPsNaive(BufferedReader reader) {
		HashSet<String> ips = new HashSet<>();
		reader.lines().forEach(ips::add);
		return ips.size();
	}

	private static void generateIPsToFile() throws IOException {
		Path path = Paths.get(System.getProperty("user.dir") + FILE_M);
		for (int i = 0; i <= 1000000; i++) {
			int[] ip = new int[4];
			for (int j = 0; j < 4; j++) {
				ip[j] = (int) Math.round(Math.random() * 255);
			}
			String str = "\n" + Arrays.stream(ip).mapToObj(Integer::toString).collect(Collectors.joining("."));
			System.out.println(str);
			Files.writeString(path, str, StandardOpenOption.APPEND);
		}

	}


}
