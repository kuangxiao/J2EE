package test;

import java.util.Collections;
import java.util.Map;

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
		
		Map<String,String> map = Collections.<String,String> emptyMap();
		System.out.println("==>" + map.get(null));
		
		Long ten = new Long(10);
		Long ten2 = new Long(10);
		System.out.println( ten == ten2 );
	}

}
