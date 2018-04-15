package test.breakpointdownload;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @description(下载的文件信息)
 * @date 2017年8月30日 下午1:55:55
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class FileInfo {

	private String siteURL; // Site's URL
	private String filePath; // Saved File's Path
	private String fileName; // Saved File's Name
	private long size = 0; // file size(bytes)
	private String etag; // judge whether file has been modified
	private String oldEtag;
	
	public FileInfo() {}
	
	public FileInfo(String url, String filePath, String fileName) {
		this.siteURL = url;
		this.filePath = filePath;
		this.fileName = fileName;
		this.mkdirs();
		getFileOriginalInfo();
	}
	
	/**
	 * 确保文件目录存在
	 * */
	private void mkdirs() {
		File fileDir = new File(filePath);
		if (fileDir.isDirectory()) {
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
		}
	}
	
	/**
	 * 从服务器获取文件大小与MD5信息
	 * */
	private void getFileOriginalInfo() {
		try {
			URL url = new URL(siteURL);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestProperty("User-Agent", "NetFox");
			int retCode = httpConn.getResponseCode();
			if (retCode >= 400) {
				System.err.println("Error code: " + retCode);
			}
			// 从头信息中获取文件的大小
			size = Long.parseLong(httpConn.getHeaderField("Content-Length"));
			etag = httpConn.getHeaderField("ETag");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utility.log("File size: " + size + ", ETag: " + etag);
	}

	public String getSiteURL() {
		return siteURL;
	}

	public void setSiteURL(String siteURL) {
		this.siteURL = siteURL;
	}

	public String getFilePath() {
		return filePath;
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
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getOldEtag() {
		return oldEtag;
	}

	public void setOldEtag(String oldEtag) {
		this.oldEtag = oldEtag;
	}
	
}
