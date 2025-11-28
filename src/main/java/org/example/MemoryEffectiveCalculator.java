package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;


public class MemoryEffectiveCalculator extends AbstractCalculator{
	final HashSet<Integer> ips = new HashSet<>();
	
	@Override
	protected long calculate(BufferedReader reader) {
		int step = 500;
		char[] buff = new char[step + 16];
		char[] extBuff = new char[1];
		int read = step;
		try {
			while (read >= step) {
				read = reader.read(buff, 0, step);
				while (buff[read - 1] != '\n' && read >= step) {
					read += reader.read(extBuff, 0, 1);
					buff[read - 1] = extBuff[0];
				}
				char[] batch = Arrays.copyOfRange(buff, 0, read);
				process(batch);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return ips.size();
	}

	private void process(char[] buff) {
		int part = 0, j = 3;
		int ip = 0;
		for (int i = 0; i < buff.length; i++) {
			char c = buff[i];
			if (c >= '0') {
				part = (part * 10) + c - 48;
			} else if (c == '.') {
				ip = ip | (part << 8 * j--);
				part = 0;
			} else if (c == '\n') {
				ip = ip | (part << 8 * j);
				ips.add(ip);
				ip = 0;
				part = 0;
				j = 3;
			}
			if (i == buff.length -1 && c != '\n') {
				ip = ip | (part << 8 * j);
				ips.add(ip);
			}
		}
	}

	@Override
	protected String getCalcName() {
		return "Memory Effective Calculator";
	}
}
