package com.project.back_end.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ログイン成功時に、ユーザーのロールに応じてダッシュボードにリダイレクトするハンドラー。
 *
 * <ul>
 *   <li>ROLE_DOCTOR → /doctor/dashboard</li>
 *   <li>ROLE_ADMIN  → /admin/dashboard</li>
 *   <li>ROLE_PATIENT → /patient/dashboard</li>
 * </ul>
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * ログイン成功後に呼び出され、ユーザーのロールに応じてリダイレクトを行う。
     *
     * @param request       HTTPリクエスト
     * @param response      HTTPレスポンス
     * @param authentication 認証済みユーザー情報
     */
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {

        // ロールを取得（最初の1つのみ）
        String role = authentication.getAuthorities()
                                    .stream()
                                    .findFirst()
                                    .map(auth -> auth.getAuthority())
                                    .orElse("");

        // ロール別にリダイレクト
        switch (role) {
            case "ROLE_DOCTOR"  -> response.sendRedirect("/doctor/dashboard");
            case "ROLE_ADMIN"   -> response.sendRedirect("/admin/dashboard");
            case "ROLE_PATIENT" -> response.sendRedirect("/patient/dashboard");
            default             -> response.sendRedirect("/access-denied");
        }
    }
}
