package com.project.back_end.Security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.back_end.Entity.User;
import com.project.back_end.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security によるログイン時に使用されるユーザー情報取得サービス。
 * <p>
 * 指定されたユーザー名（username）に対応する User エンティティをDBから検索し、
 * UserDetailsImpl に変換して返します。
 */
@Service
@RequiredArgsConstructor
public class D1_UserDetailsServiceImpl_AB implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * ユーザー名をキーにしてユーザー情報を取得し、UserDetails として返します。
     *
     * @param username ログイン時に入力されたユーザー名
     * @return Spring Security 形式のユーザー情報
     * @throws UsernameNotFoundException ユーザーが見つからなかった場合
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

        return new D2_UserDetailsImpl(user);
    }
}
