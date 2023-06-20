package ibfbatch2miniproject.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import ibfbatch2miniproject.backend.model.CheckoutPayment;

@RestController
@RequestMapping(path="/api")
// @CrossOrigin(origins="*")
public class BackendStripeController {

    // gson object
    private static Gson gson = new Gson();

    // Method 1 
    // Post mapping from checkout
    @PostMapping("/payment")
    public String paymentWithCheckoutPage(@RequestBody CheckoutPayment payment) throws StripeException {
        
        // initialize stripe object with api key 
        init();
        
        // create stripe session params
        SessionCreateParams params = SessionCreateParams.builder()
				// We will use the credit card payment method 
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(payment.getSuccessUrl())
				.setCancelUrl(payment.getCancelUrl())
				.addLineItem(SessionCreateParams.LineItem.builder().setQuantity(payment.getQuantity())
                                                                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder().setCurrency(payment.getCurrency()).setUnitAmount(payment.getAmount())
												                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder().setName(payment.getName()).build())
                                                                    .build()).build()).build();
                                
        // create a stripe session
		Session session = Session.create(params);
		Map<String, String> responseData = new HashMap<>();
    
        // We get the sessionId and we put inside the response data you can get more info from the session object
		responseData.put("id", session.getId());
        
        // We can return only the sessionId as a String
		return gson.toJson(responseData);
        
    }

    private static void init() {
		Stripe.apiKey = "sk_test_51NIPDNC2ajusx5ahIPDHxipzN66jId8IqvbrEHcxofUDqFCrjBRFmjiB0OxdWAFllDJdyeyJZX6jOI3bDQPlzVI500gQMpLmGw";
	}
    
}
