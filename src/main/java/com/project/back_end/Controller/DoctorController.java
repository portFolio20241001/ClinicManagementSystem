package com.project.back_end.Controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Optional;	//　SpringSecurity対応により廃止

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.transaction.annotation.Transactional;		//　SpringSecurity対応により廃止
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.project.back_end.DTO.Login;			//　SpringSecurity対応により廃止
import com.project.back_end.Entity.Doctor;
import com.project.back_end.Security.C_JwtService_B;
//import com.project.back_end.Service.CommonService;	//　SpringSecurity対応により廃止
import com.project.back_end.Service.DoctorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <h2>DoctorController ― 医師関連 REST エンドポイント</h2>
 * <pre>
 * ・登録／更新／削除
 * ・ログイン
 * ・空き時間確認
 * ・名前・専門・時間帯でのフィルタ
 * ※ すべて JSON を返却
 * </pre>
 */
@RestController
@RequestMapping("${api.path}doctor")          // 例: /api/doctor
@RequiredArgsConstructor                      // Lombok: 依存性はコンストラクタ注入
@Slf4j
@Valid
public class DoctorController {

    /** 医師固有ロジックを扱うサービス */
    private final DoctorService doctorService;
    
    private final C_JwtService_B jwtService;

    /** トークン検証や汎用フィルタを行う共通サービス */
//    private final CommonService commonService;		//　SpringSecurity対応により廃止

    /** API パスの前置き文字列（ログ用） */
    @Value("${api.path}")
    private String apiPrefix;

    /* =====================================================================
     * 1) 医師の空き時間を取得
     * ===================================================================*/
    // SwaggerUI表示
    @Operation(
            summary = "医師の空き時間を取得 (by Patient or Doctor)",
            description = "指定した <b>doctorId</b> と <b>date</b> について、未予約の時間帯リストを返します。",
            parameters = {
                @Parameter(
                    name = "doctorId",
                    description = "医師ID",
                    example = "2"
                ),
                @Parameter(
                    name = "date",
                    description = "対象日 (YYYY-MM-DD)",
                    example = "2025-09-11"
                ),
    	        @Parameter(
        	            name = "Authorization",
        	            in = ParameterIn.HEADER,
        	            description = "Bearer トークン（例: Bearer eyJhbGciOi...）",
        	            required = true,
        	            example = "Bearer eyJhbGciOiJIUzI1NiJ9.patientTokenSig"
        	        )
            },
            responses = {
                @ApiResponse(
                    responseCode = "200", description = "取得成功",
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        examples = @ExampleObject(value = """
                            {
                              "availableTimes": [
                                "09:00-10:00",
                                "11:00-12:00",
                                "15:00-16:00"
                              ]
                            }""")
                    )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "認証失敗。JWTトークンが無効、または期限切れです。",
                        content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"error\": 401, \"message\": \"認証に失敗しました。トークンが無効または期限切れです。\" }")
                        )
                    )
            }
        )
    /**
     * 指定日の医師の空き時間を取得する。
     *
     * @param doctorId 医師 ID
     * @param date     対象日 (yyyy-MM-dd)
     * @param token    認証トークン（patient ロール想定）
     * @return 空き時間リスト or エラー
     */
    
//    @GetMapping("/availability/{doctorId}/{date}/{token}")　//　SpringSecurity対応により廃止
    @GetMapping("/availability/{doctorId}/{date}")					//　SpringSecurity対応により追加
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")									//　SpringSecurity対応により追加
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//            @PathVariable String token) {	//　SpringSecurity対応により廃止
    	
    	
    	System.out.println("@GetMapping(\"/availability/{doctorId}/{date}/\")開始");
    	
    	//　SpringSecurity対応により廃止
//        /* ---- ① トークン検証（patient）---- */
//    	Optional<String> hasError = commonService.validateToken(token, "patient");  
//
//        System.out.println("ポイント1");
//        
//        if (hasError.isPresent()) {
//            // 認証エラーをそのまま返す
//        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", hasError.get()));
//        }
//        
//        System.out.println("① トークン検証（patient）通過");

        /* ---- ② 空き時間取得 ---- */
        Map<String, Object> body = new HashMap<>();
        
        
//        body.put("message", "トークンは有効です。");	//　SpringSecurity対応により廃止
        
        body.put("availableTimes", doctorService.getDoctorAvailability(doctorId, date));
        
        return ResponseEntity.ok(body);
    }

    /* =====================================================================
     * 2) すべての医師を取得
     * ===================================================================*/
    @Operation(
    	    summary = "全ての医師を取得( by Patient )",
    	    description = "登録されている医師レコードをすべて返します。",
    	    parameters = {
    	            @Parameter(
    	                name = "Authorization",
    	                in = ParameterIn.HEADER,
    	    	        required = true,
    	                description = "Bearer トークン（例: Bearer eyJhbGciOi...）",
     	                example = "Bearer eyJhbGciOiJIUzI1NiJ9.doctorTokenSig"
        	            )
        	        },
    	    responses = {
    	        @ApiResponse(
    	            responseCode = "200",
    	            description = "医師情報の取得成功",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(value = """
    	                    {
    	                      "doctors": [
    	                        {
    	                          "id": 2,
    	                          "specialty": "心臓内科",
    	                          "phone": "080-1111-0002",
    	                          "clinicLocation": {
    	                            "id": 1,
    	                            "name": "中央クリニック"
    	                          }
    	                        },
    	                        {
    	                          "id": 3,
    	                          "specialty": "神経内科",
    	                          "phone": "080-1111-0003",
    	                          "clinicLocation": {
    	                            "id": 1,
    	                            "name": "中央クリニック"
    	                          }
    	                        }
    	                      ]
    	                    }""")
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "401",
    	            description = "認証失敗。JWTトークンが無効、または期限切れです。",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(value = """
    	                    {
    	                      "error": 401,
    	                      "message": "認証に失敗しました。トークンが無効または期限切れです。"
    	                    }""")
    	            )
    	        )
    	    }
    	)


    @GetMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<String, Object>> getDoctors() {
    	
    	log.info("★ GET /doctor  全医師取得リクエスト受信");
    	
        // Service から ResponseEntity を直接受け取り、そのまま応答
        return doctorService.getDoctors();
        
    }

    /* =====================================================================
     * 3) 医師登録（Admin）
     * ===================================================================*/
    @Operation(
    	    summary = "医師登録（by Admin）",
    	    description = "管理者（Admin）が新しい医師を登録します。JWTトークン認証が必要です。",
    	    parameters = {
    	        @Parameter(
    	            name = "Authorization",
    	            in = ParameterIn.HEADER,
    	            required = true,
    	            description = "Bearer トークン形式のJWT（例：Bearer eyJhbGciOi...）",
    	            example = "Bearer eyJhbGciOiJIUzI1NiJ9.adminTokenSignature"
    	        )
    	    },
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        required = true,
    	        description = "登録する医師情報（User、ClinicLocation、availableTimes 含む）",
    	        content = @Content(
    	            schema = @Schema(implementation = Doctor.class),
    	            examples = @ExampleObject(
    	                name = "doctorRequest",
    	                value = """
    	                {
    	                  "specialty": "小児科",
    	                  "phone": "090-1234-5678",
    	                  "clinicLocation": { "id": 1 },
    	                  "availableTimes": [
    	                    "2025-06-20 09:00-10:00",
    	                    "2025-06-21 14:00-15:00"
    	                  ],
    	                  "user": {
    	                    "username": "doctorUser11",
    	                    "passwordHash": "docpass11",
    	                    "role": "ROLE_DOCTOR",
    	                    "fullName": "佐藤 太郎"
    	                  }
    	                }"""
    	            )
    	        )
    	    ),
    	    responses = {
    	        @ApiResponse(
    	            responseCode = "201",
    	            description = "登録成功",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(
    	                    name = "登録成功",
    	                    value = """
    	                    {
    	                      "message": "医師を登録しました。"
    	                    }"""
    	                )
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "409",
    	            description = "重複ユーザーあり（username が既に存在）",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(
    	                    name = "重複ユーザー",
    	                    value = """
    	                    {
    	                      "error": "同じユーザー名の医師が既に存在します。"
    	                    }"""
    	                )
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "401",
    	            description = "認証失敗。JWTトークンが無効、または期限切れです。",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(
    	                    name = "トークンエラー",
    	                    value = """
    	                    {
    	                      "error": 401,
    	                      "message": "認証に失敗しました。トークンが無効または期限切れです。"
    	                    }"""
    	                )
    	            )
    	        )
    	    }
    	)


//    @PostMapping("/{token}")　// SpringSecurity対応により廃止
    @PostMapping	  									// SpringSecurity対応により追加
    @PreAuthorize("hasRole('ADMIN')")		// SpringSecurity対応により追加
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody @Valid Doctor doctor) {
//            @PathVariable String token) {	// SpringSecurity対応により廃止

    	// SpringSecurity対応により廃止
//        /* ---- トークン検証（admin）---- 
//         * 
//         * commonService.validateToken(token, "admin") で JWT を解析し、「有効な admin トークンか」をチェック。
//		 * 戻り値は ResponseEntity<Map<String,String>> で、
//		 * 成功時 ── body: 空 or null  •  status: 200 OK
//		 * 失敗時 ── body: {"error": "...メッセージ"} • status: 401 など
//         * 
//         * */
//    	Optional<String> hasError = commonService.validateToken(token, "admin");  
//
//        System.out.println("ポイント1");
//        
//        if (hasError.isPresent()) {
//            // 認証エラーをそのまま返す
//        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", hasError.get()));
//        }

        /* ---- 登録処理 ---- */
        Map<String, String> body = new HashMap<>();
        
        int result = doctorService.saveDoctor(doctor);

        if (result == -1) {
            body.put("error", "同じユーザー名の医師が既に存在します。");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        } else if (result == 0) {
            body.put("error", "内部エラーが発生しました。");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

        body.put("message", "医師を登録しました。");
        
        
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

 // SpringSecurity対応により廃止
//    /* =====================================================================
//     * 4) 医師ログイン
//     * ===================================================================*/
//    @Operation(
//    	    summary = "医師ログイン",
//    	    description = "ユーザー名とパスワードでログインし、JWT トークンを取得します。",
//    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//    	        required = true,
//    	        content = @Content(
//    	            schema = @Schema(implementation = Login.class),
//    	            examples = @ExampleObject(
//    	                name = "ログイン例",
//    	                value = """
//    	                    {
//    	                      "username": "doctorUser1",
//    	                      "password": "password123"
//    	                    }"""
//    	            )
//    	        )
//    	    ),
//    	    responses = {
//    	        @ApiResponse(
//    	            responseCode = "200",
//    	            description = "ログイン成功",
//    	            content = @Content(
//    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
//    	                examples = @ExampleObject(
//    	                    name = "成功例",
//    	                    value = """
//    	                        {
//    	                          "token": "eyJhbGciOiJIUzI1NiJ9.generatedToken",
//    	                          "message": "ログインに成功しました。"
//    	                        }"""
//    	                )
//    	            )
//    	        ),
//    	        @ApiResponse(
//    	        	    responseCode = "401",
//    	        	    description = "認証失敗（ユーザー名不一致 または パスワード不一致）",
//    	        	    content = @Content(
//    	        	        mediaType = MediaType.APPLICATION_JSON_VALUE,
//    	        	        examples = {
//    	        	            @ExampleObject(
//    	        	                name = "ユーザー名不一致",
//    	        	                value = """
//    	        	                    {
//    	        	                      "error": "ユーザー名が存在しません。"
//    	        	                    }"""
//    	        	            ),
//    	        	            @ExampleObject(
//    	        	                name = "パスワード不一致",
//    	        	                value = """
//    	        	                    {
//    	        	                      "error": "パスワードが一致しません。"
//    	        	                    }"""
//    	        	            )
//    	        	        }
//    	        	    )
//    	        	)
//    	        }
//    	)
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> doctorLogin(
//            @RequestBody @Valid Login login) {
//
//        log.info("★ 医師ログイン要求: username={}", login.getUsername());
//        
//        return doctorService.doctorLogin(login);
//    }

    /* =====================================================================
     * 5) 医師情報更新（Adminロールで更新）
     * ===================================================================*/
    @Operation(
    	    summary = "医師情報更新（by Admin）",
    	    description = "管理者（Admin）が医師情報（電話番号・専門分野・所属クリニックなど）を更新します。JWT 認証が必要です。",
    	    parameters = {
    	        @Parameter(
    	            name = "Authorization",
    	            in = ParameterIn.HEADER,
    	            required = true,
    	            description = "Bearer トークン形式のJWT（例：Bearer eyJhbGciOi...）",
    	            example = "Bearer eyJhbGciOiJIUzI1NiJ9.adminTokenSig"
    	        )
    	    },
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        required = true,
    	        content = @Content(
    	            mediaType = MediaType.APPLICATION_JSON_VALUE,
    	            schema = @Schema(implementation = Doctor.class),
    	            examples = @ExampleObject(
    	                name = "更新例",
    	                summary = "医師情報更新（id:28）",
    	                description = "ID 28 の医師（佐藤 花子）の情報を更新する例",
    	                value = """
    	                {
    	                  "id": 28,
    	                  "specialty": "産婦人科",
    	                  "phone": "090-1234-5678",
    	                  "clinicLocation": { "id": 1 },
    	                  "availableTimes": [
    	                    "2025-06-20 09:00-10:00",
    	                    "2025-06-21 14:00-15:00"
    	                  ],
    	                  "user": {
    	                    "username": "doctorUser13",
    	                    "passwordHash": "docpass13",
    	                    "role": "ROLE_DOCTOR",
    	                    "fullName": "佐藤 花子"
    	                  }
    	                }"""
    	            )
    	        )
    	    ),
    	    responses = {
    	        @ApiResponse(
    	            responseCode = "200",
    	            description = "更新成功",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(
    	                    name = "成功レスポンス",
    	                    value = "{ \"message\": \"医師情報を更新しました。\" }"
    	                )
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "404",
    	            description = "該当 ID が存在しない",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(
    	                    name = "存在しないID",
    	                    value = "{ \"error\": \"指定 ID の医師は存在しません。\" }"
    	                )
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "500",
    	            description = "更新リクエストの異常（ID未指定など）",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(
    	                    name = "ID未指定エラー",
    	                    value = "{ \"error\": \"Doctor IDを更新するならIDがNULLはダメです。\" }"
    	                )
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "401",
    	            description = "認証失敗。JWTトークンが無効または期限切れです。",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(
    	                    name = "認証エラー",
    	                    value = """
    	                    {
    	                      "error": 401,
    	                      "message": "認証に失敗しました。トークンが無効または期限切れです。"
    	                    }"""
    	                )
    	            )
    	        )
    	    }
    	)


//    @PutMapping("/{token}")	// SpringSecurity対応により廃止
    @PutMapping											// SpringSecurity対応により追加
    @PreAuthorize("hasRole('ADMIN')")			// SpringSecurity対応により追加
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody @Valid Doctor doctor) {
//            @PathVariable String token) {				// SpringSecurity対応により廃止

//    	Optional<String> hasError = commonService.validateToken(token, "admin");  	// SpringSecurity対応により廃止

    	System.out.println("username: " + doctor.getUser().getUsername());
    	System.out.println("password: " + doctor.getUser().getPasswordHash());
    	System.out.println("fullname: " + doctor.getUser().getFullName());
    	System.out.println("role: " + doctor.getUser().getRole());
        
    	// SpringSecurity対応により廃止
//        if (hasError.isPresent()) {
//            // 認証エラーをそのまま返す
//        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", hasError.get()));
//        }
        
        return doctorService.updateDoctor(doctor);
    }

    /* =====================================================================
     * 6) 医師削除（Admin）
     * ===================================================================*/
    @Operation(
    	    summary = "医師削除（by Admin）",
    	    description = "Admin が指定 ID の医師（および関連する予約履歴など）を削除します。",
    	    parameters = {
    	        @Parameter(
    	            name = "doctorId",
    	            in = ParameterIn.PATH,
    	            required = true,
    	            description = "削除対象の医師 ID",
    	            example = "11"
    	        ),
    	        @Parameter(
    	            name = "Authorization",
    	            in = ParameterIn.HEADER,
    	            required = true,
    	            description = "Bearer トークン形式のJWT（例：Bearer eyJhbGciOi...）",
    	            example = "Bearer eyJhbGciOiJIUzI1NiJ9.adminTokenSig"
    	        )
    	    },
    	    responses = {
    	        @ApiResponse(
    	            responseCode = "200",
    	            description = "削除成功",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(
    	                    name = "削除成功",
    	                    value = """
    	                    {
    	                      "message": "医師(および紐づく予定履歴)を削除しました。"
    	                    }"""
    	                )
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "404",
    	            description = "該当IDの医師が存在しない",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(
    	                    name = "医師IDなし",
    	                    value = """
    	                    {
    	                      "error": "指定 ID の医師は存在しません。"
    	                    }"""
    	                )
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "401",
    	            description = "認証失敗。JWTトークンが無効または期限切れです。",
    	            content = @Content(
    	                mediaType = MediaType.APPLICATION_JSON_VALUE,
    	                examples = @ExampleObject(
    	                    name = "認証失敗",
    	                    value = """
    	                    {
    	                      "error": 401,
    	                      "message": "認証に失敗しました。トークンが無効または期限切れです。"
    	                    }"""
    	                )
    	            )
    	        )
    	    }
    	)


//    @DeleteMapping("/{doctorId}/{token}")	// SpringSecurity対応により廃止
    @DeleteMapping("/{doctorId}")				//	 SpringSecurity対応により追加
    @PreAuthorize("hasRole('ADMIN')")			// 	SpringSecurity対応により追加
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long doctorId) {
//            @PathVariable String token) {	// SpringSecurity対応により廃止
    	// SpringSecurity対応により廃止
//    	Optional<String> hasError = commonService.validateToken(token, "admin");  
//
//        System.out.println("ポイント1");
//        
//        if (hasError.isPresent()) {
//            // 認証エラーをそのまま返す
//        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", hasError.get()));
//        }
        
        return doctorService.deleteDoctor(doctorId);
    }

    
    /* =====================================================================
     * 7) 医師フィルタ（名前・専門・AM/PM）
     * ===================================================================*/
    @Operation(
        summary = "医師フィルタ( by Patient )",
        description = """
            3 つのパラメータを組み合わせて医師を検索します。  
            <ul>
              <li><code>name</code>      … 医師名（部分一致）例: <i>鈴木</i></li>
              <li><code>specialty</code> … 専門分野           例: <i>心臓内科</i></li>
              <li><code>period</code>    … 午前/午後           例: <i>AM</i> または <i>PM</i></li>
            </ul>
            各値が <code>null</code>（空文字）の場合、その条件は無視されます。
            """,
        parameters = {
            @Parameter(
                name        = "name",
                description = "医師名（部分一致）",
                example     = "鈴木"
            ),
            @Parameter(
                name        = "specialty",
                description = "専門分野（完全一致）",
                example     = "心臓内科"
            ),
            @Parameter(
                name        = "period",
                description = "時間帯フィルタ: AM = 0:00-11:59, PM = 12:00-23:59",
                example     = "AM"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description  = "条件にマッチした医師一覧",
                content      = @Content(
                    mediaType = "application/json",
                    examples  = @ExampleObject(
                        name    = "取得例",
                        summary = "AM・心臓内科・鈴木 で検索",
                        value   = """
                        {
                          "doctors": [
                            {
                              "id": 2,
                              "user": {
                                "username": "doctorUser1",
                                "fullName": "鈴木 花子"
                              },
                              "phone": "080-1111-0002",
                              "specialty": "心臓内科",
                              "clinic": {
                                "id": 1,
                                "name": "中央クリニック"
                              },
                              "availableTimes": [
                                "09:00-10:00",
                                "10:00-11:00",
                                "11:00-12:00"
                              ]
                            }
                          ]
                        }
                        """
                    )
                )
            )
        }
    )
    @GetMapping("/filter/{name}/{specialty}/{period}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String specialty,
            @PathVariable String period) {

        Map<String, Object> body = new HashMap<>();
        
        log.info("★ /filter/{name}/{specialty}/{period}開始");

        System.out.println("name:" + name );
        System.out.println("specialty:" + specialty );
        System.out.println("period:" + period );
        
        
        List<Doctor>doctorFilterList =  doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, period);
        
        System.out.println("【Contoroller】doctorFilterList:"+ doctorFilterList);
        
        body.put("doctors",doctorFilterList );
        
        return ResponseEntity.ok(body);
    }
    
    
    
    
    
    /* =====================================================================
     * 
     * 医師の利用可能時間を取得・追加・更新・削除
     * 
     * ===================================================================*/
    


    /**
     * 医師の診療可能時間（availableTimes）を操作するコントローラー。
     * <p>対象はログイン中のDoctor自身。</p>
     *
     * 提供機能:
     * <ul>
     *   <li>利用可能時間の一覧取得</li>
     *   <li>新規追加</li>
     *   <li>時間の更新（古い時間 → 新しい時間）</li>
     *   <li>時間の削除</li>
     * </ul>
     */


    @Operation(
    	    summary = "利用可能時間一覧取得( by Doctor )",
    	    description = "ログイン中の医師の利用可能時間を取得します。",
    	    parameters = {
    	        @Parameter(
    	            name = "Authorization",
    	            description = "Bearerトークン（例: Bearer eyJhbGciOi...）",
    	            in = ParameterIn.HEADER,
    	            required = true,
    	            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    	        )
    	    }
    	)
    	@ApiResponses(value = {
    	    @ApiResponse(
    	        responseCode = "200",
    	        description = "利用可能時間リスト",
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = @ExampleObject(value = "{\n  \"availableTimes\": [\"2025-06-30 09:00\", \"2025-06-30 10:00\"]\n}")
    	        )
    	    ),
    	    @ApiResponse(
    	        responseCode = "400",
    	        description = "入力値エラーや認証失敗など",
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = @ExampleObject(value = "{\n  \"error\": \"医師IDが不正です。\"\n}")
    	        )
    	    ),
    	    @ApiResponse(
    	        responseCode = "500",
    	        description = "サーバーエラー",
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = @ExampleObject(value = "{\n  \"error\": \"サーバーエラー: 予期しないエラーが発生しました。\"\n}")
    	        )
    	    )
    	})
    	@GetMapping("/available-times")
    	@PreAuthorize("hasRole('DOCTOR')")
    	public ResponseEntity<?> getAvailableTimes(HttpServletRequest request) {
    	
    	    try {
    	    	
    	    	// JWTからusername抽出
    	        String username = jwtService.extractUsernameFromRequest(request);
    	        
    	        System.out.println("username:"+username);
    	        
    	        List<String> times = doctorService.getAvailableTimes(username);
    	        
    	        return ResponseEntity.ok(Map.of("availableTimes", times));
    	        
    	    } catch (IllegalArgumentException e) {
    	    	
    	        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    	        
    	    } catch (Exception e) {
    	    	
    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    	                .body(Map.of("error", "サーバーエラー: " + e.getMessage()));
    	        
    	    }
    	}


    
    @Operation(
    	    summary = "利用可能時間を追加（by Doctor）",
    	    description = "ログイン中の医師に診療可能時間を追加します。",
    	    parameters = {
    	  		       @Parameter(
    	    		      name = "Authorization",
    	    		      description = "Bearerトークン（例: Bearer eyJhbGciOi...）",
    	    		      in = ParameterIn.HEADER,
    	    		      required = true,
    	    		      example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    	    		   )
    	    },
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        required = true,
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = @ExampleObject(
    	                name = "Add Time Example",
    	                value = "{\n  \"time\": \"2025-06-30 09:00\"\n}"
    	            )
    	        )
    	    )
    	)
    	@ApiResponses(value = {
    	    @ApiResponse(
    	        responseCode = "201",
    	        description = "利用可能時間を追加しました",
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = @ExampleObject(
    	                value = "{\n  \"message\": \"利用可能時間を追加しました。\"\n}"
    	            )
    	        )
    	    ),
    	    @ApiResponse(
    	    	    responseCode = "400",
    	    	    description = "バリデーションエラー（時間が不正・医師が存在しない・すでに存在する時間の追加）",
    	    	    content = @Content(
    	    	        mediaType = "application/json",
    	    	        examples = {
    	    	            @ExampleObject(
    	    	                name = "Doctor Not Found",
    	    	                value = "{\n  \"error\": \"該当する医師が見つかりません。\"\n}"
    	    	            ),
    	    	            @ExampleObject(
    	    	                name = "Time Already Exists",
    	    	                value = "{\n  \"error\": \"指定された時間は既に存在します。\"\n}"
    	    	            )
    	    	        }
    	    	    )
    	    	),
    	    @ApiResponse(
    	        responseCode = "500",
    	        description = "サーバーエラー",
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = @ExampleObject(
    	                value = "{\n  \"error\": \" 予期しない例外が発生しました\"\n}"
    	            )
    	        )
    	    )
    	})
    /**
     * 新しい診療可能時間を追加します。
     *
     * @param body    追加する時間（例: "2025-06-30 09:00"）
     * @param request JWTからDoctor IDを取得
     * @return 成功 or エラー
     */
    @PostMapping("/available-times")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> addAvailableTime(@RequestBody Map<String, String> body, HttpServletRequest request) {
    	
        try {
        	
	    	// JWTからusername抽出
	        String username = jwtService.extractUsernameFromRequest(request);
            
            doctorService.addAvailableTime(username, body.get("time"));
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "利用可能時間を追加しました。"));
            
        } catch (IllegalArgumentException e) {
        	
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            
        } catch (Exception e) {
        	
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "サーバーエラー: " + e.getMessage()));
            
        }
    }

    
    
    
    @Operation(
    	    summary = "利用可能時間の更新（by Doctor）",
    	    description = "ログイン中の医師の診療可能時間を更新します。",
    	    parameters = {
    	            @Parameter(
    	   	           name = "Authorization",
    	   	           description = "Bearerトークン（例: Bearer eyJhbGciOi...）",
    	   	           in = ParameterIn.HEADER,
    	               required = true,
     	               example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
       	            )
   	        },
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        required = true,
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = @ExampleObject(
    	                value = "{\n  \"oldTime\": \"2025-06-30 09:00\",\n  \"newTime\": \"2025-06-30 10:00\"\n}"
    	            )
    	        )
    	    ),
    	    responses = {
    	        @ApiResponse(
    	            responseCode = "200",
    	            description = "利用可能時間を更新しました。",
    	            content = @Content(
    	                mediaType = "application/json",
    	                examples = @ExampleObject(
    	                    value = "{\n  \"message\": \"利用可能時間を更新しました。\"\n}"
    	                )
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "400",
    	            description = "バリデーションエラー（更新元が存在しない・予約済みなど）",
    	            content = @Content(
    	                mediaType = "application/json",
    	                examples = @ExampleObject(
    	                    value = "{\n  \"error\": \"指定された更新元の時間は存在しません。\"\n}"
    	                )
    	            )
    	        ),
    	        @ApiResponse(
    	            responseCode = "500",
    	            description = "サーバーエラー",
    	            content = @Content(
    	                mediaType = "application/json",
    	                examples = @ExampleObject(
    	                    value = "{\n  \"error\": \"　この時間には既に患者予約があります。変更できません。仕事してください。\"\n}"
    	                )
    	            )
    	        )
    	    }
    	)
    /**
     * 既存の時間を新しい時間に更新します。
     *
     * @param body    oldTime と newTime を含むMap
     * @param request JWTからDoctor IDを取得
     * @return 成功 or エラー
     */
    @PutMapping("/available-times")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> updateAvailableTime(@RequestBody Map<String, String> body, HttpServletRequest request) {
    	
        try {
        	
	    	// JWTからusername抽出
	        String username = jwtService.extractUsernameFromRequest(request);
            
            doctorService.updateAvailableTime(username, body.get("oldTime"), body.get("newTime"));
            
            return ResponseEntity.ok(Map.of("message", "利用可能時間を更新しました。"));
            
        } catch (IllegalArgumentException e) {
        	
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            
        } catch (Exception e) {
        	
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "サーバーエラー: " + e.getMessage()));
            
        }
    }

    
    
    @Operation(
    	    summary = "利用可能時間の削除( by Doctor )",
    	    description = "ログイン中の医師の利用可能時間を削除します。予約が入っている時間は削除できません。",
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        required = true,
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = @ExampleObject(
    	                value = "{\n  \"time\": \"2025-06-30 09:00\" \n}"
    	            )
    	        )
    	    ),
    	    parameters = {
    	        @Parameter(
    	            name = "Authorization",
    	            in = ParameterIn.HEADER,
    	            required = true,
    	            description = "JWTトークン（例: Bearer xxx.yyy.zzz）",
    	            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    	        )
    	    }
    	)
    	@ApiResponses(value = {
    	    @ApiResponse(
    	        responseCode = "200",
    	        description = "削除成功",
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = @ExampleObject(
    	                value = "{\n  \"message\": \"利用可能時間を削除しました。\" \n}"
    	            )
    	        )
    	    ),
    	    @ApiResponse(
    	    	    responseCode = "400",
    	    	    description = "バリデーションエラー（時間が存在しない・形式不正など）",
    	    	    content = @Content(
    	    	        mediaType = "application/json",
    	    	        examples = {
    	    	            @ExampleObject(
    	    	                name = "時間が存在しない",
    	    	                value = "{\n  \"error\": \"指定された時間は存在しません。\" \n}"
    	    	            ),
    	    	            @ExampleObject(
    	    	                name = "時間形式が不正",
    	    	                value = "{\n  \"error\": \"時間形式が不正です。正しい形式（例: 2025-06-30 09:00）で指定してください。\" \n}"
    	    	            )
    	    	        }
    	    	    )
    	    	),
    	    @ApiResponse(
    	        responseCode = "500",
    	        description = "サーバーエラー",
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = @ExampleObject(
    	                value = "{\n  \"error\": \"この時間には既に患者予約があります。削除できません。仕事してください。\"\n}"
    	            )
    	        )
    	    )
    	})
    /**
     * 指定の利用可能時間を削除します。
     *
     * @param body    time（例: "2025-06-30 09:00"）を含むMap
     * @param request JWTからDoctor IDを取得
     * @return 成功 or エラー
     */
    @DeleteMapping("/available-times")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> deleteAvailableTime(@RequestBody Map<String, String> body, HttpServletRequest request) {
    	
        try {
        	
	    	// JWTからusername抽出
	        String username = jwtService.extractUsernameFromRequest(request);
            
            doctorService.deleteAvailableTime(username, body.get("time"));
            
            return ResponseEntity.ok(Map.of("message", "利用可能時間を削除しました。"));
            
        } catch (IllegalArgumentException e) {
        	
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            
        } catch (Exception e) {
        	
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "サーバーエラー: " + e.getMessage()));
            
        }
        
    }
    

    
    
    
    
}
