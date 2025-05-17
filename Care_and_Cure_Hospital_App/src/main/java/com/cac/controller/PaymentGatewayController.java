package com.cac.controller;

import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentGatewayController {

    private static final String RAZORPAY_KEY_ID = "rzp_test_48xaV0vyd3zQaL";
    private static final String RAZORPAY_KEY_SECRET = "mVV4Z93SnrgD52rN4YPkkqDP";

    @PostMapping("/createOrder")
    public String createOrder(@RequestBody Map<String, Object> data) {
        try {
            RazorpayClient client = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", ((int) data.get("amount")) * 100); // Amount in paisa (INR)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_123456");

            Order order = client.orders.create(orderRequest);
            return order.toString(); // Returns the order details
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to create order\"}";
        }
    }
}
