package com.project.back_end.Security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * 認証フロー図（ざっくり）
 
		[HTTPリクエスト]
				  ↓ ヘッダーに "Authorization: Bearer xxx"
				[JwtAuthFilter]
				  ↓
					→　[jwtService でトークン解析]
						　  ↓
					→　[UserDetailsService でユーザー情報取得]
						　  ↓
					→　[SecurityContextHolder に認証情報セット]
		
 */



/**
 * JWTを用いた認証を行うセキュリティフィルター。
 * <p>
 * このフィルターはすべてのリクエストに対して1回だけ実行されます（OncePerRequestFilter）。
 * リクエストヘッダーの "Authorization" に含まれるJWTを検出し、
 * そのトークンが有効であれば、Spring Securityの認証情報として設定します。
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /** JWTの発行・検証・抽出などを行うサービス */
    private final JwtService jwtService;

    /** ユーザー情報をデータベース等から取得するサービス */
    private final UserDetailsService userDetailsService;

    /**
     * フィルターの本体処理。
     * <p>
     * Authorizationヘッダーに含まれるJWTを検出し、認証処理を実施します。
     * </p>
     *
     * @param request     クライアントからのHTTPリクエスト
     * @param response    クライアントへのHTTPレスポンス
     * @param filterChain フィルターチェーン（次の処理に進むために必要）
     * @throws ServletException フィルター処理中に発生したServletエラー
     * @throws IOException      フィルター処理中に発生したIOエラー
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Authorizationヘッダーからトークンを取得
        final String authHeader = request.getHeader("Authorization");

        // Bearerトークン形式でなければ次のフィルターへスキップ
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // トークンの文字列だけを抽出（"Bearer "の7文字を除去）
        final String jwt = authHeader.substring(7);

        // トークンからユーザー名（メールアドレスなど）を抽出
        final String username = jwtService.extractUsername(jwt);

        // 認証済みでなく、かつトークンが有効な場合のみ処理を行う
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // ユーザー情報をロード（DBなどから取得）
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // トークンの整合性と有効期限をチェック
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Spring Security に認証情報をセットするトークンを作成
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,        // 認証対象のユーザー情報
                                null,               // パスワードは不要
                                userDetails.getAuthorities() // 権限（ロール）一覧
                        );

                // Webコンテキストの詳細（IPアドレスなど）を設定
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 認証情報をセキュリティコンテキストに保存（このユーザーは認証済みとなる）
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 次のフィルター処理へ進む
        filterChain.doFilter(request, response);
    }
}