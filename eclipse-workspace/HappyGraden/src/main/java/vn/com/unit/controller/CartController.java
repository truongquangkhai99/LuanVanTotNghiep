package vn.com.unit.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import vn.com.unit.dto.CartDto;
import vn.com.unit.entity.Account;
import vn.com.unit.service.AccountService;
import vn.com.unit.service.CartService;
import vn.com.unit.service.CategoryService;
import vn.com.unit.utils.CommonUtils;

@Controller
public class CartController {

	@Autowired
	CartService cartService;

	@Autowired
	CategoryService categoryService;
	
	@Autowired
	AccountService accountService;

	@GetMapping("/cart")
	public ModelAndView cart(Model model) {

		int total_cart_item= 0;
		Long total = 0L;
		Account account = accountService.findCurrentAccount();
		total_cart_item = cartService.countAllCartItemByCurrentAccount(account.getAccountId());
		model.addAttribute("total_cart_item", total_cart_item);
	
		model.addAllAttributes(CommonUtils.getMapHeaderAtribute(model, categoryService));

		List<CartDto> list_cart_item = cartService.findAllCartItemByCurrentAccount();

		model.addAttribute("list_cart_item", list_cart_item);

		total = cartService.calculateCartTotalByCurrentAccount();

		model.addAttribute("total", total);
		model.addAttribute("total_price", total);
		model.addAttribute("name", account.getAccountName());
		model.addAttribute("phone", account.getAccountPhone());
		model.addAttribute("address", account.getAccountAddress());

		model.addAttribute("title", "Cart");
		return new ModelAndView("shopping-cart");
	}

	@PutMapping("/cart/add")
	@ResponseBody
	public ResponseEntity<String> add(Model model, @RequestBody Map<String, String> json) {		
		
		int productQuantity = 1;	
		if (json.get("productQuantity") != null) {
			productQuantity = Integer.valueOf(json.get("productQuantity"));
		}
		
		cartService.addCartItemCurrentAccount(Long.valueOf(json.get("productId")), productQuantity);
		Account account = accountService.findCurrentAccount();
		int total_cart_item = cartService.countAllCartItemByCurrentAccount(account.getAccountId());
		Long total = cartService.calculateCartTotalByCurrentAccount();
		return ResponseEntity.ok(
				"{\"msg\" : \"Add product succes! Please check again!\", \"total_item\" : \""+total_cart_item+"\", \"total\" : \""+total+"\"  }");
	}
	
	
//	@DeleteMapping("/cart/deletee")
//	@ResponseBody
//	public ResponseEntity<String> delete(Model model, @RequestBody Map<String, String> json) {
//		
//		Long curent_account_id = accountService.findCurrentAccount().getAccountId();
//		cartService.deleteCartItemCurrentAccount(Long.valueOf(json.get("product_id")), curent_account_id);
//		
//		return ResponseEntity.ok(
//				"{\"msg\" : \"Delete product succces! Please check again!\" }");
//	}

	@GetMapping("/cart/add/{productId}")
	@ResponseBody
	public ResponseEntity<String> addProductIntoCart(@PathVariable Long productId) {
		cartService.addCartItemCurrentAccount(productId, 1);
		return new ResponseEntity<String>("Add product succes! Please check again!", HttpStatus.OK);
		//return ResponseEntity.ok("{}");
	}
	
	@DeleteMapping("/cart/delete/{product_id}")
	public ResponseEntity<Boolean> AdminDisable(Model model, @PathVariable("product_id") Long product_id,
			HttpServletRequest request) {
		Long curent_account_id = accountService.findCurrentAccount().getAccountId();
		cartService.deleteCartItemCurrentAccount(product_id, curent_account_id);
		return  ResponseEntity.ok(null);
	}
}
