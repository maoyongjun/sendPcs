package org.foxconn.testReflect;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class PropertyReflectTest{
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		Person person = new Person("luoxin3",23);
		Class clazz = person .getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field :fields){
			String key = field.getName();
			PropertyDescriptor propertyDescriptor = new PropertyDescriptor(key, clazz);
			Method method = propertyDescriptor.getReadMethod();
			Object value =  method.invoke(person);
			System.out.println(key+":"+value);
		}
	}
}

 class Person implements Serializable{
	private String name;
	private int age;
	public Person(String name,int age){
		this.name = name;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
}
