package test;
/**
 * @description(������)
 * @date 2017��9��7�� ����4:11:30
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class SubTest {

	public static void main(String[] args) {
		String str = "StartPos=1";
		String[] arr = str.split("&");
		for (String s : arr) {
			System.out.println(s);
		}
	}
}
