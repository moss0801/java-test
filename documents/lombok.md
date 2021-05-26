# 개요
lombok 적용

# gradle
build.gradle
```
dependencies {
	compileOnly 'org.projectlombok:lombok:1.18.20'
	testCompile 'org.projectlombok:lombok:1.18.20'

	annotationProcessor 'org.projectlombok:lombok:1.18.20'	
}
```

# IntelliJ

1. Plugin 설치
   1. 설정
      * **Windnows**: File > Settings (Ctrl+Alt+S)
      * **MacOS**: Preferences (Cmd+,)
   2. Plugins 에서 lombok 설치
2. Enable annotation 설정
   1. 설정
      * **Windnows**: File > Settings (Ctrl+Alt+S)
      * **MacOS**: Preferences (Cmd+,)
   2. Build, Execution, Deployment > Compiler > Annotation Processings
   3. Enable annotation processing 체크 