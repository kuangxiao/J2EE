package test;

import java.util.ArrayList;
import java.util.Collection;

public class GenericTest {

	public static void main(String[] args) {

		Collection<Protocol<?>> list = new ArrayList<Protocol<?>>();
		list.add(new Protocol<String>(String.class));
		list.add(new Protocol<Integer>(Integer.class));
		list.add(new Protocol<Boolean>(Boolean.class));

		for (Protocol<?> i : list) {
			i.print();
			System.out.println(i.getParamType());
		}

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
