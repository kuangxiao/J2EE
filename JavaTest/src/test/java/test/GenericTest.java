package test;

import java.util.ArrayList;
import java.util.Collection;

public class GenericTest {

	public static void main(String[] args) {

		Collection<Protocol<?>> list = new ArrayList<Protocol<?>>();
		list.add(new Protocol<String>(String.class));
		list.add(new Protocol<Integer>(Integer.class));
		list.add(new Protocol<Boolean>(Boolean.class));
		
		list.forEach(i -> { System.out.println(i.getParamType()); } );
		list.forEach(System.out::println);
		
		for (Protocol<?> i : list) {
			i.print();
			System.out.println(i.getParamType());
		}
		
		System.out.println(String.valueOf(GenericTest.<Exception>get()));

	}	
	
	@SuppressWarnings("unchecked")
	public static <T extends Exception> T get() {
		return (T) new Exception();
	}

	public static class Protocol<C> {

		Class<C> clazz;

		public Protocol(Class<C> clazz) {
			this.clazz = clazz;
		}

		public void print() {
			System.out.println("-- " + clazz.getClass());
		}

		public Class<?> getParamType() {
			return clazz;
		}

	}
}
