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
 * @description(断点上传服务类)
 * @date 2017年9月5日 下午4:56:26
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
		// 判断是否是文件上传的请求
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (isMultipart) {
			DiskFileItemFactory diskFileFactory = new DiskFileItemFactory();
			ServletFileUpload sFileUpload = new ServletFileUpload(diskFileFactory);
			sFileUpload.setHeaderEncoding("UTF-8");
			sFileUpload.setSizeMax(1024 * 1024 * 2); // 字节，限制单次上传总大小2M
			
			try {
				List<FileItem> items = sFileUpload.parseRequest(req);
				Map<String,String> normalFields = new HashMap<String,String>();
				// 处理普通文本域
				for (FileItem item : items) {
					if (item.isFormField()) {
						normalFields.put(item.getFieldName(), item.getString());
					} 
				}
				// 处理上传的文件
				String fileName = ""; // 文件全名称，包含路径
				String md5 = ""; // 上送的文件MD5
				for (FileItem item : items) {
					if (!item.isFormField()) {
						// 解析文件名
						fileName = item.getName();
						// 读取临时文件中的断点信息与MD5
						File tmpFile = new File("E:/temp/upload.info");
						long startPos = 0L;
						if (tmpFile.exists()) {
							DataInputStream distream = new DataInputStream(new FileInputStream(tmpFile));
							md5 = distream.readUTF();
							startPos = distream.readLong();
							distream.close();
						}
						// 向目标文件中写入文件碎片
						File file = new File("E:/temp/spdb");
						if (!file.exists()) {
							file.createNewFile();
						}
						RandomAccessFile randomAccFile = new RandomAccessFile(file, "rw");
						randomAccFile.seek(startPos);
						InputStream is = item.getInputStream(); // 获取上传的文件流
						byte[] buff = new byte[1024 * 1024];
						int readnum = 0;
						while ( (readnum = is.read(buff)) > 0 ) {
							randomAccFile.write(buff, 0, readnum);
						}
						randomAccFile.close();
					}
				}
				if ( Long.parseLong(normalFields.get("StartPos")) >= Long.parseLong(normalFields.get("FileSize")) ) {
					// 文件上传完毕，删除临时文件，重命名源文件
					File tmpFile = new File("E:/temp/upload.info");
					tmpFile.delete();
					// 使用当前时间创建文件夹
					String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					File desfile = new File("E:/temp/" + date);
					if (!desfile.exists()) {
						desfile.mkdirs();
					}
					// 截取文件名称
					String fileRealName = "";
					if (fileName.lastIndexOf("\\") >= 0) {
						fileRealName = fileName.substring(fileName.lastIndexOf("\\") + 1);
						if (fileRealName.lastIndexOf("/") >= 0) {
							fileRealName = fileRealName.substring(fileRealName.lastIndexOf("/") + 1);
						}
					} else {
						fileRealName = fileName.substring(fileName.lastIndexOf("/") + 1);
					}
					// 重命名并移动文件
					new File("E:/temp/spdb").renameTo(new File(desfile, fileRealName));
					
					// 重新验证文件的MD5
					String newMD5 = this.md5(new File(desfile, fileRealName));
					System.err.println("oldMD5= " + md5);
					System.err.println("newMD5= " + newMD5);
					if (!md5.equals(newMD5)) {
						// MD5不同，异常文件，提示传输失败重新传输
						OutputStream os = res.getOutputStream();
						os.write("RetMessage=文件传输失败".getBytes("UTF-8"));
						os.flush();
					}
				} else {
					// 更新临时文件中的信息
					File tmpFile = new File("E:/temp/upload.info");
					OutputStreamWriter osWriter = new OutputStreamWriter(new FileOutputStream(tmpFile));
					osWriter.write(""); // 清空临时文件内容
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
			RuntimeException e = new RuntimeException("非文件上传请求");
			throw e;
		}
	}
	
	/**
	 * 获取文件MD5
	 * @param file 要获取MD5的源文件
	 * @return 文件的MD5
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
		    	// 每个字节均转换成两位的16进制数
		    	result += Integer.toString( (b[i] & 0xff) + 0x100, 16).substring(1);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
