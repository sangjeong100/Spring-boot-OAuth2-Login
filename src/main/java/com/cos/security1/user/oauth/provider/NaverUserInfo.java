package com.cos.security1.user.oauth.provider;

import java.util.Map;

import com.cos.security1.user.constant.OAuth2Constants;

public class NaverUserInfo implements OAuth2UserInfo{

	private Map<String, Object> attributes; //getAttributes
	
	public NaverUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
		
	}
	
	@Override
	public String getProviderId() {
		// TODO Auto-generated method stub
		return (String) attributes.get("id");
	}

	@Override
	public String getProvider() {
		// TODO Auto-generated method stub
		return OAuth2Constants.OAUTH_PROVIDER.naver.toString();
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return (String) attributes.get("email");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return (String) attributes.get("name");
	}
	
}
