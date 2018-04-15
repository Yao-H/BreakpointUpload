package test.breakpointsnippetdownload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
	private int posCount = 10;
	private long[] startPosArr = new long[posCount];
	private long[] endPosArr = new long[posCount];
	private int currentPos = 0;
	
	public FileFragmentFetch(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
	
	/**
	 * 下载文件
	 * */
	public void download() {
		boolean isBreak = isBreakDownload();
		initDownloadInfo(isBreak);
		
		for (int i = currentPos; i < posCount; i++) {
			long start = startPosArr[i];
			long end = endPosArr[i];
			try {
				URL url = new URL(fileInfo.getSiteURL());
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setRequestProperty("User-Agent", "NetFox");
				String range = "bytes=" + startPosArr[i] + "-";
				httpConn.setRequestProperty("RANGE", range);
				
				
				InputStream input = httpConn.getInputStream();
				// logResponseHead(httpConnection); // 打印响应头信息
				
				byte[] b = new byte[512];
				int readSize;
				while ((readSize = input.read(b, 0, 512)) > 0 && start < end) {
					start += new FileAccess(fileInfo.getFilePath() + File.separator + fileInfo.getFileName(), start).write(b, 0, readSize);
				}
				Utility.log(range + endPosArr[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 更新当前下载位置
			currentPos++;
			File oldInfo = new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info");
			oldInfo.delete();
			initDownloadInfo(false);
		}
		// 删除临时文件
		File tmpFile = new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info");
		tmpFile.delete();
		Utility.log("文件下载完成！");
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
				posCount = in.readInt();
				for (int i = 0; i < posCount; i++) {
					startPosArr[i] = in.readLong();
					endPosArr[i] = in.readLong();
				}
				currentPos = in.readInt();
				String oldEtag =  in.readUTF();
				if (!oldEtag.equals(fileInfo.getEtag())) { // MD5改变，文件被修改
					currentPos = 0; // 重置下载位置
					File oldInfo = new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info");
					oldInfo.delete();
					initDownloadInfo(false); // 重新下载
				}
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
			// 初始化下载位置
			for (int i = 0; i < posCount; i++) {
				startPosArr[i] = i * (fileInfo.getSize() / posCount);
			}
			for (int i = 0; i < posCount-1; i++) {
				endPosArr[i] = startPosArr[i+1];
			}
			endPosArr[posCount-1] = fileInfo.getSize();
			// 存储文件的下载信息
			DataOutputStream out = null;
			try {
				out = new DataOutputStream(new FileOutputStream(new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info")));
				out.writeInt(posCount); // 文件分片数
				for (int i = 0; i < posCount; i++) {
					out.writeLong(startPosArr[i]);
					out.writeLong(endPosArr[i]);
				}
				out.writeInt(currentPos); // 当前下载位置
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
	 * 是否是断点下载
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
