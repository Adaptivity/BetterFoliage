package mods.betterfoliage.common.config;

public class OptionInteger {

	public int min;
	public int max;
	public int step;
	public int value;
	
	public OptionInteger(int min, int max, int step, int value) {
		this.min = min;
		this.max = max;
		this.step = step;
		this.value = value;
	}
	
	public void increment() {
		value += step;
		if (value > max) value = max;
	}
	
	public void decrement() {
		value -= step;
		if (value < min) value = min;
	}
}
