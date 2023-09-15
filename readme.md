# batch-study

스프링 배치를 공부하고 정리합니다.

## 스프링 배치를 사용하는 이유
* 상태 관리
  * 배치 작업의 진행 상태(중단, 완료 에러 발생 여부 등)를 관리하고 중단 발생 시 다시 회복할 수 있는 기능을 제공해줌 
* 에러 분석
  * 작업의 크기가 클수록 에러를 분석하는데 시간과 노력이 더 들고 에러 분석이 힘들어짐
  * 스프링은 에러 분석을 쉽게 도와줌
* 기타
  * 작업 병렬화
  * 기타 부가 기능들