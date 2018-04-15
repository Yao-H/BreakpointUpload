package main.io.spring.beans;
/**
 * @description(Hello World类)
 * @data 2017年7月23日 下午4:18:21
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
