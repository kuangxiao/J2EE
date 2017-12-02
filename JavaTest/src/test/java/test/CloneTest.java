package test;

import java.util.Date;

/**
 * @author win
 *
 */
/**
 * @author win
 *
 */
class CloneTest implements Cloneable {
	
	private int age;
	private String name;
	private Date birth;

	public CloneTest(int a, String n, Date b) {
		age = a;
		name = n;
		birth = b;
	}

	public static void main(String[] args) throws Exception {
		CloneTest kobe = new CloneTest(33, "kobe", new Date());
		CloneTest kobeClone = (CloneTest) kobe.clone();
		System.out.println(kobe.birth);
		System.out.println(kobeClone.birth);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		CloneTest p = (CloneTest) super.clone();
		// Date类型的birth域是可变的，需要对其克隆，进行深拷贝
		// Date类实现的克隆，直接调用即可
		p.birth = (Date) birth.clone();
		
		return p;
	}

}
