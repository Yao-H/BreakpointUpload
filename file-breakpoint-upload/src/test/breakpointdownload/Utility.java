package test.breakpointdownload;
/**
 * @description(������)
 * @date 2017��8��30�� ����11:04:07
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
	 * ��ӡ��־��Ϣ
	 * */
	public static void log(String msg) {
		System.err.println(msg);
	}
	
}
