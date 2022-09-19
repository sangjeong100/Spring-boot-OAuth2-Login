package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.user.model.PrincipalDetails;
import com.cos.security1.user.model.User;
import com.cos.security1.user.repository.UserRepository;


@Controller
public class IndexController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	@ResponseBody
	public String loginTest(Authentication authentication
						,@AuthenticationPrincipal PrincipalDetails userDetails) { //DI(의존성 주입)
		System.out.println("/test/login ===============");
		System.out.println("authenticaiton : "+authentication.getPrincipal());
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("authentication  : "+principalDetails.getUser());
		
		System.out.println("userDetails : " + userDetails.getUser());
		
		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	@ResponseBody
	public String oauthLoginTest(Authentication authentication
			,@AuthenticationPrincipal PrincipalDetails userDetails) { //DI(의존성 주입)
		System.out.println("/test/login ===============");
		System.out.println("authenticaiton : "+authentication.getPrincipal());
		System.out.println("auth2User  : "+userDetails.getAttributes());
		
		return "세션 정보 확인하기";
	}
	
	

	@GetMapping({ "", "/" })
	public String index() {
		//머스테치 기본폴더 src/main/resources/
		return "index"; // src/main/resources/template/index.mustache
	}

	//OAuth 로그인을해서 PrincipalDetails로 받을 수 있고
	//일반 로그인을 해도 PrincipalDetails로 받을 수 있다.
	@GetMapping("/user")
	@ResponseBody
	public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : "+ principalDetails.getUser());
		return "user";
	}

	@GetMapping("/admin")
	public String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public String manager() {
		return "manager";
	}
	

	@GetMapping("/loginForm")
	public String login() {
		return "loginForm";
	}

	@GetMapping("/joinForm")
	public String join() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String joinProc(User user) {
		System.out.println("회원가입 진행 : " + user);
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		user.setRole("ROLE_USER");
		userRepository.save(user);
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	@ResponseBody
	public String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	@ResponseBody
	public String data() {
		return "data 정보";
	}
}
