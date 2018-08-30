package zarodha;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;

import com.neovisionaries.ws.client.WebSocketException;
import com.zarodha.model.OutputFeed;
import com.zarodha.util.CSVReader;
import com.zarodha.util.ExcelWriter;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.LTPQuote;
import com.zerodhatech.models.User;
/**
 * 
 * @author Jai1.Kumar
 *
 */
public class ZarodhaExecuter {

	static KiteConnect kiteConnect = null;
	static AppConstant appConstant = AppConstant.getInstance();
	static ApiCaller apiCaller = new ApiCaller();
	static String DATE_FORMAT = "MM/dd/YYYY";
	static SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
	static int BATCH_SIZE = 500;
	public static void main(String[] args) {
		ExcelWriter writer = new ExcelWriter();
		initializ();
		try {
			for (int i = 0; i < 1; i++) {
				// AllOperationCall();
				ArrayList<Long> tokens = new ArrayList<>();
				tokens.add(Long.parseLong("265"));
				//String[] input = {"MCX:ZINC18AUGFUT"};
				Object[] list = ((new CSVReader()).getInstruments(appConstant.CSV_PATH)).toArray();
				String[] input = new String[BATCH_SIZE];
				int j = 0;
				for(Object str:list){
					input[j] = (String)str;
					System.out.println(input[j]);
					j = j + 1;
					if(j == BATCH_SIZE){
						Map<String,LTPQuote> map = apiCaller.getLtp(kiteConnect, input);
						processResponse(map,writer);
						j = 0;
						input = new String[BATCH_SIZE];
						Thread.sleep(1000);
					}
				}
				
				//Thread.sleep(1000);
			}
		} catch (KiteException e) {
			// TODO: handle exception
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void processResponse(Map<String, LTPQuote> map,ExcelWriter writer) {
		List<OutputFeed> outputFeet = new ArrayList<>();
		
		Iterator<Map.Entry<String, LTPQuote>> entries = map.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String, LTPQuote> entry = entries.next();
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    OutputFeed feed = new OutputFeed();
		    feed.setInstrument(entry.getKey());
		    feed.setTime(String.valueOf((new Date()).getTime()));
		    feed.setLtp(String.valueOf(entry.getValue().lastPrice));
		    outputFeet.add(feed);
		}
		// Write to excel file
		writer.writeOutput(outputFeet);
		
	}

	private static void initializ() {
		
		kiteConnect = new KiteConnect(appConstant.key);

		try {

			// Set userId
			kiteConnect.setUserId(appConstant.userId);

			// Enable logs for debugging purpose. This will log request and
			// response.
			kiteConnect.setEnableLogging(true);

			// Get login url
			String url = kiteConnect.getLoginURL();

			// Set session expiry callback.
			kiteConnect.setSessionExpiryHook(new SessionExpiryHook() {
				@Override
				public void sessionExpired() {
					System.out.println("session expired");
				}
			});

			User user = new User();
			/*kiteConnect.setAccessToken("2eg1u80wnSOnuK7M2WUNxkAhXvKH1BHC");
			kiteConnect.setPublicToken("nBAvWAhl3ALwpj6WbtTdSghptbK88VH5");
			writeToFile(kiteConnect.getAccessToken(),kiteConnect.getPublicToken());*/
			//readPropertiesFile();
			
			if (!readPropertiesFile(user)) {
				user = kiteConnect.generateSession(appConstant.getTokenKeys().get("request_token"),
						appConstant.apiSecret);
				System.out.println("Access token:::"+user.accessToken);
				
				appConstant.getTokenKeys().put("access_token", user.accessToken);
				appConstant.getTokenKeys().put("public_token", user.publicToken);
				writeToFile(user.accessToken,user.publicToken);
			} else {
				/*user.accessToken; = appConstant.getTokenKeys().get("access_token");
				user.publicToken; = appConstant.getTokenKeys().get("public_token");*/
			}
			kiteConnect.setAccessToken(user.accessToken);
			kiteConnect.setPublicToken(user.publicToken);
			
		} catch (KiteException e) {
			System.out.println(e.message + " " + e.code + " " + e.getClass().getName());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	private static boolean readPropertiesFile(User user) {
		Properties prop = new Properties();
		InputStream input = null;
		
		try {

			input = new FileInputStream(appConstant.FILE_PATH);

			// load a properties file
			prop.load(input);
			if(prop.get("DATETIME")!= null && prop.get("DATETIME").toString().equalsIgnoreCase(format.format(new Date()))){
				user.accessToken = prop.getProperty("access_token");
				user.publicToken = prop.getProperty("public_token");
				return true;
			}
			appConstant.tocken = prop.getProperty("request_token");

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
		
	}

	/**
	 * 
	 * @param accessToken
	 * @param publicToken
	 */
	private static void writeToFile(String accessToken, String publicToken) {
		Properties prop = new Properties();
		OutputStream output = null;
		
		try {
			output = new FileOutputStream(appConstant.FILE_PATH);
			// set the properties value
			prop.put("DATETIME", format.format(new Date()));
			prop.put("access_token", accessToken);
			prop.put("public_token", publicToken);
			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		
	}

	/**
	 * 
	 */
	private static void AllOperationCall() {

		try {
			ApiCaller examples = new ApiCaller();

			examples.getProfile(kiteConnect);

			examples.getMargins(kiteConnect);

			examples.placeOrder(kiteConnect);

			examples.modifyOrder(kiteConnect);

			examples.cancelOrder(kiteConnect);

			examples.placeBracketOrder(kiteConnect);

			examples.modifyFirstLegBo(kiteConnect);

			examples.modifySecondLegBoSLM(kiteConnect);

			examples.modifySecondLegBoLIMIT(kiteConnect);

			examples.exitBracketOrder(kiteConnect);

			examples.getTriggerRange(kiteConnect);

			examples.placeCoverOrder(kiteConnect);

			examples.converPosition(kiteConnect);

			examples.getHistoricalData(kiteConnect);

			examples.getOrders(kiteConnect);

			examples.getOrder(kiteConnect);

			examples.getTrades(kiteConnect);

			examples.getTradesWithOrderId(kiteConnect);

			examples.getPositions(kiteConnect);

			examples.getHoldings(kiteConnect);

			examples.getAllInstruments(kiteConnect);

			examples.getInstrumentsForExchange(kiteConnect);

			examples.getQuote(kiteConnect);

			examples.getOHLC(kiteConnect);

			examples.getLTP(kiteConnect);

			examples.getMFInstruments(kiteConnect);

			examples.placeMFOrder(kiteConnect);

			examples.cancelMFOrder(kiteConnect);

			examples.getMFOrders(kiteConnect);

			examples.getMFOrder(kiteConnect);

			examples.placeMFSIP(kiteConnect);

			examples.modifyMFSIP(kiteConnect);

			examples.cancelMFSIP(kiteConnect);

			examples.getMFSIPS(kiteConnect);

			examples.getMFSIP(kiteConnect);

			examples.getMFHoldings(kiteConnect);

			examples.logout(kiteConnect);

			ArrayList<Long> tokens = new ArrayList<>();
			tokens.add(Long.parseLong("265"));
			examples.tickerUsage(kiteConnect, tokens);
		} catch (Exception e) {
			// TODO: handle exception
		} catch (KiteException e) {
			// TODO: handle exception
		}
	}
}
