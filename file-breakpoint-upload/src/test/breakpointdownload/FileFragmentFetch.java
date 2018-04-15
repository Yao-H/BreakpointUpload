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
 * @description(�����ļ���Ƭ)
 * @date 2017��8��30�� ����11:12:43
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
	 * �����ļ�
	 * */
	public void download() {
		boolean isBreak = isBreakDownload();
		initDownloadInfo(isBreak);
		
		while (nextStartPos < fileInfo.getSize()) {
			try {
				URL url = new URL(fileInfo.getSiteURL());
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setRequestProperty("User-Agent", "NetFox");
				httpConn.setRequestProperty("ETag", fileInfo.getEtag()); // �ļ�MD5
				String range = "bytes=" + nextStartPos + "-";
				httpConn.setRequestProperty("RANGE", range); // �����ļ���ƫ����
				
				Utility.log(range);
				
				String newETag = httpConn.getHeaderField("ETag");
				System.err.println("The new ETag: " + newETag + ", old ETag: " + fileInfo.getEtag());
				// �ļ����޸���������
				if (!(fileInfo.getEtag().equals(newETag))) {
					this.nextStartPos = 0;
					fileInfo.setEtag(newETag);
					initDownloadInfo(false);
					this.download();
				}
				
				logResponseHead(httpConn); // ��ӡ��Ӧͷ��Ϣ
				
				InputStream input = httpConn.getInputStream();
				byte[] b = new byte[512];
				int readSize;
				long nextWritePos = nextStartPos;
				while ((readSize = input.read(b, 0, 512)) > 0 && nextWritePos - nextStartPos < step) {
					nextWritePos += new FileAccess(fileInfo.getFilePath() + File.separator + fileInfo.getFileName(), nextWritePos).write(b, 0, readSize);
				}
			} catch (Exception e) {
				System.err.println("�������ж����ӣ�");
			}
			// ���µ�ǰ����λ��
			nextStartPos += step;
			clearDownloadInfo();
			initDownloadInfo(false);
		}
		// ɾ����ʱ�ļ�
		File tmpFile = new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info");
		tmpFile.delete();
		Utility.log("�ļ�������ɣ�");
	}
	
	/**
	 * ��ӡ��Ӧͷ��Ϣ
	 * */
	public void logResponseHead(HttpURLConnection httpConn) {
		System.out.println("*********��Ӧͷ��ϢStart***********");
		for (int i = 1;; i++) {
			String header = httpConn.getHeaderFieldKey(i);
			if (header != null)
				// responseHeaders.put(header,httpConnection.getHeaderField(header));
				Utility.log(header + " : " + httpConn.getHeaderField(header));
			else
				break;
		}
		System.out.println("*********��Ӧͷ��ϢEnd***********");
	}
	
	/**
	 * ��ȡ�ļ�������Ϣ
	 * */
	private void initDownloadInfo(boolean isBreak) {
		if (isBreak) {
			// ��ȡ�ļ���������Ϣ
			DataInputStream in = null;
			try {
				in = new DataInputStream(new FileInputStream(new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info")));
				nextStartPos = in.readLong(); // ��ǰ����λ��
				String oldEtag =  in.readUTF(); // MD5��Ϣ
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
			// �洢�ļ�������λ����Ϣ
			DataOutputStream out = null;
			try {
				out = new DataOutputStream(new FileOutputStream(new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info")));
				out.writeLong(nextStartPos); // ��ǰ����λ��
				out.writeUTF(fileInfo.getEtag()); // �ļ���MD5ֵ
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
	 * �����ʱ�ļ���Ϣ
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
	 * ͨ���ж���ʱ�ļ��Ƿ�������϶��Ƿ��Ƕϵ�����
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
