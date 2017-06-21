package test;

public class RecursiveTest {

	public static void main(String[] args) {
		recursive(1);
	}

	public static void recursive(int n) {
		System.out.println("First: " + n);
		if (n < 5) {
			recursive(n + 1);
		}
		System.out.println("Last: " + n);
	}

}
