package test.breakpointdownload;
/**
 * @description(工具类)
 * @date 2017年8月30日 上午11:04:07
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class Utility {
	
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打印日志信息
	 * */
	public static void log(String msg) {
		System.err.println(msg);
	}
	
}
