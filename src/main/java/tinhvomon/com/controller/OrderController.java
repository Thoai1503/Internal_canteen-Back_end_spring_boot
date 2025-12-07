package tinhvomon.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tinhvomon.com.repository.OrderRepository;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderRepository orderRepository;
    
    @Autowired
    public OrderController (OrderRepository orderRepository) {
    	this.orderRepository=orderRepository;
    }
    @GetMapping("/user/{user_id}")
    public ResponseEntity findByUserId (@PathVariable int user_id) {
    	var list = orderRepository.FindByUserIdList(user_id);
    	return ResponseEntity.ok(list);
    }
    @GetMapping("")
    public ResponseEntity findAll() {
    	var list = orderRepository.getAllList().toArray();
    	return ResponseEntity.ok(list);
    }
    
}
