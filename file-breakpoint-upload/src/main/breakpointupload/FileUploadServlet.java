package main.breakpointupload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @description(�ϵ��ϴ�������)
 * @date 2017��9��5�� ����4:56:26
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = -3662169970908680378L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		this.doPost(req, res);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// �ж��Ƿ����ļ��ϴ�������
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (isMultipart) {
			DiskFileItemFactory diskFileFactory = new DiskFileItemFactory();
			ServletFileUpload sFileUpload = new ServletFileUpload(diskFileFactory);
			sFileUpload.setHeaderEncoding("UTF-8");
			sFileUpload.setSizeMax(1024 * 1024 * 2); // �ֽڣ����Ƶ����ϴ��ܴ�С2M
			
			try {
				List<FileItem> items = sFileUpload.parseRequest(req);
				Map<String,String> normalFields = new HashMap<String,String>();
				// ������ͨ�ı���
				for (FileItem item : items) {
					if (item.isFormField()) {
						normalFields.put(item.getFieldName(), item.getString());
					} 
				}
				// �����ϴ����ļ�
				String fileName = ""; // �ļ�ȫ���ƣ�����·��
				String md5 = ""; // ���͵��ļ�MD5
				for (FileItem item : items) {
					if (!item.isFormField()) {
						// �����ļ���
						fileName = item.getName();
						// ��ȡ��ʱ�ļ��еĶϵ���Ϣ��MD5
						File tmpFile = new File("E:/temp/upload.info");
						long startPos = 0L;
						if (tmpFile.exists()) {
							DataInputStream distream = new DataInputStream(new FileInputStream(tmpFile));
							md5 = distream.readUTF();
							startPos = distream.readLong();
							distream.close();
						}
						// ��Ŀ���ļ���д���ļ���Ƭ
						File file = new File("E:/temp/spdb");
						if (!file.exists()) {
							file.createNewFile();
						}
						RandomAccessFile randomAccFile = new RandomAccessFile(file, "rw");
						randomAccFile.seek(startPos);
						InputStream is = item.getInputStream(); // ��ȡ�ϴ����ļ���
						byte[] buff = new byte[1024 * 1024];
						int readnum = 0;
						while ( (readnum = is.read(buff)) > 0 ) {
							randomAccFile.write(buff, 0, readnum);
						}
						randomAccFile.close();
					}
				}
				if ( Long.parseLong(normalFields.get("StartPos")) >= Long.parseLong(normalFields.get("FileSize")) ) {
					// �ļ��ϴ���ϣ�ɾ����ʱ�ļ���������Դ�ļ�
					File tmpFile = new File("E:/temp/upload.info");
					tmpFile.delete();
					// ʹ�õ�ǰʱ�䴴���ļ���
					String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					File desfile = new File("E:/temp/" + date);
					if (!desfile.exists()) {
						desfile.mkdirs();
					}
					// ��ȡ�ļ�����
					String fileRealName = "";
					if (fileName.lastIndexOf("\\") >= 0) {
						fileRealName = fileName.substring(fileName.lastIndexOf("\\") + 1);
						if (fileRealName.lastIndexOf("/") >= 0) {
							fileRealName = fileRealName.substring(fileRealName.lastIndexOf("/") + 1);
						}
					} else {
						fileRealName = fileName.substring(fileName.lastIndexOf("/") + 1);
					}
					// ���������ƶ��ļ�
					new File("E:/temp/spdb").renameTo(new File(desfile, fileRealName));
					
					// ������֤�ļ���MD5
					String newMD5 = this.md5(new File(desfile, fileRealName));
					System.err.println("oldMD5= " + md5);
					System.err.println("newMD5= " + newMD5);
					if (!md5.equals(newMD5)) {
						// MD5��ͬ���쳣�ļ�����ʾ����ʧ�����´���
						OutputStream os = res.getOutputStream();
						os.write("RetMessage=�ļ�����ʧ��".getBytes("UTF-8"));
						os.flush();
					}
				} else {
					// ������ʱ�ļ��е���Ϣ
					File tmpFile = new File("E:/temp/upload.info");
					OutputStreamWriter osWriter = new OutputStreamWriter(new FileOutputStream(tmpFile));
					osWriter.write(""); // �����ʱ�ļ�����
					osWriter.close();
					DataOutputStream doStream = new DataOutputStream(new FileOutputStream(tmpFile));
					doStream.writeUTF(normalFields.get("MD5"));
					doStream.writeLong(Long.parseLong(normalFields.get("StartPos")));
					doStream.close();
				}
			} catch(FileUploadException e) {
				e.printStackTrace();
			}
		} else {
			RuntimeException e = new RuntimeException("���ļ��ϴ�����");
			throw e;
		}
	}
	
	/**
	 * ��ȡ�ļ�MD5
	 * @param file Ҫ��ȡMD5��Դ�ļ�
	 * @return �ļ���MD5
	 * */
	private String md5(File file) {
		String result = "";
		try {
			InputStream fis = new FileInputStream(file);
			byte[] buf = new byte[1024 * 1024];
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			int num;
			do {
				num = fis.read(buf);
				if (num > 0) {
					md5.update(buf, 0, num);
				}
			} while (num != -1);
			fis.close();
			
			byte[] b = md5.digest();
		    for (int i = 0; i < b.length; i++) {
		    	// ÿ���ֽھ�ת������λ��16������
		    	result += Integer.toString( (b[i] & 0xff) + 0x100, 16).substring(1);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
