package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;


public class FastestCalculator extends AbstractCalculator{
	final IntSet ips = new IntSet();
	
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
		return ips.size;
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
	
	static class IntSet {
		int size = 0;
		float loadFactor = 0.75f;
		int capacity = 16;
		int threshold = (int)(capacity * loadFactor);
		
		int[] data = new int[capacity];
		
		public void add(int value) {
			if (!contains(value)) {
				int index = getPosition(value);
				while (data[index] != 0) {
					if (++index >= capacity) {
						index = 0;
					}
				}
				data[index] = value;
				if (size++ >= threshold) {
					resize();
				}
			}
		}
		
		private int getPosition(int value) {
			return value & (capacity - 1);
		}
		
		private boolean contains(int value) {
			int index = getPosition(value);
			do {
				if (data[index] == value) {
					return true;
				}
				index++;
				if (index >= capacity) {
					index = 0;
				}
			} while (data[index] != 0);
			return false;
		}
		
		
		private void resize() {
			int newCapacity = capacity << 1;
			int[] oldData = data;
			data = new int[newCapacity];
			capacity = newCapacity;
			threshold = threshold << 1;
			size = 0;
			for (int value : oldData) {
				add(value);
			}
		}
	}

	@Override
	protected String getCalcName() {
		return "Fast Calculator";
	}
}
