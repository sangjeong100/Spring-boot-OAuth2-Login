package com.cos.security1.user.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.user.constant.OAuth2Constants;
import com.cos.security1.user.model.PrincipalDetails;
import com.cos.security1.user.model.User;
import com.cos.security1.user.oauth.provider.FacebookUserInfo;
import com.cos.security1.user.oauth.provider.GoogleUserInfo;
import com.cos.security1.user.oauth.provider.NaverUserInfo;
import com.cos.security1.user.oauth.provider.OAuth2UserInfo;
import com.cos.security1.user.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{ 
	
	@Autowired
	private UserRepository userRepo;
	
	// 구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("getClientRegistration : " + userRequest.getClientRegistration()); // 우리서버 정보가 들어있음
																					// registrationId로 어떤 OAuth로 로그인했는지 확인가능
		System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		// 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken 요청
		//userRequest정보 -> loadUser함수  -> 회원프로필
		System.out.println("getAttributes : " + oauth2User.getAttributes());
		
		OAuth2UserInfo oAuth2UserInfo = null;
		if(userRequest.getClientRegistration().getRegistrationId().equals(OAuth2Constants.OAUTH_PROVIDER.google.toString())) {
			 System.out.println("구글 로그인 요청");
			 oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
		} else if(userRequest.getClientRegistration().getRegistrationId().equals(OAuth2Constants.OAUTH_PROVIDER.facebook.toString())) {
			System.out.println("페이스북 로그인 요청");
			oAuth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
		} else if(userRequest.getClientRegistration().getRegistrationId().equals(OAuth2Constants.OAUTH_PROVIDER.naver.toString())){
			System.out.println("네이버 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
		}
		else {
			System.out.println("구글과 페이스북, 네이버만 지원");
		}
		
		BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
		
		//String provider = userRequest.getClientRegistration().getClientId(); //google
		String provider = oAuth2UserInfo.getProvider();
		//String providerId = oauth2User.getAttribute("sub");
		String providerId = oAuth2UserInfo.getProviderId();
		String username = provider+"_"+providerId; // google_providerId
		String password = bcryptPasswordEncoder.encode("1q2w3e$R");
		//String email = oauth2User.getAttribute("email");
		String email = oAuth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		Optional<User> userOptional = userRepo.findByUsername(username);
		User user = User.builder()
				.username(username)
				.email(email)
				.provider(provider)
				.providerId(providerId)
				.password(password)
				.role(role)
				.build();
		if(userOptional.isEmpty()) {
			System.out.println("최초로그인");
			userRepo.save(user);
			
		} 
		
		//OAuth2인 경우 처리  -> Authentication안에 들어감
		return new PrincipalDetails(user, oauth2User.getAttributes());
	}
	
}
