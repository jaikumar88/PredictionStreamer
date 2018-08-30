package zarodha;

import java.util.HashMap;
import java.util.Map;

public class AppConstant {

	public String tocken = "z07T93qBnCXiNI5sbbHjSWg6rE98XIJI";

	public Map<String, String> tokenKeys = new HashMap<>();

	public String key = "epndfs0qi2w1p9ci";

	public String userId = "GZ2488";

	public String apiSecret = "isixhdbht151pwhwf41te58afblk21ra";

	double ltp = 0.00;

	private static AppConstant single_instance = null;
	
	public String FILE_PATH="D:\\records.properties";
	
	public String CSV_PATH = "D:\\instruments.csv";

	private AppConstant() {

	}

	public static AppConstant getInstance() {
		if (single_instance == null)
			single_instance = new AppConstant();

		return single_instance;
	}

	/**
	 * @return the tocken
	 */
	public String getTocken() {
		return tocken;
	}

	/**
	 * @param tocken
	 *            the tocken to set
	 */
	public void setTocken(String tocken) {
		this.tocken = tocken;
	}

	/**
	 * @return the tokenKeys
	 */
	public Map<String, String> getTokenKeys() {

		if (tokenKeys.isEmpty()) {
			tokenKeys.put("request_token", tocken);
		}
		return tokenKeys;
	}

	/**
	 * @param tokenKeys
	 *            the tokenKeys to set
	 */
	public void setTokenKeys(Map<String, String> tokenKeys) {
		this.tokenKeys = tokenKeys;
	}

}
