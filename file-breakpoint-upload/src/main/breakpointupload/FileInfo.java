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
 * @description(�ļ���Ϣ)
 * @date 2017��9��4�� ����11:00:22
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
	 * ��ȡ�ļ�����ϢժҪMD5
	 * @param filePath �ļ���Ŀ¼·��
	 * @param fileName �ļ�����
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
		    	// ÿ���ֽھ�ת������λ��16������
		    	result += Integer.toString( (b[i] & 0xff) + 0x100, 16).substring(1);
		    }
		    this.md5 = result;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��ȡ����˵��ļ���Ϣ
	 * */
	private Map<String,String> getServerFileInfo() {
		System.err.println("****************��ʼ��ȡ�������ʱ�ļ���Ϣ********************");
		Map<String,String> retMap = new HashMap<String,String>();
		try {
			URL url = new URL(this.url);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoOutput(true); // �Ƿ�������
			httpConn.setDoInput(true); // �Ƿ������
			httpConn.setUseCaches(false); // �Ƿ�ʹ�û���
			httpConn.setRequestMethod("POST"); // ���󷽷�
			httpConn.setRequestProperty("Accept", "*/*"); // ���յ���������
			httpConn.setRequestProperty("Connection", "Keep-Alive"); // ������
			httpConn.setRequestProperty("Charset", "UTF-8"); // ָ���������ݵ��ַ���
			httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); // ָ���û�����
			
			// ����������
			OutputStream os = httpConn.getOutputStream();
			StringBuilder requestContent = new StringBuilder();
			requestContent.append("MD5=" + md5);
			os.write(requestContent.toString().getBytes("UTF-8"));
			os.flush();
			os.close();
			
			// ��ȡ��Ӧ����
			StringBuilder sb = new StringBuilder();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "utf-8"));
			String line;
			while (null != (line = buffReader.readLine())) {
				sb.append(line);
			}
			buffReader.close();
			// �������ز���
			// ����-�ļ���MD5��Ϣ
		    System.err.println("MD5= " + this.md5);
			String[] retData = new String[0];
			if (sb.toString().trim().length() > 0) {
				retData = sb.toString().trim().split("&");
			}
			for (int i = 0; i < retData.length; i++) {
				String[] keyValuePair = retData[i].split("=");
				retMap.put(keyValuePair[0], keyValuePair[1]);
				// ����-���յ��ķ���˵��ļ��ϴ���Ϣ
				System.err.println(keyValuePair[0] + "=" + keyValuePair[1]);
			}
			// �����ļ������λ����Ϣ
			this.setStartPos(Long.parseLong(retMap.get("StartPos")));
			System.err.println("****************��ȡ�������ʱ�ļ���Ϣ����********************");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("��ȡ������ļ���Ϣʧ�ܣ�");
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
