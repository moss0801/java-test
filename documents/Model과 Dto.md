# 개요
DDD 관점에서 도메인 모델과 Dto 간 변환 구현에 대한 정리

# DtoAssembler
Model을 Dto로 Dto를 Model로 변환해 주는 HelperClass

modelmapper를 이용해서 개발

# gradle
```
dependencies {
	implementation 'org.modelmapper:modelmapper:2.4.3'
}
```

# DtoAssembler
Model을 Dto로 Dto를 Model로 변환하기 위한 Helper 함수 모음

com.moss.javatest.shared.dto.DtoAssembler 코드 참고

