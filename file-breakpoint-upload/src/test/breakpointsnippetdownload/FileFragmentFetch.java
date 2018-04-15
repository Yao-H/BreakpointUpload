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
 * @description(�����ļ���Ƭ)
 * @date 2017��8��30�� ����11:12:43
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
	 * �����ļ�
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
				// logResponseHead(httpConnection); // ��ӡ��Ӧͷ��Ϣ
				
				byte[] b = new byte[512];
				int readSize;
				while ((readSize = input.read(b, 0, 512)) > 0 && start < end) {
					start += new FileAccess(fileInfo.getFilePath() + File.separator + fileInfo.getFileName(), start).write(b, 0, readSize);
				}
				Utility.log(range + endPosArr[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ���µ�ǰ����λ��
			currentPos++;
			File oldInfo = new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info");
			oldInfo.delete();
			initDownloadInfo(false);
		}
		// ɾ����ʱ�ļ�
		File tmpFile = new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info");
		tmpFile.delete();
		Utility.log("�ļ�������ɣ�");
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
				posCount = in.readInt();
				for (int i = 0; i < posCount; i++) {
					startPosArr[i] = in.readLong();
					endPosArr[i] = in.readLong();
				}
				currentPos = in.readInt();
				String oldEtag =  in.readUTF();
				if (!oldEtag.equals(fileInfo.getEtag())) { // MD5�ı䣬�ļ����޸�
					currentPos = 0; // ��������λ��
					File oldInfo = new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info");
					oldInfo.delete();
					initDownloadInfo(false); // ��������
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
			// ��ʼ������λ��
			for (int i = 0; i < posCount; i++) {
				startPosArr[i] = i * (fileInfo.getSize() / posCount);
			}
			for (int i = 0; i < posCount-1; i++) {
				endPosArr[i] = startPosArr[i+1];
			}
			endPosArr[posCount-1] = fileInfo.getSize();
			// �洢�ļ���������Ϣ
			DataOutputStream out = null;
			try {
				out = new DataOutputStream(new FileOutputStream(new File(fileInfo.getFilePath() + File.separator + fileInfo.getFileName() + ".info")));
				out.writeInt(posCount); // �ļ���Ƭ��
				for (int i = 0; i < posCount; i++) {
					out.writeLong(startPosArr[i]);
					out.writeLong(endPosArr[i]);
				}
				out.writeInt(currentPos); // ��ǰ����λ��
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
	 * �Ƿ��Ƕϵ�����
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
