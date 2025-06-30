#!/bin/sh
set -e

# 1) Core‑dump 활성화
ulimit -c unlimited              # 컨테이너 내부
sysctl -w kernel.core_pattern=/app/logs/core-%e-%p-%t

# 2) Spring Boot 기동 옵션
JAVA_OPTS="
  -XX:+UnlockDiagnosticVMOptions
  -XX:+DebugNonSafepoints
  -XX:+HeapDumpOnOutOfMemoryError
  -XX:HeapDumpPath=/app/logs
  -XX:ErrorFile=/app/logs/hs_err_pid%p.log
  -XX:OnOutOfMemoryError='jcmd %p Thread.print > /app/logs/oom_thread_%p.dump'
  -XX:OnError='jcmd %p Thread.print > /app/logs/error_thread_%p.dump'
  -Duser.timezone=Asia/Seoul
  -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}
"

# 3) Java 프로세스를 백그라운드로 띄우고 PID 잡기
java $JAVA_OPTS -jar /app/project.jar --jasypt.encryptor.password=${JASYPT_ENCRYPTOR_PASSWORD} &
PID=$!

# 4) SIGTERM(정상 종료·재시작) 받을 때 Thread‑dump 저장
term_handler () {
  echo "[entrypoint] caught SIGTERM – dumping threads"
  jcmd $PID Thread.print > /app/logs/term_thread_$(date +%s).dump
  kill -TERM "$PID"
}

trap term_handler TERM INT

wait $PID