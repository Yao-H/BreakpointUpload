package test;
/**
 * @description(测试类)
 * @date 2017年9月7日 下午4:11:30
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
