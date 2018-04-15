package main.breakpointupload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * @description(文件信息)
 * @date 2017年9月4日 下午11:00:22
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class FileInfo {
	private String url; // server url
	private String filePath; // local File's Path
	private String fileName; // local File's Name
	private long size; // file's size in bytes
	private String md5; // file's message digest
	private String oldmd5; // server file's md5
	private long startPos = 0; // the offset to start read
	
	public FileInfo() {}
	
	public FileInfo(String url, String filePath, String fileName) {
		this.url = url;
		this.filePath = filePath;
		this.fileName = fileName;
		this.setSize();
		this.setFileMD5(filePath, fileName);
		this.getServerFileInfo();
	}
	
	/**
	 * 获取文件的信息摘要MD5
	 * @param filePath 文件的目录路径
	 * @param fileName 文件名称
	 * */
	public void setFileMD5(String filePath, String fileName) {
		try {
			InputStream fis = new FileInputStream(filePath + fileName);
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
			String result = ""; 
		    for (int i = 0; i < b.length; i++) {
		    	// 每个字节均转换成两位的16进制数
		    	result += Integer.toString( (b[i] & 0xff) + 0x100, 16).substring(1);
		    }
		    this.md5 = result;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取服务端的文件信息
	 * */
	private Map<String,String> getServerFileInfo() {
		System.err.println("****************开始获取服务端临时文件信息********************");
		Map<String,String> retMap = new HashMap<String,String>();
		try {
			URL url = new URL(this.url);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoOutput(true); // 是否有输入
			httpConn.setDoInput(true); // 是否有输出
			httpConn.setUseCaches(false); // 是否使用缓存
			httpConn.setRequestMethod("POST"); // 请求方法
			httpConn.setRequestProperty("Accept", "*/*"); // 接收的内容类型
			httpConn.setRequestProperty("Connection", "Keep-Alive"); // 长连接
			httpConn.setRequestProperty("Charset", "UTF-8"); // 指定请求内容的字符集
			httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); // 指定用户代理
			
			// 添加请求参数
			OutputStream os = httpConn.getOutputStream();
			StringBuilder requestContent = new StringBuilder();
			requestContent.append("MD5=" + md5);
			os.write(requestContent.toString().getBytes("UTF-8"));
			os.flush();
			os.close();
			
			// 读取响应参数
			StringBuilder sb = new StringBuilder();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "utf-8"));
			String line;
			while (null != (line = buffReader.readLine())) {
				sb.append(line);
			}
			buffReader.close();
			// 解析返回参数
			// 调试-文件的MD5信息
		    System.err.println("MD5= " + this.md5);
			String[] retData = new String[0];
			if (sb.toString().trim().length() > 0) {
				retData = sb.toString().trim().split("&");
			}
			for (int i = 0; i < retData.length; i++) {
				String[] keyValuePair = retData[i].split("=");
				retMap.put(keyValuePair[0], keyValuePair[1]);
				// 调试-接收到的服务端的文件上传信息
				System.err.println(keyValuePair[0] + "=" + keyValuePair[1]);
			}
			// 更新文件传输的位置信息
			this.setStartPos(Long.parseLong(retMap.get("StartPos")));
			System.err.println("****************获取服务端临时文件信息结束********************");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("获取服务端文件信息失败！");
		}
		return retMap;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilePath() {
		return filePath;
	}
	
	public String getFileRealPath() {
		return filePath + fileName;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public long getSize() {
		return this.size;
	}
	
	public void setSize() {
		this.size = new File(this.getFileRealPath()).length();
	}
	
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getOldmd5() {
		return oldmd5;
	}

	public void setOldmd5(String oldmd5) {
		this.oldmd5 = oldmd5;
	}

	public long getStartPos() {
		return startPos;
	}

	public void setStartPos(long startPos) {
		this.startPos = startPos;
	}
	
	
}
