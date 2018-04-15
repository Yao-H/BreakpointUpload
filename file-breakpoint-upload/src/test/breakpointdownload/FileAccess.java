package test.breakpointdownload;
/**
 * @description(��Ƭ�ļ���д)
 * @date 2017��8��30�� ����11:14:22
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileAccess {

	private RandomAccessFile randomAcceFile;
	private long writePos;
	
	public FileAccess(String fileName, long startPos) throws Exception {
		randomAcceFile = new RandomAccessFile(fileName, "rw");
		this.writePos = startPos;
		randomAcceFile.seek(writePos);
	}
	
	public long write(byte[] b, int startPos, int len) {
		long n = -1;
		try {
			randomAcceFile.write(b, startPos, len);
			n = len;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}
}
