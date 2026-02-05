# Wemade(전기아이피) 과제

## 📊 Log Analyzer
전기아이피 웹 앱 백엔드 개발 과제입니다.

## 🚀 실행 방법

### 1. 빌드 및 실행
Java 17 이상이 설치되어 있어야 합니다.

**Mac / Linux**
```bash
./gradlew clean build

java -jar module-api/build/libs/module-api-0.0.1-SNAPSHOT.jar
```

**Windows**
```DOS
gradlew.bat clean build

java -jar module-api/build/libs/module-api-0.0.1-SNAPSHOT.jar
```

### 2. API 테스트
서버 실행 후 Swagger UI를 통해 API를 직접 테스트할 수 있습니다.

Swagger UI: http://localhost:8080/swagger-ui/index.html

### 3. 가장 중요하다고 판단한 기능
대용량 로그 파일을 메모리 효율적인 스트리밍 처리하는 것이 가장 중요하다 생각했습니다.

따라서 `InputStream`과 `BufferedReader`를 활용하여 파일을 한 줄씩 읽고 처리한 뒤 메모리에서 즉시 해제하는 스트리밍 방식을 적용했습니다.


#### 특히 신경쓴 부분
1. 외부 API 조회 성능 최적화 - Caffeine Cache

로그 데이터 특성상 동일한 IP가 반복해서 등장할 확률이 높을 것이라 판단하였습니다. 매번 외부 API(ipinfo.io)를 호출하면 네트워크 지연(Latency)과 비용(Rate Limit) 문제가 발생합니다.

이를 해결하기 위해 `로컬 캐시(Caffeine)`를 도입하여, 한 번 조회된 IP 정보는 메모리에 저장하고 재사용함으로써 분석 속도를 대폭 개선했습니다.

2. 유연하고 확장 가능한 아키텍처

비즈니스 로직(Core)이 구체적인 기술(Infrastructure, API)에 의존하지 않도록 멀티 모듈 및 포트-어댑터 패턴을 적용했습니다.

### 4. 실 서비스로 운영한다면 중점 개선하거나 보완할 포인트

1. Message Queue 기반 처리

현재: 사용자가 파일 업로드 시 분석이 끝날 때까지 기다려야 하는 동기 방식입니다. 파일이 매우 크다면 Timeout이 발생할 수 있습니다.

개선: 업로드 요청을 받으면 Kafka나 RabbitMQ 같은 메시지 큐에 작업을 적재하고 바로 응답(202 Accepted)을 준 뒤, 백그라운드 워커(Worker)가 비동기로 로그를 분석하도록 개선하여 사용자 경험을 높일 수 있을 것입니다.

2. 영속성 저장소 및 분산 캐시 도입

현재: 서버 재시작 시 메모리에 저장된 분석 결과와 캐시가 모두 초기화됩니다.

개선: 분석 결과는 RDB 또는 NoSQL에 영구 저장하고, 캐시는 Redis 같은 글로벌 캐시로 전환하여 서버가 여러 대로 Scale-out되어도 데이터 정합성을 유지하도록 보완할 수 있습니다.


### 비고
`application.yml`을 github에 올렸고, API 키를 같이 올렸습니다.
보안상 원래는 이 파일을 올리지 않도록 `.gitignore` 파일에 yml파일을 추가해야하지만
별도의 설정 없이 바로 실행하실 수 있도록 해당 파일을 키를 포함하여 올렸음을 알려드립니다.