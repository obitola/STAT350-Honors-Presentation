import java.util.concurrent.atomic.AtomicInteger;
import java.time.Duration;
import java.lang.Math;

public class DataGenerator {
	Handler handler;
	Thread[] vars;
	int count;
	public DataGenerator() {
		handler = new Handler();
		count = 0;
		vars = new Thread[8];
		vars[count++] = new Thread((Runnable) new Mod(true, 0, handler));
		vars[count++] = new Thread((Runnable) new Mod(false, 0, handler));
		vars[count++] = new Thread((Runnable) new Mod(true, 1, handler));
		vars[count++] = new Thread((Runnable) new Mod(false, 1, handler));
		vars[count++] = new Thread((Runnable) new Mod(true, 2, handler));
		vars[count++] = new Thread((Runnable) new Mod(false, 2, handler));
		vars[count++] = new Thread((Runnable) new Mod(true, 3, handler));
		vars[count++] = new Thread((Runnable) new Mod(false, 3, handler));
	}

	public void start() {
		final int ITER = 2000 * 200;

		System.out.print("Type\tTime\n");
		long start = System.nanoTime();
		long time;
		long[][] sum = new long[4][200];
		int k = 0;
		int up;
		int down;
		String type = "ctrl";

		for (int i = 0; i < ITER; i++) {
			for (int j = 0; j < 4; j++) {
				start = System.nanoTime();
				up = j * 2;
				down = j * 2 + 1;
				vars[up].start();
				vars[down].start();
				try {
					vars[up].join();
					vars[down].join();
				} catch (Exception e) {
					e.printStackTrace();
				}
				time = System.nanoTime() - start;
				
				if (k == sum[0].length - 1) {
					sum[j][k] = time;
					if (i >= 20 * 100 && Math.log(avg(sum[j])) < 7.35) {
						System.out.printf("%s\t%f\n", type, Math.log(avg(sum[j])));
					}
				}
			} k++; if (k == sum[0].length) { k = 0; } count = 0; vars[count++] = new Thread((Runnable) new Mod(true, 0, handler));
			vars[count++] = new Thread((Runnable) new Mod(false, 0, handler));
			vars[count++] = new Thread((Runnable) new Mod(true, 1, handler));
			vars[count++] = new Thread((Runnable) new Mod(false, 1, handler));
			vars[count++] = new Thread((Runnable) new Mod(true, 2, handler));
			vars[count++] = new Thread((Runnable) new Mod(false, 2, handler));
			vars[count++] = new Thread((Runnable) new Mod(true, 3, handler));
			vars[count++] = new Thread((Runnable) new Mod(false, 3, handler));
		}
	}

	private static long avg(long[] l) {
		long s = 0;
		for (int i = 0; i < l.length; i++) {
			s += l[i] / l.length;
		}

		return s;
	}

	private class Mod implements Runnable {
		final int AMOUNT = 100000;
		boolean up;
		int type;
		Handler handler;
		
		public Mod(boolean up, int type, Handler handler) {
			this.up = up;
			this.type = 0;
			this.handler = handler;
		}
	
		public void run() {
		
			if (type == 0) {
				if (up) {
					for (int i = 0; i < AMOUNT; i++) {
						handler.ctrlUp();
					}
				} else {
					for (int i = 0; i < AMOUNT; i++) {
						handler.ctrlDown();
					}
				}
			}

			if (type == 1) {
				if (up) {
					for (int i = 0; i < AMOUNT; i++) {
						handler.syncUp();
					}
				} else {
					for (int i = 0; i < AMOUNT; i++) {
						handler.syncDown();
					}
				}
			}

			if (type == 2) {
				if (up) {
					for (int i = 0; i < AMOUNT; i++) {
						handler.atomUp();
					}
				} else {
					for (int i = 0; i < AMOUNT; i++) {
						handler.atomDown();
					}
				}
			}

			if (type == 3) {
				if (up) {
					for (int i = 0; i < AMOUNT; i++) {
						handler.lockUp();
					}
				} else {
					for (int i = 0; i < AMOUNT; i++) {
						handler.lockDown();
					}
				}
			}
		}

	}

	private class Handler {
		private Object obj = new Object();	
		private int ctrl;
		private int sync;
		private AtomicInteger atom;
		private int lock;

		public Handler() {
			this.reset();
		}

		public void reset() {
			this.ctrl = 0;
			this.sync = 0;
			this.atom = new AtomicInteger(0);
			this.lock = 0;
		}

		public void ctrlUp() {
			this.ctrl++;
		}
		public void ctrlDown() {
			this.ctrl--;
		}
		public synchronized void syncUp() {
			this.sync++;
		}

		public synchronized void syncDown() {
			this.sync--;
		}

		public void atomUp() {
			atom.incrementAndGet();
		}
		
		public void atomDown() {
			atom.decrementAndGet();
		}

		public void lockUp() {
			synchronized(obj) {
				lock++;
			}
		}

		public void lockDown() {
			synchronized(obj) {
				lock--;
			}
		}

		public String toString() {
			String output = "Control Sync Atom Lock: " + ctrl + sync + atom.toString() + lock;
			return output;
		}
	}

	public static void main(String[] args) {
		DataGenerator dg = new DataGenerator();
		dg.start();		
	}
}
