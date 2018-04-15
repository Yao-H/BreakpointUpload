package main.breakpointupload;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @description(断点上传客户端)
 * @date 2017年9月4日 下午10:59:24
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class FileUploadClient {
	// 每个post参数之间的分隔符，随意设定，只要不和其他的字符串重复即可
	private static final String BOUNDARY = "HV2ymHFg03ehbqgZCaKO6jyH";
	
	private FileInfo fileInfo;
	private String uploadUrl;
	
	public FileUploadClient() {}
	
	public FileUploadClient(FileInfo fileInfo, String uploadUrl) {
		this.fileInfo = fileInfo;
		this.uploadUrl = uploadUrl;
	}
	
	/**
	 * 模拟表单向服务器上传文件
	 * */
	public void upload() {
		try {
			// 模拟form表单传输文件碎片，每一行的数据必须以(\r\n)结尾
			do {
				URL url = new URL(uploadUrl);
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setDoOutput(true); // 是否有输入
				httpConn.setDoInput(true); // 是否有输出
				httpConn.setUseCaches(false); // 是否使用缓存
				httpConn.setRequestMethod("POST"); // 请求方法
				httpConn.setRequestProperty("Accept", "*/*"); // 接收内容类型
				httpConn.setRequestProperty("Connection", "Keep-Alive"); // 长连接
				httpConn.setRequestProperty("Charset", "UTF-8"); // 请求编码
				httpConn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); // 客户代理
				httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY); // 请求内容类型
				httpConn.connect();
				
				// 当前连接的输出流对象
				OutputStream os = httpConn.getOutputStream();
				
				// http请求体-文件数据
				os.write(("--" + BOUNDARY + "\r\n").getBytes("UTF-8"));
				StringBuffer contentBody = new StringBuffer();
				contentBody.append("Content-Disposition: form-data;name=\"")
				.append("uploadSnippet\";") // 相当于input标签的name属性
				.append("filename=\"")
				.append(fileInfo.getFileRealPath() + "\"\r\n") // 文件的全名称，包括路径
				.append("Content-Type: application/octet-stream\r\n")
				.append("\r\n");
				os.write(contentBody.toString().getBytes("UTF-8"));
				
				// 向服务器传输文件碎片
				RandomAccessFile raf = new RandomAccessFile(fileInfo.getFileRealPath(), "r");
				raf.seek(fileInfo.getStartPos()); // 设置文件游标(file-pointer)的位置
				byte[] buff = new byte[1024 * 1024];
				int readnum = 0;
				readnum = raf.read(buff);
				raf.close();
				os.write(buff, 0, readnum);
				os.write("\r\n".getBytes("UTF-8"));
				os.write(("--" + BOUNDARY + "\r\n").getBytes("UTF-8"));
				
				// 更新文件读取的起始位置
				fileInfo.setStartPos(fileInfo.getStartPos() + readnum);
				
				// http请求体-普通参数
				KeyValuePair[] kvPair = new KeyValuePair[3];
				kvPair[0] = new KeyValuePair("MD5", fileInfo.getMd5());
				kvPair[1] = new KeyValuePair("StartPos", fileInfo.getStartPos() + "");
				kvPair[2] = new KeyValuePair("FileSize", fileInfo.getSize() + "");
				contentBody = new StringBuffer("");
				for (int i = 0; i < kvPair.length; i++) {
					contentBody.append("Content-Disposition: form-data;name=\"")
					.append(kvPair[i].getKey() + "\"\r\n")
					.append("\r\n")
					.append(kvPair[i].getValue() + "\r\n");
					if (i == (kvPair.length - 1)) {
						// http请求体结束
						contentBody.append("--" + BOUNDARY + "--\r\n\r\n");
					} else {
						contentBody.append("--" + BOUNDARY + "\r\n");
					}
				}
				os.write(contentBody.toString().getBytes("UTF-8"));
				os.flush();
				os.close();
				
				// 调试-当前文件的信息
				System.err.println("StartPos= " + fileInfo.getStartPos() + ", FileSize= " + fileInfo.getSize());
				
				// 接收服务端响应
				StringBuilder sb = new StringBuilder();
				BufferedReader buffReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
				String line;
				while (null != (line = buffReader.readLine())) {
					sb.append(line);
				}
				buffReader.close();
				// 解析响应数据
				String[] retData = new String[0];
				if (sb.toString().trim().length() > 0) {
					retData = sb.toString().trim().split("&");
				}
				Map<String,String> retMap = new HashMap<String,String>();
				for (String s : retData) {
					String[] keyValuePair = s.split("=");
					retMap.put(keyValuePair[0], keyValuePair[1]);
				}
				if (null != retMap.get("RetMessage") && !"".equals(((String) retMap.get("RetMessage")).trim())) {
					System.err.println(retMap.get("RetMessage"));
				}
			} while (fileInfo.getStartPos() < fileInfo.getSize());
		} catch (Exception e) {
			System.err.println("文件传输中断！");
			System.exit(0);
		}
		System.err.println("*********文件传输完成**************");
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}
	
}
