package main.breakpoint;

/**
 * @description(������) @data 2017��8��29�� ����12:12:14
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class Utility {
	public Utility() {
	}

	public static void sleep(int nSecond) {
		try {
			Thread.sleep(nSecond);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void log(String sMsg) {
		System.err.println(sMsg);
	}

	public static void log(int sMsg) {
		System.err.println(sMsg);
	}
}
