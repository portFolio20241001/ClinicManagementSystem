##################################################################
# ❶ ビルド用ステージ（Maven） ――――――――――――――――――――――――――――――――――――――
##################################################################
FROM maven:3.9.7-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY pom.xml .
# 依存キャッシュ用のダミービルド
RUN mvn -B -q dependency:go-offline -Dmaven.test.skip
COPY src ./src
RUN mvn -B -DskipTests spring-boot:repackage             \
    && cp target/*-SNAPSHOT.jar app.jar                  \
    && echo "🏗️  Build done!"

##################################################################
# ❷ 実行用ステージ（スリム JRE） ―――――――――――――――――――――――――――――――――
##################################################################
FROM eclipse-temurin:21-jre

# --- 任意: プロファイル & JVM オプションを環境変数で注入 ----------------
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

WORKDIR /app
COPY --from=build /workspace/app.jar app.jar
EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
