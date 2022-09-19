package com.cos.security1.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.user.model.PrincipalDetails;
import com.cos.security1.user.model.User;
import com.cos.security1.user.repository.UserRepository;

// 시큐리티 설정에서 loginProcessingUrl("/login");
// login요청이 오면 자동으로 UserDetailsService 타이븡로 IoC되어있는 loadUserByUsername호
@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	// 시큐리티 session(내부 Authentication(내부 User Details))
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isEmpty()) {
			return null;
		}
		return new PrincipalDetails(user.get());
	}

}
