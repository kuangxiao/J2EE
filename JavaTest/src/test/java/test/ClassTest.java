package test;

public class ClassTest {

	public static void main(String[] args) {
		int[] aa = new int[10];
		System.out.println("==>" + int[].class.getCanonicalName());
		System.out.println("==>" + int[].class.getSimpleName());
		System.out.println("==>" + String[].class.getCanonicalName());
		System.out.println("==>" + String[].class.getSimpleName());

		System.out.println("==>" + new ClassTest().getClass().getSimpleName());

		Class<?> strClazz = String.class;
		Class<?> vClazz = Void.class;
	}

}
