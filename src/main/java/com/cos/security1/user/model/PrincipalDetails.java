package com.cos.security1.user.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Data;

// Authentication 객체에 저장할 수 있는 유일한 타입
@Data
public class PrincipalDetails implements UserDetails, OAuth2User{

	private User user;
	private Map<String, Object> attributes;

	//일반로그인
	public PrincipalDetails(User user) {
		this.user = user;
	}

	//OAuth 로그인
	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		//우리 사이트의 1년동안 회원이 로그인을 안하면 휴면계정으로함 
		//현재날짜 > user.getLoginDate() + 1year
		return true;
	}
	//OAuth 대응
	@Override
	public Map<String, Object> getAttributes(){
		return attributes;
	}
	
	
	//OAuth 대응
	@Override
	public String getName() {
		return null;
	}
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collet = new ArrayList<GrantedAuthority>();
		collet.add(()->{ return user.getRole();});
		return collet;
	}


	
}
