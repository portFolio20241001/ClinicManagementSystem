package com.project.back_end.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security 全体の設定クラス。
 *
 * <ul>
 *   <li>フォームログインのカスタム画面指定</li>
 *   <li>JWTによるトークン認証フィルターの挿入</li>
 *   <li>ロールごとのアクセス制御</li>
 *   <li>パスワードハッシュや認証マネージャーの定義</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity // @PreAuthorize などを有効化
@RequiredArgsConstructor
public class A_SecurityConfig {

    private final B_JwtAuthFilter_A jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * セキュリティフィルターの定義と、認可ルールを構成。
     *
     * @param http HttpSecurity のビルダー
     * @return SecurityFilterChain（Spring によるセキュリティフィルター定義）
     * @throws Exception 構成エラー時
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // === ログイン画面・静的リソース・APIログインは全員アクセス許可 ===
                .requestMatchers("/", "/login/**", "/html/**","/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()

                // === DoctorController ===
                .requestMatchers("/doctor/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/doctor").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/doctor").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/doctor/availability/doctorId/**").hasRole("PATIENT")
                .requestMatchers(HttpMethod.GET, "/doctor/doctorDashboard/**").hasRole("DOCTOR")

                
                

                // === AppointmentController ===
                .requestMatchers(HttpMethod.POST, "/appointments").hasRole("PATIENT")
                .requestMatchers(HttpMethod.PUT, "/appointments").hasRole("PATIENT")
                .requestMatchers(HttpMethod.GET, "/appointments/*/*/*").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.GET, "/appointments/*/*").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.DELETE, "/appointments/*").hasRole("PATIENT")

                // === PrescriptionController ===
                .requestMatchers(HttpMethod.POST, "/prescription").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.GET, "/prescription/*").hasRole("DOCTOR")

                // === PatientController ===
                .requestMatchers(HttpMethod.GET, "/patient/**").hasRole("PATIENT")

                // その他のリクエストはすべて認証が必要
                .anyRequest().authenticated()
            )
            
            //以下はSessionベースのFormログイン認証の場合　今回はJWTなので不要
//            .formLogin(form -> form
//                .loginPage("/login")
//                .successHandler(successHandler)
//                .permitAll()
//            )
//            .logout(logout -> logout
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/login?logout")
//                .permitAll()
//            )
//            .sessionManagement(sess -> sess
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//            )
            
            .sessionManagement(sess -> sess
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWTでは必ず STATELESS！
                )
            
            // JwtAuthFilter を、Spring Securityの UsernamePasswordAuthenticationFilter の前に実行させる
            // Form認証であるUsernamePasswordAuthenticationFilterが先に実行されてしまうのを防ぐため
            /*
             * [JwtAuthFilter] ←★ここに追加！
				    ↓
				[UsernamePasswordAuthenticationFilter] ←通常のformログイン
				    ↓
				[その他のSecurityフィルター]
				    ↓
				[Controller呼び出し]
             * 
             */
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }


    
    /**
     * 認証マネージャー（ログイン処理などで使用）。
     *
     * @return AuthenticationManager（認証ロジックを集約）
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authProvider());
    }
    
    /**
     * Spring Security に認証処理を委ねるためのプロバイダ。
     *
     * @return DaoAuthenticationProvider（DBにあるユーザーで認証する）
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
    	
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService); // ユーザー検索方法を指定
        authProvider.setPasswordEncoder(passwordEncoder());     // パスワード比較用のエンコーダ
        
        return authProvider;
    }



    /**
     * パスワードをハッシュ化／検証するためのエンコーダ。
     *
     * @return BCryptPasswordEncoder インスタンス
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
