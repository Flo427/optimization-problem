package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.ThreadLocalRandom;

public class Instance {

	private int x[];
	private int y[];
	private int z[];
	private int grid[][][];
	private int boxCount;

	public final int n;
	private final int l;

	private int width[];
	private int height[];

	public Instance(Instance instance) {
		this.boxCount = instance.boxCount;
		this.n = instance.n;
		this.l = instance.l;
		this.x = new int[n];
		this.y = new int[n];
		this.z = new int[n];
		this.grid = new int[l][l][boxCount];
		this.width = new int[n];
		this.height = new int[n];
		for (int i = 0; i < this.n; ++i) {
			this.width[i] = instance.width[i];
			this.height[i] = instance.height[i];
		}
	}

	public Instance(int n, int l, int min, int max) {
		// ensure max is not too big
		if (max > l) {
			max = l;
			System.out.println("max was set to maximal possible value " + l);
		}

		// ensure min is not too small
		if (min < 1) {
			min = 1;
			System.out.println("min was set to minimal possible value 1");
		}

		// ensure min is not bigger than max
		if (max < min) {
			int swap = max;
			max = min;
			min = swap;
			System.out.println("max and min were switched");
		}

		int w, h;
		this.n = n;
		this.l = l;
		boxCount = n;
		x = new int[n];
		y = new int[n];
		z = new int[n];
		grid = new int[l][l][boxCount];
		width = new int[n];
		height = new int[n];
		for (int i = 0; i < n; ++i) {
			w = ThreadLocalRandom.current().nextInt(min, max + 1);
			h = ThreadLocalRandom.current().nextInt(min, max + 1);
			width[i] = w;
			height[i] = h;
		}
	}

	public Instance(int n, int l) {
		// ensure n is not too big
		if (n > l * l) {
			n = l * l;
			System.out.println("n was set to maximal possible value " + l * l);
		}

		int j, cut;
		this.n = n;
		this.l = l;
		boxCount = n;
		x = new int[n];
		y = new int[n];
		z = new int[n];
		grid = new int[l][l][boxCount];
		width = new int[n];
		height = new int[n];
		width[0] = l;
		height[0] = l;

		for (int i = 1; i < n; ++i) {
			do {
				j = ThreadLocalRandom.current().nextInt(0, i);
			} while (width[j] * height[j] == 1);
			cut = ThreadLocalRandom.current().nextInt(1, width[j] + height[j] - 1);
			if (cut < width[j]) {
				width[i] = width[j] - cut;
				width[j] = cut;
				height[i] = height[j];
			} else {
				cut = cut - width[j] + 1;
				height[i] = height[j] - cut;
				height[j] = cut;
				width[i] = width[j];
			}
//			System.out.println();
//			for (int k = 0; k < i + 1; ++k) {
//				System.out.println(width[k] + ", " + height[k]);
//			}
		}
	}

	public Instance plot(Graphics g, int frameWidth, int frameHeight) {
		int zoom = Math.min((frameWidth - 10) / (boxCount * (l + 10)), 5);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(240, 240, 240));
		g2.fillRect(0, 0, frameWidth, frameHeight);

		g2.setColor(Color.WHITE);
		for (int i = 0; i < boxCount; ++i) {
			g2.fillRect(zoom * x(i, frameWidth), zoom * y(i, frameWidth), zoom * l, zoom * l);
		}

		for (int i = 0; i < n; ++i) {
			g2.setColor(new Color(ThreadLocalRandom.current().nextInt(0, 256),
					ThreadLocalRandom.current().nextInt(0, 256), ThreadLocalRandom.current().nextInt(0, 256)));
			g2.fillRect(zoom * (x[i] + x(z[i], frameWidth)), zoom * (y[i] + y(z[i], frameWidth)), zoom * width[i],
					zoom * height[i]);
		}

		return this;
	}

	private int x(int i, int frameWidth) {
		int m = (frameWidth - 10) / (l + 10);
		return 10 + ((i % m) * (l + 10));
	}

	private int y(int i, int frameWidth) {
		int m = (frameWidth - 10) / (l + 10);
		return 10 + ((i / m) * (l + 10));
	}

	public void generateCoordinatesByPermutation() {
		int i = 0;
		for (int z_value = 0; z_value < boxCount; ++z_value) {
			for (int y_value = 0; y_value < l; ++y_value) {
				for (int x_value = 0; x_value < l; ++x_value) {
					// try first rotate direction
					boolean successful = placeRectangle(i, x_value, y_value, z_value);

					// if not successful, try second rotate direction
					if (!successful) {
						rotateRectangle(i);
						successful = placeRectangle(i, x_value, y_value, z_value);
					}

					// if any placement successful, go to next rectangle if existing
					if (successful) {
						// System.out.println("Rectangle " + i + ": (" + x[i] + "," + y[i] + "," + z[i]
						// + ")");
						++i;
						if (i == n) {
							boxCount = z_value + 1;
							return;
						}
						if (width[i] < height[i]) {
							rotateRectangle(i);
						}
					}
				}
			}
		}
	}

	private boolean placeRectangle(int i, int x_value, int y_value, int z_value) {
		// check if space for placing rectangle is free
		if (x_value + width[i] > l || y_value + height[i] > l) {
			return false;
		}
		for (int j = x_value; j < x_value + width[i]; ++j) {
			for (int k = y_value; k < y_value + height[i]; ++k) {
				if (grid[j][k][z_value] > 0) {
					return false;
				}
			}
		}

		// if so, place rectangle
		x[i] = x_value;
		y[i] = y_value;
		z[i] = z_value;
		for (int j = x_value; j < x_value + width[i]; ++j) {
			for (int k = y_value; k < y_value + height[i]; ++k) {
				grid[j][k][z_value] = 1;
			}
		}
		return true;
	}

	private void rotateRectangle(int i) {
		int swap = width[i];
		width[i] = height[i];
		height[i] = swap;
	}

	public void swapRectangles(int i, int j) {
		int swap = width[i];
		width[i] = width[j];
		width[j] = swap;
		swap = height[i];
		height[i] = height[j];
		height[j] = swap;
	}

	public void newRectanglePosition(int i, int pos) {
		int swap_width = width[i];
		int swap_height = height[i];
		if (i < pos) {
			for (int j = i; j < pos; ++j) {
				width[j] = width[j + 1];
				height[j] = height[j + 1];
			}
		} else {
			for (int j = i; j > pos; --j) {
				width[j] = width[j - 1];
				height[j] = height[j - 1];
			}			
		}
		width[pos] = swap_width;
		height[pos] = swap_height;
	}

	public int getObjectiveValue() {
		int sum = l * l * boxCount;
		for (int k = 0; k < boxCount; ++k) {
			for (int j = 0; j < n; ++j) {
				for (int i = 0; i < n; ++i) {
					// prefers rectangles in first box(es)
					sum += grid[i][j][k] * k;
				}
			}
		}
		return sum;
	}

}
