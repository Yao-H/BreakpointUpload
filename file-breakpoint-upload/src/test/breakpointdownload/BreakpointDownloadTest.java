package test.breakpointdownload;
/**
 * @description(�ϵ����ز���)
 * @date 2017��8��30�� ����4:44:54
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class BreakpointDownloadTest {
	
	public static void main(String[] args) {
		FileInfo fileInfo = new FileInfo("http://localhost:8080/SpringLearning/p2b_time.log", "E:/temp", "p2b_time.log");
		FileFragmentFetch fileFragmentFetch = new FileFragmentFetch(fileInfo);
		fileFragmentFetch.download();
	}

}
