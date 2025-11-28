package org.example;

import org.apache.commons.lang3.time.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public abstract class AbstractCalculator {
	
	private final StopWatch stopWatch = new StopWatch();
	
	public void start(Path path) {
		stopWatch.start();
		BufferedReader readerF;
		try {
			readerF = Files.newBufferedReader(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		long count = calculate(readerF);
		memoryUsage();
		stopWatch.stop();
		System.out.println(getCalcName() + "(" + (stopWatch.getTime()) + "): " + count);
	}
	
	private void memoryUsage(){
		Runtime runtime = Runtime.getRuntime();

		long totalMemory = runtime.totalMemory(); // Total memory available to the JVM
		long freeMemory = runtime.freeMemory();   // Free memory within the JVM
		long usedMemory = totalMemory - freeMemory; // Currently used memory

		System.out.println("Used JVM Memory: " + usedMemory / (1024 * 1024) + " MB");
	}
	
	protected abstract long calculate(BufferedReader bufferedReader);
	protected abstract String getCalcName();
}
