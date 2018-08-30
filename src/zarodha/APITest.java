package zarodha;

import java.io.IOException;

import org.json.JSONException;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Margin;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.TokenSet;

public class APITest {

	public static void main(String[] args) {
		
	try{
	
	// Initialize Kiteconnect using apiKey.
	KiteConnect kiteSdk = new KiteConnect("epndfs0qi2w1p9ci");

	// Set userId.
	kiteSdk.setUserId("GZ2488");
	
	/* First you should get request_token, public_token using kitconnect login and then use request_token, public_token, api_secret to make any kiteconnect api call.
	Get login url. Use this url in webview to login user, after authenticating user you will get requestToken. Use the same to get accessToken. */
	String url = kiteSdk.getLoginURL();

	String accessToken = kiteSdk.getAccessToken();
	// Get accessToken as follows,
	TokenSet tokenSet =  kiteSdk.renewAccessToken(accessToken, "isixhdbht151pwhwf41te58afb1k21ra");

	// Set request token and public token which are obtained from login process.
	kiteSdk.setAccessToken(tokenSet.accessToken);
	kiteSdk.setPublicToken(tokenSet.refreshToken);

	// Set session expiry callback.
	kiteSdk.setSessionExpiryHook(new SessionExpiryHook() {

		@Override
		public void sessionExpired() {
			 
			        System.out.println("session expired");                    
			    
			
		}
	   
	});

	// Get margins returns margin model, you can pass equity or commodity as arguments to get margins of respective segments.
	Margin margins = kiteSdk.getMargins("equity");
	System.out.println(margins.available.cash);
	System.out.println(margins.utilised.debits);

	/* Place order method requires a map argument which contains,
	tradingsymbol, exchange, transaction_type, order_type, quantity, product, price, trigger_price, disclosed_quantity, validity
	squareoff_value, stoploss_value, trailing_stoploss
	and variety  which can be value can be regular, bo, co, amo.
	place order which will return order model which will have only orderId in the order model.

	Following is an example param for SL order,
	if a call fails then KiteException will have error message in it
	Success of this call implies only order has been placed successfully, not order execution.*/
	
	OrderParams params = new OrderParams();
	
		params.quantity = Integer.parseInt("1");
		params.orderType = "SL";
		params.tradingsymbol = "HINDALCO";
		params.product = "CNC";
		params.exchange = "NSE";
		params.transactionType = "BUY";
		params.validity = "DAY";
		params.price = Double.parseDouble("158.0");
		params.triggerPrice = Double.parseDouble("157.5");

	Order order = null;
	
		order = kiteSdk.placeOrder(params, "regular");
		System.out.println(order.orderId);
	} catch (JSONException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (KiteException e) {
		e.printStackTrace();
	}
	
	}
}
