package main.io.spring.beans;
/**
 * @description(Hello World��)
 * @data 2017��7��23�� ����4:18:21
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class Hello {
	
	private String message;
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}

	public void saying(){
		System.out.println(message);
	}
}
