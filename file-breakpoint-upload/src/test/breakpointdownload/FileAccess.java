package test.breakpointdownload;
/**
 * @description(碎片文件读写)
 * @date 2017年8月30日 上午11:14:22
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
