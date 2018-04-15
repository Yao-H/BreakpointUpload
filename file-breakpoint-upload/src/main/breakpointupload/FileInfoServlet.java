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
 * @description(文件信息查询)
 * @date 2017年9月5日 下午11:49:54
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
					// 文件变更，重新上传
					tmpFile.delete();
					sourceFile.delete();
					responseBody.append("StartPos=0");
				} else {
					// 进行断点上传
					responseBody.append("StartPos=" + startPos);
				}
			} else {
				// 上传的文件碎片被删除，重新上传
				tmpFile.delete();
				responseBody.append("StartPos=0");
			}
		} else {
			// 临时文件被删除，无法确认文件的一致性，删除文件碎片，重新上传
			if (sourceFile.exists()) {
				sourceFile.delete();
			}
			responseBody.append("StartPos=0");
		}
		printer.write(responseBody.toString());
	}
}
