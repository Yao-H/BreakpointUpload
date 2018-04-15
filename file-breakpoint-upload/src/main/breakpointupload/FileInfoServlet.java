package main.breakpointupload;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description(�ļ���Ϣ��ѯ)
 * @date 2017��9��5�� ����11:49:54
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class FileInfoServlet extends HttpServlet{

	private static final long serialVersionUID = 6060600915707902829L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		this.doPost(req, res);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PrintWriter printer = res.getWriter();
		StringBuffer responseBody = new StringBuffer();
		
		File tmpFile = new File("E:/temp/upload.info");
		File sourceFile = new File("E:/temp/spdb");
		if (tmpFile.exists()) {
			DataInputStream dis = new DataInputStream(new FileInputStream(tmpFile));
			String md5 = dis.readUTF();
			long startPos = dis.readLong();
			dis.close();
			if (sourceFile.exists()) {
				if (!req.getParameter("MD5").equals(md5)) {
					// �ļ�����������ϴ�
					tmpFile.delete();
					sourceFile.delete();
					responseBody.append("StartPos=0");
				} else {
					// ���жϵ��ϴ�
					responseBody.append("StartPos=" + startPos);
				}
			} else {
				// �ϴ����ļ���Ƭ��ɾ���������ϴ�
				tmpFile.delete();
				responseBody.append("StartPos=0");
			}
		} else {
			// ��ʱ�ļ���ɾ�����޷�ȷ���ļ���һ���ԣ�ɾ���ļ���Ƭ�������ϴ�
			if (sourceFile.exists()) {
				sourceFile.delete();
			}
			responseBody.append("StartPos=0");
		}
		printer.write(responseBody.toString());
	}
}
