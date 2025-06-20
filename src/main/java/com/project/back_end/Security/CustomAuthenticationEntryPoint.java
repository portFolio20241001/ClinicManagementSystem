package com.project.back_end.Security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 認証エラー時に呼び出されるエントリーポイント。
 * 
 * <p>
 * 主に Spring Security の JWT 認証において、以下のようなケースで発動します：
 * </p>
 * <ul>
 *   <li>トークンが送信されていない</li>
 *   <li>トークンのフォーマットが不正</li>
 *   <li>トークンの署名検証に失敗</li>
 *   <li>トークンの有効期限が切れている</li>
 * </ul>
 *
 * <p>
 * 通常 Spring Security は HTMLのエラーページを返しますが、SPA や API アプリケーションでは
 * JSON フォーマットでのレスポンスが求められるため、本クラスで制御します。
 * </p>
 *
 * <p>例として以下の JSON が返却されます：</p>
 * <pre>
 * {
 *   "error": 401,
 *   "message": "認証に失敗しました。トークンが無効または期限切れです。"
 * }
 * </pre>
 *
 * @author 
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 認証失敗時のレスポンスを構成します。
     *
     * @param request        認証要求元のHTTPリクエスト
     * @param response       クライアントに返すHTTPレスポンス
     * @param authException  発生した認証例外（例：BadCredentialsException, InsufficientAuthenticationException など）
     * @throws IOException            入出力例外
     * @throws ServletException       サーブレット処理例外
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        // JSON 形式のレスポンスを返す
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        Map<String, Object> body = new HashMap<>();
        body.put("error", 401);
        body.put("message", "認証に失敗しました。トークンが無効または期限切れです。");

        // Jackson を使って JSON 文字列として書き出す
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}