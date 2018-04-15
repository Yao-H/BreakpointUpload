package main.breakpointupload;
/**
 * @description(键值对)
 * @date 2017年9月5日 下午2:40:15
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class KeyValuePair {

	private String key;
	private String value;
	
	public KeyValuePair(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
