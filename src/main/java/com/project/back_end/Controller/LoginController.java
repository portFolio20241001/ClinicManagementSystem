package com.project.back_end.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.back_end.DTO.LoginRequest;
import com.project.back_end.DTO.LoginResponse;
import com.project.back_end.Security.C_JwtService_B;
import com.project.back_end.Security.D2_UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

/**
 * ログイン認証を担当する REST コントローラー。
 * <p>
 * ユーザー名・パスワードを受け取り、認証成功時に JWT を発行してクライアントへ返却します。
 */

//@RequestMapping("/api/auth") // ベースとなるURLパスの指定（例: /api/auth/login）

@Controller
@RequiredArgsConstructor // コンストラクタインジェクションを自動生成
public class LoginController {

    // Spring Security の認証処理を担当する AuthenticationManager をDI
    private final AuthenticationManager authenticationManager;

    // JWTトークンの発行・検証などを担当するサービスをDI
    private final C_JwtService_B jwtService;
    
    
    /**
     * ルートアクセス時にログイン画面へリダイレクト
     */
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    /**
     * ログイン画面（login.html）表示
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // → templates/login.html
    }

    /**
     * ログイン処理（JWTトークンを返すAPI）
     */
    
    @Operation(
    	    summary = "ユーザーのログイン認証を行う（JWTトークン返却　Admin　Patient　Doctor共通）",
    	    description = "ユーザー名とパスワードを受け取り、JWTトークンとユーザーロールを返却します。認証に失敗した場合は401を返します。",
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        required = true,
    	        content = @Content(
    	            mediaType = "application/json",
    	            schema = @Schema(implementation = LoginRequest.class),
    	            examples = {
    	                @ExampleObject(name = "Admin Login", value = "{\n  \"username\": \"adminUser1\",\n  \"password\": \"addpass1\"\n}"),
    	                @ExampleObject(name = "Doctor Login", value = "{\n  \"username\": \"doctorUser1\",\n  \"password\": \"docpass1\"\n}"),
    	                @ExampleObject(name = "Patient Login", value = "{\n  \"username\": \"patientUser1\",\n  \"password\": \"patpass1\"\n}")
    	            }
    	        )
    	    ),
    	    responses = {
    	        @ApiResponse(
    	            responseCode = "200",
    	            description = "ログイン成功（JWTトークンとロール返却）",
    	            content = @Content(
    	                mediaType = "application/json",
    	                schema = @Schema(implementation = LoginResponse.class),
    	                examples = {
    	                    @ExampleObject(name = "Admin Success", value = "{\n  \"token\": \"eyJhbGciOiJIUzM4NCJ9...\",\n  \"role\": \"ROLE_ADMIN\"\n}"),
    	                    @ExampleObject(name = "Doctor Success", value = "{\n  \"token\": \"eyJhbGciOiJIUzM4NCJ9...\",\n  \"role\": \"ROLE_DOCTOR\"\n}"),
    	                    @ExampleObject(name = "Patient Success", value = "{\n  \"token\": \"eyJhbGciOiJIUzM4NCJ9...\",\n  \"role\": \"ROLE_PATIENT\"\n}")
    	                }
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "401",
    	            description = "認証失敗",
    	            content = @Content(
    	                mediaType = "application/json",
    	                examples = @ExampleObject(value = "{\n  \"message\": \"認証に失敗しました。ユーザー名またはパスワードが正しくありません。\",\n  \"error\": 401\n}")
    	            )
    	        )
    	    }
    	)



    /**
     * ログインリクエストを処理し、JWT トークンを返却します。
     *
     * @param request クライアントからのログインリクエスト（ユーザー名・パスワード）
     * @return JWT トークンとユーザーのロール情報
     */
    @PostMapping("/api/auth/login") // HTTP POST /api/auth/login にマッピング
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    	
    	System.out.println("ログイン認証開始");
    	
    	System.out.println("request.getUsername()：" + request.getUsername());
    	System.out.println("request.getPassword()  : " + request.getPassword());
    	
        try {
	        // === 認証処理 ===
        	System.out.println("ユーザー認証開始");
	        // ユーザー名とパスワードを使って、Spring Security の認証処理を行う
	        Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                        request.getUsername(), // 入力されたユーザー名
	                        request.getPassword()  // 入力されたパスワード
	                )
	        );
	        
	        System.out.println("ユーザー認証OK");
	        System.out.println("userDetails取得開始");
	
	        // === 認証成功時のユーザー情報取得 ===
	        // 認証済みユーザーの情報を UserDetailsImpl 型にキャストして取得
	        D2_UserDetailsImpl userDetails = (D2_UserDetailsImpl) authentication.getPrincipal();
	        
	        System.out.println("userDetails.getUser()：" + userDetails.getUser());
	        
	        // === JWT トークンの発行 ===
	        // 認証済みユーザーに対してトークンを発行
	        System.out.println("token発行開始");
	        	        
	        String token = jwtService.generateToken(userDetails);
	        String role = userDetails.getUser().getRole().name();
	        
	        System.out.println("token発行、Role取得完了。token：" + token + "Role: " + role);
	        
	        

	        
	        Long doctorId = null;

	        if ("ROLE_DOCTOR".equals(role)) {
	            // DoctorRepository に「findByUser_Id(Long)」を用意しておくと楽
	            doctorId =userDetails.getUser().getId();
	        }
	        
	
	        // === トークンとロール情報をレスポンスとして返却 ===
	        // クライアントはこれを保持し、次回以降 Authorization ヘッダーにセットして使用する
	        return ResponseEntity.ok(
	                new LoginResponse(
	                        token,                          // 発行したJWTトークン
	                        userDetails.getUser().getRole().name() // ユーザーのロール（例：ROLE_DOCTOR）,
	                )
	        );
        } catch (AuthenticationException ex) {
        	
            // Mapを使ってJSON形式のレスポンスを返す
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("message", "認証に失敗しました。ユーザー名またはパスワードが正しくありません。");
            errorBody.put("error", 401);

            return ResponseEntity.status(401).body(errorBody);
        }
    }
}
