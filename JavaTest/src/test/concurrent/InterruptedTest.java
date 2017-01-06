package test.concurrent;

public class InterruptedTest implements Runnable {
	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Thread sleep()");
				Thread.sleep(30000);
				System.out.println("A1>>>, Thread.currentThread().isInterrupted() = " + Thread.currentThread().isInterrupted());
			} catch (InterruptedException e) {
				System.out.println("A2>>>, Thread.currentThread().isInterrupted() = " + Thread.currentThread().isInterrupted());
				Thread.currentThread().interrupt();
				System.out.println("A3>>>, Thread.currentThread().isInterrupted() = " + Thread.currentThread().isInterrupted());
			}
		}
	}

	public static void main(String[] args) {
		InterruptedTest run = new InterruptedTest();
		Thread thread = new Thread(run);

		// 启动线程进入sleep状态。
		thread.start();

		// 立刻中断线程。
		thread.interrupt();
	}
}
