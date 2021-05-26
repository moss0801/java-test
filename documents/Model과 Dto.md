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

* 하나의 Model을 Dto로 변환하는 함수
* Type 또는 ProperyName이 동일하지 않은 경우를 위한 Modifier를 추가 가능
* Model List도 Dto로 변환할 수 있어야 하고, Modifier도 지원해야 한다.  
* Dto를 Model 변환하는 함수
* 추가인 경우 Id를 설정한 Model에 Dto의 속성들을 Mapping 할 수 있어야 한다.
* Dto 속성을 Model로 Mapping시에도 Modifier를 사용할 수 있어야 한다.

## Example
```
    /**
     * 책 추가
     * @param command 책 추가 Command
     */
    @Transactional
    public void add(AddBookCommand command) {
        BookId id = repository.newIdentity();
        var book = Book.builder().id(id).build();
        DtoAssembler.map(command, book, (dto, model) -> {
            model.setCategoryId(CategoryId.of(dto.getCategoryId()));
            return model;
        });

        repository.save(book);
    }

    /**
     * 책 조회
     * @param id 책 id
     * @return 책 Dto
     */
    @Transactional(readOnly = true)
    public BookDto get(String id) {
        BookDto result;
        var bookOptional = repository.findById(BookId.of(id));
        if (bookOptional.isEmpty()) {
            return null;
        }
        return DtoAssembler.to(bookOptional.get(), BookDto.class);
    }

    /**
     * 책 목록 조회
     * @return 책 Dto 목록
     */
    @Transactional(readOnly = true)
    public List<BookDto> list() {
        return DtoAssembler.to(repository.findAll(), BookDto.class);
    }
```