package main.io.spring.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import main.io.spring.beans.Hello;

/**
 * @description(Spring核心容器测试)
 * @data 2017年7月23日 下午3:50:07
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class ContainerTest {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("Spring.xml");
		Hello hello = (Hello)context.getBean("hello");
		hello.saying();
	}
}