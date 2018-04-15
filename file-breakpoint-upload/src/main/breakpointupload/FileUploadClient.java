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
 * @description(�ϵ��ϴ��ͻ���)
 * @date 2017��9��4�� ����10:59:24
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class FileUploadClient {
	// ÿ��post����֮��ķָ����������趨��ֻҪ�����������ַ����ظ�����
	private static final String BOUNDARY = "HV2ymHFg03ehbqgZCaKO6jyH";
	
	private FileInfo fileInfo;
	private String uploadUrl;
	
	public FileUploadClient() {}
	
	public FileUploadClient(FileInfo fileInfo, String uploadUrl) {
		this.fileInfo = fileInfo;
		this.uploadUrl = uploadUrl;
	}
	
	/**
	 * ģ�����������ϴ��ļ�
	 * */
	public void upload() {
		try {
			// ģ��form�������ļ���Ƭ��ÿһ�е����ݱ�����(\r\n)��β
			do {
				URL url = new URL(uploadUrl);
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setDoOutput(true); // �Ƿ�������
				httpConn.setDoInput(true); // �Ƿ������
				httpConn.setUseCaches(false); // �Ƿ�ʹ�û���
				httpConn.setRequestMethod("POST"); // ���󷽷�
				httpConn.setRequestProperty("Accept", "*/*"); // ������������
				httpConn.setRequestProperty("Connection", "Keep-Alive"); // ������
				httpConn.setRequestProperty("Charset", "UTF-8"); // �������
				httpConn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); // �ͻ�����
				httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY); // ������������
				httpConn.connect();
				
				// ��ǰ���ӵ����������
				OutputStream os = httpConn.getOutputStream();
				
				// http������-�ļ�����
				os.write(("--" + BOUNDARY + "\r\n").getBytes("UTF-8"));
				StringBuffer contentBody = new StringBuffer();
				contentBody.append("Content-Disposition: form-data;name=\"")
				.append("uploadSnippet\";") // �൱��input��ǩ��name����
				.append("filename=\"")
				.append(fileInfo.getFileRealPath() + "\"\r\n") // �ļ���ȫ���ƣ�����·��
				.append("Content-Type: application/octet-stream\r\n")
				.append("\r\n");
				os.write(contentBody.toString().getBytes("UTF-8"));
				
				// ������������ļ���Ƭ
				RandomAccessFile raf = new RandomAccessFile(fileInfo.getFileRealPath(), "r");
				raf.seek(fileInfo.getStartPos()); // �����ļ��α�(file-pointer)��λ��
				byte[] buff = new byte[1024 * 1024];
				int readnum = 0;
				readnum = raf.read(buff);
				raf.close();
				os.write(buff, 0, readnum);
				os.write("\r\n".getBytes("UTF-8"));
				os.write(("--" + BOUNDARY + "\r\n").getBytes("UTF-8"));
				
				// �����ļ���ȡ����ʼλ��
				fileInfo.setStartPos(fileInfo.getStartPos() + readnum);
				
				// http������-��ͨ����
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
						// http���������
						contentBody.append("--" + BOUNDARY + "--\r\n\r\n");
					} else {
						contentBody.append("--" + BOUNDARY + "\r\n");
					}
				}
				os.write(contentBody.toString().getBytes("UTF-8"));
				os.flush();
				os.close();
				
				// ����-��ǰ�ļ�����Ϣ
				System.err.println("StartPos= " + fileInfo.getStartPos() + ", FileSize= " + fileInfo.getSize());
				
				// ���շ������Ӧ
				StringBuilder sb = new StringBuilder();
				BufferedReader buffReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
				String line;
				while (null != (line = buffReader.readLine())) {
					sb.append(line);
				}
				buffReader.close();
				// ������Ӧ����
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
			System.err.println("�ļ������жϣ�");
			System.exit(0);
		}
		System.err.println("*********�ļ��������**************");
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
