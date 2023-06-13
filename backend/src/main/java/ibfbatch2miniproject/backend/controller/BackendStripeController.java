package ibfbatch2miniproject.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import ibfbatch2miniproject.backend.model.CheckoutPayment;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Controller
@RequestMapping(path="/api")
public class BackendStripeController {

    // gson object
    private static Gson gson = new Gson();

    // Post mapping from checkout
    // @PostMapping("/payment")
    // public String paymentWithCheckoutPage(@RequestBody CheckoutPayment payment) throws StripeException {
        
    //     // initialize stripe object with api key 
    //     init();
        
    //     // create stripe session params
    //     SessionCreateParams params = SessionCreateParams.builder()
	// 			// We will use the credit card payment method 
	// 			.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
	// 			.setMode(SessionCreateParams.Mode.PAYMENT)
    //             .setSuccessUrl(payment.getSuccessUrl())
	// 			.setCancelUrl(payment.getCancelUrl())
	// 			.addLineItem(SessionCreateParams.LineItem.builder().setQuantity(payment.getQuantity())
    //                                                                 .setPriceData(SessionCreateParams.LineItem.PriceData.builder().setCurrency(payment.getCurrency()).setUnitAmount(payment.getAmount())
	// 											                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder().setName(payment.getName()).build())
    //                                                                 .build()).build()).build();
                                
    //     // create a stripe session
	// 	Session session = Session.create(params);
	// 	Map<String, String> responseData = new HashMap<>();
    
    //     // We get the sessionId and we put inside the response data you can get more info from the session object
	// 	responseData.put("id", session.getId());
        
    //     // We can return only the sessionId as a String
	// 	return gson.toJson(responseData);
    // }

    @GetMapping("/api/stripe/payment-intent")
    public ResponseEntity<String> getPaymentIntent() throws StripeException {
        init(); 

        Map<String, Object> automaticPaymentMethods = new HashMap<>();
        automaticPaymentMethods.put("enabled", true);

        Map<String, Object> params = new HashMap<>();
        params.put("amount", 2000);
        params.put("currency", "sgd");
        params.put("automaticPaymentMethods", automaticPaymentMethods);

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        JsonObject resp = Json.createObjectBuilder()
                .add("clientSecret", paymentIntent.getClientSecret())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(resp.toString());
    }

    private static void init() {
		Stripe.apiKey = "sk_test_51NIPDNC2ajusx5ahIPDHxipzN66jId8IqvbrEHcxofUDqFCrjBRFmjiB0OxdWAFllDJdyeyJZX6jOI3bDQPlzVI500gQMpLmGw";
	}
    
}
