package main.breakpoint;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * @description(负责文件的存储) @data 2017年8月29日 上午12:11:01
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class FileAccessI implements Serializable {
	
	private static final long serialVersionUID = -5632480249861540459L;
	
	RandomAccessFile oSavedFile;
	long nPos;

	public FileAccessI() throws IOException {
		this("", 0);
	}

	public FileAccessI(String sName, long nPos) throws IOException {
		oSavedFile = new RandomAccessFile(sName, "rw");
		this.nPos = nPos;
		oSavedFile.seek(nPos);
	}

	public synchronized int write(byte[] b, int nStart, int nLen) {
		int n = -1;
		try {
			oSavedFile.write(b, nStart, nLen);
			n = nLen;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}
}
