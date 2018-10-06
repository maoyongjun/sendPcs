package org.foxconn.testReflect.proxy;

import java.lang.reflect.Proxy;

public class TestProxy {
	public static void main(String[] args) {
		RealObject real = new RealObject();
		Interface proxy = (Interface) Proxy.newProxyInstance(Interface.class.getClassLoader(),
				new Class[]{Interface.class}, new DynamicProxyHandler(real));
		proxy.doSomething();
		proxy.somethingElse("luoxin28");
	}
}
