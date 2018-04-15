package test.breakpointdownload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import main.breakpoint.Utility;

/**
 * @description(下载文件碎片)
 * @date 2017年8月30日 上午11:12:43
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class FileFragmentFetch {

	private FileInfo fileInfo;
	private long nextStartPos = 0;
	private long step = 1024 * 1024;
	
	public FileFragmentFetch(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
	
	/**
	 * 下载文件
	 * */
	public void download() {
		boolean isBreak = isBreakDownload();
		initDownloadInfo(isBreak);
		
		while (nextStartPos < fileInfo.getSize()) {
			try {
				URL url = new URL(fileInfo.getSiteURL());
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setRequestProperty("User-Agent", "NetFox");
				httpConn.setRequestProperty("ETag", fileInfo.getEtag()); // 文件MD5
				String range = "bytes=" + nextStartPos + "-";
				httpConn.setRequestProperty("RANGE", range); // 下载文件的偏移量
				
				Utility.log(range);
				
				String newETag = httpConn.getHeaderField("ETag");
				System.err.println("The new ETag: " + newETag + ", old ETag: " + fileInfo.getEtag());
				// 文件被修改重新下载
				if (!(fileInfo.getEtag().equals(newETag))) {
					this.nextStartPos = 0;
					fileInfo.setEtag(newETag);
					initDownloadInfo(false);
					this.download();
				}
				
				logResponseHead(httpConn); // 打印响应头信息
				
				InputStream input = httpConn.getInputStream();
				byte[] b = new byte[512];
				int readSize;
				long nextWritePos = nextStartPos;
				while ((readSize = input.read(b, 0, 512)) > 0 && nextWritePos - nextStartPos < step) {
					nextWritePos += new FileAccess(fileInfo.getFilePath() + File.separator + fileInfo.getFileName(), nextWritePos).write(b, 0, readSize);
				}
			} catch (Exception e) {
				System.err.println("服务器中断连接！");
			}
			// 更新当前下载位置
			nextStartPos += step;
			clearDownloadInfo();
			initDownloadInfo(false);
		}
		// 删除临时文件
		File tmpFile = new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info");
		tmpFile.delete();
		Utility.log("文件下载完成！");
	}
	
	/**
	 * 打印响应头信息
	 * */
	public void logResponseHead(HttpURLConnection httpConn) {
		System.out.println("*********响应头信息Start***********");
		for (int i = 1;; i++) {
			String header = httpConn.getHeaderFieldKey(i);
			if (header != null)
				// responseHeaders.put(header,httpConnection.getHeaderField(header));
				Utility.log(header + " : " + httpConn.getHeaderField(header));
			else
				break;
		}
		System.out.println("*********响应头信息End***********");
	}
	
	/**
	 * 存取文件下载信息
	 * */
	private void initDownloadInfo(boolean isBreak) {
		if (isBreak) {
			// 读取文件的下载信息
			DataInputStream in = null;
			try {
				in = new DataInputStream(new FileInputStream(new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info")));
				nextStartPos = in.readLong(); // 当前下载位置
				String oldEtag =  in.readUTF(); // MD5信息
				fileInfo.setEtag(oldEtag);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != in) {
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			// 存储文件的下载位置信息
			DataOutputStream out = null;
			try {
				out = new DataOutputStream(new FileOutputStream(new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info")));
				out.writeLong(nextStartPos); // 当前下载位置
				out.writeUTF(fileInfo.getEtag()); // 文件的MD5值
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != out) {
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 清空临时文件信息
	 * */
	public void clearDownloadInfo() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info"));
			writer.write("");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != writer)
					writer.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 通过判断临时文件是否存在来断定是否是断点下载
	 * */
	private boolean isBreakDownload() {
		File file = new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info");
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
}
