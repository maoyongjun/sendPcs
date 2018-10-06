package org.foxconn.testReflect;

class Base {
	static int num =1;
	static{
		System.out.println("base"+num);
	}
	
}

public class ClassFlectTest{
	public static void main(String[] args) throws ClassNotFoundException{
		Class clazz1 = Base.class;
		System.out.println("------");
		Class clazz2 = Class.forName("org.foxconn.ftpupload.Base");
	}
	
}