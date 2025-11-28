package org.example;

import java.io.BufferedReader;
import java.util.HashSet;


public class NaiveCalculator extends AbstractCalculator{
	@Override
	protected long calculate(BufferedReader reader) {
		HashSet<String> ips = new HashSet<>();
		reader.lines().forEach(ips::add);
		return ips.size();
	}

	@Override
	protected String getCalcName() {
		return "Naive Calculator";
	}
}
