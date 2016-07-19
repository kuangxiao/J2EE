package test.executorservice;

public class Main {
	
	public static void main(String[] args) {
		int[] numbers = new int[] { 1, 2, 3, 4, 5, 6, 7, 8,9,10,11,12 };
		ConcurrentCalculator calc = new ConcurrentCalculator();
		Long sum = calc.sum(numbers);
		System.out.println(sum);
		calc.close();
	}

}
