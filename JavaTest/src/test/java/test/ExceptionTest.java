package test;

public class ExceptionTest {

	public static void main(String[] args) {
		System.out.println(testReturn());
	}

	public static int testReturn() {
		try {
			int a = 1 / 0;
			return 1;
		} catch (Exception e) {
			throw e;
		} finally {
			return 3;
		}
	}

}
