package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class ConcurrentCalculator extends AbstractCalculator{
	final boolean[][][][] ips = new boolean[256][256][256][256];
	AtomicLong sum = new AtomicLong(0L);
	int batchCounter = 0;
	
	@Override
	protected long calculate(BufferedReader reader) {
		int step = 1000000;
		char[] buff = new char[step + 16];
		char[] extBuff = new char[1];
		int read = step;
		ExecutorService executorService = Executors.newFixedThreadPool(512);
		try {
			while (read >= step) {
				read = reader.read(buff, 0, step);
				while (buff[read - 1] != '\n' && read >= step) {
					read += reader.read(extBuff, 0, 1);
					buff[read - 1] = extBuff[0];
				}
				char[] batch = Arrays.copyOfRange(buff, 0, read);
				executorService.execute(() -> calcBatch(prepareIntList(batch)));
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
		for (int i = 0; i < buff.length; i++) {
			char c = buff[i];
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
			if (i == buff.length -1 && c != '\n') {
				ip[j] = part;
				list.add(ip);
			}
		}
		return list;
	}

	private void calcBatch(List<int[]> ips) {
		ips.forEach(this::calculate);
	}

	private void calculate(int[] ip) {
		if (!ips[ip[0]][ip[1]][ip[2]][ip[3]]) {
			ips[ip[0]][ip[1]][ip[2]][ip[3]] = true;
			sum.getAndIncrement();
		}
	}

	@Override
	protected String getCalcName() {
		return "Concurrent Calculator";
	}
}
