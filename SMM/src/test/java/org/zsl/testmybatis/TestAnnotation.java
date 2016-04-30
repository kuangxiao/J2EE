package org.zsl.testmybatis;

import java.lang.annotation.Annotation;

import org.springframework.stereotype.Component;

import com.cn.hnust.controller.UserController;

public class TestAnnotation {

	public static void main(String[] args) throws Exception {
		Annotation[] annos = UserController.class.getAnnotations();
		for (Annotation anno : annos) {
			System.out.println(anno.annotationType().isAnnotationPresent(Component.class));
		}
		
		System.out.println(String.class.getCanonicalName());		
		System.out.println(Component.class.getCanonicalName());
		System.out.println(int[].class.getCanonicalName());
		System.out.println(byte[].class);
		
		int[] aa = {10,11};
		
		Class<int[]> inta= int[].class;
		Class<String> stringa= String.class;
	}
}
