package main.breakpointupload;
/**
 * @description(�ϵ��ϴ�������)
 * @date 2017��9��6�� ����12:08:55
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class BreakPointUploadTest {

	public static void main(String[] args) {
		// ��ʼ���ϴ��ļ���Ϣ
		FileInfo fileInfo = new FileInfo("http://localhost:8080/SpringLearning/FileInfo.do", "E:\\", "video3.avi");
		
		// ��ʼ���ϴ��ͻ���
		FileUploadClient client = new FileUploadClient(fileInfo, "http://localhost:8080/SpringLearning/FileUpload.do");
		client.upload();
	}
}
