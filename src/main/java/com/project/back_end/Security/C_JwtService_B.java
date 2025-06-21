package com.project.back_end.Security;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

/**
 * JWT の発行・検証・解析を担当するサービスクラス。
 */
@Service
public class C_JwtService_B {

    // application.properties から取得（例: jwt.secret=6A77394A536B73797A24432646294A404E635266556A586E3272357538782F41）
    @Value("${jwt.secret}")
    private String secretKey;

    // トークンの有効期間（例：1000 * 60 * 60 * 24 = 24時間）
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * 指定したユーザー情報からJWTトークンを生成します。
     *
     * @param userDetails Spring Security の UserDetails 実装
     * @return JWT トークン文字列
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    /**
     * カスタムクレームとユーザー情報からJWTを生成します。　　※クレームとは、JWT内に格納される情報の1つ1つのこと。
     *
     * @param extraClaims 追加クレーム
     * @param userDetails ユーザー情報
     * @return JWT トークン文字列
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();
    }


    /**
     * トークンが有効かどうかを判定します。
     *
     * @param token JWTトークン
     * @param userDetails 検証対象のユーザー
     * @return 有効なら true
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * トークンからユーザー名（サブジェクト）を抽出します。
     *
     * @param token JWTトークン
     * @return ユーザー名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }



    /**
     * トークンの有効期限が切れているかを判定します。
     *
     * @param token JWTトークン
     * @return 有効期限切れなら true
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * トークンから有効期限を取得します。
     *
     * @param token JWTトークン
     * @return 有効期限
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * トークンから任意のクレームを抽出します。　※クレームとは、JWT内に格納される情報の1つ1つのこと。
     *
     * @param token JWTトークン
     * @param claimsResolver クレーム取得関数
     * @return 任意のクレーム値
     * @param <T> クレームの型
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * トークンを解析してすべてのクレームを取得します。
     *
     * @param token JWTトークン
     * @return 全クレーム情報
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // ← parserBuilder() は消滅
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 秘密鍵をBase64から復号し、署名用のKeyオブジェクトを返します。
     *
     * @return 署名用秘密鍵
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    


    /**
     * リクエストから Authorization ヘッダーを通じて username を抽出します。
     *
     * @param request HttpServletRequest（ヘッダーに "Authorization: Bearer xxx" を含む）
     * @return userId（Long）
     */
    public String extractUsernameFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorizationヘッダーが無効です。");
        }
        String token = authHeader.substring(7);
        return extractUsername(token);
    }
    
}
