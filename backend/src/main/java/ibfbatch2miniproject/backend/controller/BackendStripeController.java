package ibfbatch2miniproject.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import ibfbatch2miniproject.backend.model.CheckoutPayment;

@RestController
@RequestMapping(path="/api")
@CrossOrigin(origins="https://checkout.stripe.com")
public class BackendStripeController {

    // gson object
    private static Gson gson = new Gson();

    // Method 1 
    // Post mapping from checkout
    @PostMapping("/payment")
    @CrossOrigin("https://checkout.stripe.com")
    public ResponseEntity<String> paymentWithCheckoutPage(@RequestBody CheckoutPayment payment) throws StripeException {
        
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
		String jsonResponse = gson.toJson(responseData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origins", "https://checkout.stripe.com");
        
        return ResponseEntity.ok().headers(headers).body(jsonResponse);
    }

    @GetMapping("/checkout-session") 
    public ResponseEntity<?> getCheckoutSession(@RequestParam String sessionId) { 
        try { 
            init(); 
            Session session = Session.retrieve(sessionId);
            return ResponseEntity.ok(session.toJson()); 
        } catch (StripeException e) { 
            return ResponseEntity.status(500).body(e.getMessage()); 
        } 
    } 

    // Method 2 
    // @GetMapping("/api/payment-intent/{sessionId}")
    // public ResponseEntity<String> getPaymentIntent() throws StripeException {
    //     init(); 

    //     Map<String, Object> automaticPaymentMethods = new HashMap<>();
    //     automaticPaymentMethods.put("enabled", true);

    //     Map<String, Object> params = new HashMap<>();
    //     params.put("amount", 2000);
    //     params.put("currency", "sgd");
    //     params.put("automaticPaymentMethods", automaticPaymentMethods);

    //     PaymentIntent paymentIntent = PaymentIntent.create(params);

    //     JsonObject resp = Json.createObjectBuilder()
    //             .add("clientSecret", paymentIntent.getClientSecret())
    //             .build();

    //     return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(resp.toString());
    // }

    private static void init() {
		Stripe.apiKey = "sk_test_51NIPDNC2ajusx5ahIPDHxipzN66jId8IqvbrEHcxofUDqFCrjBRFmjiB0OxdWAFllDJdyeyJZX6jOI3bDQPlzVI500gQMpLmGw";
	}
    
}
