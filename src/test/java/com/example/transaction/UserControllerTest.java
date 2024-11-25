package com.example.transaction;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import com.example.transaction.service.UserService;
import com.example.transaction.controller.UserController;
import com.example.transaction.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // Initialize
public class UserControllerTest {
	@Autowired
	private UserController userController;

	@Autowired
	private UserService userService;

	@Test
	public void testLogin_Success() {
		Map<String, String> payload = new HashMap<>();
		payload.put("email", "smridhi@gmail.com");
		payload.put("password", "katyal");

		String result = userController.login(payload);
		assertEquals("Login Done", result);
	}

	@Test
	public void testLogin_Failure() {
		Map<String, String> payload = new HashMap<>();
		payload.put("email", "smri@gmail.com");
		payload.put("password", "katyal");

		String result = userController.login(payload);
		assertEquals("Invalid Credentials", result);
	}

	@Test
	public void testRegister_Success() {
		Map<String, String> payload = new HashMap<>();
		payload.put("email", "hello@gmail.com");
		payload.put("password", "world");

		String result = userController.register(payload);
		assertEquals("Registration successful", result);
	}

	@Test
	public void testRegister_Failure() {
		Map<String, String> payload = new HashMap<>();
		payload.put("email", "smri@gmail.com");
		payload.put("password", "katyal");

		String result = userController.register(payload);
		assertEquals("Email already in use", result);
	}

	@Test
	public void Transaction_Success() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("userId", 2L);
		payload.put("amount", 100.0);

		String result = userController.makeTransaction(payload);
		assertEquals("Transaction successful", result);
	}

	@Test
	@Disabled
	public void Transaction_Failure() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("userId", 3L);
		payload.put("amount", 10000.0);

		String result = userController.makeTransaction(payload);
		assertEquals("Insufficient balance or transaction limit reached", result);
	}

	@Test
	@Disabled
	public void Transaction_Failure_UserNotFound() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("id", -1L); // Invalid ID
		payload.put("amount", 100.0);

		String result = userController.makeTransaction(payload);
		assertEquals("User not found", result);
	}

	@Test
	public void getTransactionHistory_Success() {
		Long userId = 2L;
		ResponseEntity<List<Transaction>> response = userController.getTransactionHistory(userId);

		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
	}



	@Test
	public void getTransactionHistory_Failure() {
		Long userId = 14L; // No transaction for this ID
		ResponseEntity<List<Transaction>> response = userController.getTransactionHistory(userId);

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}
	@Test
	public void testUpdateUser_Success() {
		Map<String, String> payload = new HashMap<>();
		payload.put("email", "happy@example.com");
		payload.put("password", "onlywell");

		ResponseEntity<String> response = userController.updateUser(3L, payload); // Assuming userId 1 exists
		assertEquals(200, response.getStatusCodeValue());
		assertEquals("User updated successfully", response.getBody());
	}

	@Test
	public void testUpdateUser_Failure_UserNotFound() {
		Map<String, String> payload = new HashMap<>();
		payload.put("email", "newemail@example.com");
		payload.put("password", "newpassword");

		ResponseEntity<String> response = userController.updateUser(-1L, payload); // Invalid userId
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void testDeleteUser_Success() {
		ResponseEntity<String> response = userController.deleteUser(1L); // Assuming userId 1 exists
		assertEquals(200, response.getStatusCodeValue());
		assertEquals("User deleted successfully", response.getBody());
	}

	@Test
	public void testDeleteUser_Failure_UserNotFound() {
		ResponseEntity<String> response = userController.deleteUser(-1L); // Invalid userId
		assertEquals(404, response.getStatusCodeValue());
	}
}

