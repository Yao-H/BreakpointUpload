package main.breakpointupload;
/**
 * @description(断点上传测试类)
 * @date 2017年9月6日 上午12:08:55
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class BreakPointUploadTest {

	public static void main(String[] args) {
		// 初始化上传文件信息
		FileInfo fileInfo = new FileInfo("http://localhost:8080/SpringLearning/FileInfo.do", "E:\\", "video3.avi");
		
		// 初始化上传客户端
		FileUploadClient client = new FileUploadClient(fileInfo, "http://localhost:8080/SpringLearning/FileUpload.do");
		client.upload();
	}
}
