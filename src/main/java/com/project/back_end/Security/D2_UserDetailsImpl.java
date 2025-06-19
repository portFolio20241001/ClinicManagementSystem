package com.project.back_end.Security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.back_end.Entity.User;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security が認証に利用するためのユーザー情報を保持するクラス。
 * <p>
 * アプリケーションの User エンティティをラップして、必要な情報（ユーザー名、パスワード、ロールなど）を提供します。
 */
@RequiredArgsConstructor
public class D2_UserDetailsImpl implements UserDetails {

    /**
	 * Javaの「直列化（シリアライズ）」に使われます。
	 * Spring Securityがユーザー情報（UserDetails）をセッションに保持するときなど
	 * メモリ内の状態 → ファイルやバイト列に変換する
	 * ファイルやネットワーク経由で再びJavaオブジェクトとして読み戻す（＝デシリアライズ）
	 * 
	 * 　　　ま、安全のためつけとけってやつ
	 */
	private static final long serialVersionUID = 1268213488433381326L;
	
	private final User user;

    /**
     * ユーザーが保持するロールを Spring Security の形式に変換して返します。
     *
     * @return 単一の GrantedAuthority（例: ROLE_DOCTOR）
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name())
        );
    }

    /**
     * ユーザーのパスワードを返します。
     *
     * @return ハッシュ化されたパスワード
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * ユーザー名（ログインID）を返します。
     *
     * @return ユーザー名（username列）
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /** アカウントの有効期限チェック（常に true を返す） */
    @Override public boolean isAccountNonExpired() { return true; }

    /** アカウントのロックチェック（常に true を返す） */
    @Override public boolean isAccountNonLocked() { return true; }

    /** 認証情報の有効期限チェック（常に true を返す） */
    @Override public boolean isCredentialsNonExpired() { return true; }

    /** アカウントが有効かどうか（常に true を返す） */
    @Override public boolean isEnabled() { return true; }

    /**
     * 元の User エンティティを取得します。
     *
     * @return ユーザーオブジェクト
     */
    public User getUser() {
        return user;
    }
}
