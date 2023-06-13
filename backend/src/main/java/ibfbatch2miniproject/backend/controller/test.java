// package ibfbatch2miniproject.backend.controller;

// import java.util.ArrayList;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.stripe.exception.StripeException;

// public class test {


// @RestController 
// @RequestMapping("/api/stripe") 
// public class StripeController { 
 
//     @Autowired 
//     private StripeService stripeService; 
 
//     @PostMapping("/create-checkout-session") 
//     public ResponseEntity<?> createCheckoutSession(@RequestBody Map<String, Object> request) { 
//         List<Map<String, Object>> lineItems = (List<Map<String, Object>>) request.get("line_items"); 
//         List<SessionCreateParams.LineItem> stripeLineItems = new ArrayList<>(); 
 
//         for (Map<String, Object> item : lineItems) { 
//             Map<String, Object> priceData = (Map<String, Object>) item.get("price_data"); 
//             Map<String, Object> productData = (Map<String, Object>) priceData.get("product_data"); 
 
//             SessionCreateParams.LineItem stripeItem = SessionCreateParams.LineItem.builder() 
//                     .setQuantity(Long.valueOf(String.valueOf(item.get("quantity")))) 
//                     .setPriceData( 
//                             SessionCreateParams.LineItem.PriceData.builder() 
//                                     .setCurrency((String) priceData.get("currency")) 
//                                     .setUnitAmount(Long.valueOf(String.valueOf(priceData.get("unit_amount")))) 
//                                     .setProductData( 
//                                             SessionCreateParams.LineItem.PriceData.ProductData.builder() 
//                                                     .setName((String) productData.get("name")) 
//                                                     .build()) 
//                                     .build()) 
//                     .build(); 
 
//             stripeLineItems.add(stripeItem); 
//         } 
 
//         try { 
//             Session session = stripeService.createSession(stripeLineItems); 
//             return ResponseEntity.ok(session.toJson()); 
//         } catch (StripeException e) { 
//             e.printStackTrace(); // log the exception for debugging 
//             return ResponseEntity.status(500).body(e.getMessage()); 
//         } 
//     } 
 
//     @GetMapping("/checkout-session") 
//     public ResponseEntity<?> getCheckoutSession(@RequestParam String sessionId) { 
//         try { 
//             Session session = stripeService.getSession(sessionId); 
//             return ResponseEntity.ok(session.toJson()); 
//         } catch (StripeException e) { 
//             return ResponseEntity.status(500).body(e.getMessage()); 
//         } 
//     } 
 
// }
    
// }
