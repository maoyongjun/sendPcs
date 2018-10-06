package org.foxconn.testReflect;



import org.junit.Test;

public class StaticBlockTest {
	
	@Test
	public void testReflect(){
		XYZ zyz = new XYZ();
	}

	
	
}

class XYZ{
	public static String name ="luoxn28";
	static{
		System.out.println("xyz静态块");
	}
	public XYZ(){
		System.out.println("xyz构造了");
	}
}