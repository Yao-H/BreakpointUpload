package main.breakpoint;

/**
 * @description(测试类) @data 2017年8月29日 上午12:13:03
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class TestMethod {
	public TestMethod() { /// xx/weblogic60b2_win.exe
		try {
			SiteInfoBean bean = new SiteInfoBean("http://localhost:8080/SpringLearning/p2b_time.log", "E:\\temp",
					"p2b_time.log", 5);
			SiteFileFetch fileFetch = new SiteFileFetch(bean);
			fileFetch.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestMethod();
	}
}
