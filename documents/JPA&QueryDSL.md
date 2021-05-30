# 개요
JPA 및 QueryDSL 셋팅

# gradle
```
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

	runtimeOnly 'com.h2database:h2'

	compile("com.querydsl:querydsl-core")
	compile("com.querydsl:querydsl-jpa")

	annotationProcessor("com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jpa") // querydsl JPAAnnotationProcessor 사용 지정
	annotationProcessor("jakarta.persistence:jakarta.persistence-api") // java.lang.NoClassDefFoundError(javax.annotation.Entity) 발생 대응
	annotationProcessor("jakarta.annotation:jakarta.annotation-api") // java.lang.NoClassDefFoundError (javax.annotation.Generated) 발생 대응
}
```

Spring JPA 사용을 위한 spring-boot-starter-data-jpa 추가

JPA 구현체는 하이버네이트

테스트를 위한 것이기에 h2 DB 사용

Intellij 2020.1 이후 QueryDSL Q 클래스 생성을 위한 annotationProcessor 추가


# application.yml
```
spring:
  h2:
    console:
      enabled: true
  jpa:
    generate-ddl: true
    open-in-view: false
    show-sql: true
    properties:
      hibernate.dialect: org.hibernate.dialect.MySQL5InnoDBDialect
      hibernate.dialect.storage_engine: innodb

  datasource:
    hikari:
      jdbc-url: jdbc:h2:mem://localhost/~/testdb;MODE=MYSQL
```

# application-k8s.yml
```
spring:
  jpa:
    generate-ddl: false
    show-sql: false
```

# Entitiy & ValueObject

## ValueObjectIdEntity
Id를 ValueObject로 사용하는 Entity의  경우 ValueObjectIdEntity를 상속받는다.

spring-data-jpa는 save시 Persistable.isNew() 값이 true인 경우 바로 INSERT 문을 수행하나,
false인 경우 ID로 존재여부를 확인 후 INSERT 를 수행하기에 성능 저하를 방지하기 위함이다.

# SharedQuerydslRepositorySupport
QueryDSL 코드 작성간 필요한 코드 모음

* List<Expression> getExpressions(EntityPathBase obj) : Dto의 Expresion 목록을 추출 - 하나의 Repository에서 가장 복잡한 조건식을 가지는 Dto에 대해서 추출(이경우가 항목 수가 가장 많더라.)
* select(dtoType, expressions) : Dto로 쿼리하는 편의 함수 - getQuerydsl().createQuery().select(Projections.fields(Class<? extends T> type, Expression<?>... exprs)); 축약
* offset(page, size) : 페이징 시 page, size 값으로 offset값 계산
* likeSide(keyword) : keyword 양쪽에 '%'를 추가해주는 함수 'keyword' -> '%keyword%'
* subSelect(expression) : SELECT 에서 subQuery를 위한 'JPAExpressions.select(Expression<T> expr)' 축약
* as(source, alias) : ExpressionUtils.as(Expression<D> source, String alias) 축약 

# Predicates
QueryDSL의 다양한 Expression 조합을 함수형으로 작성하기 위한 Helper Class
현재까지 필요한 만큼만 개발되어 있고 추가 고려사항이 있다면 추가 개발 필요

* 조건절의 처리를 where내에서 처리되도록 함
* 입력값의 필수(required), 옵션(optional) 구분 (if로 처리하는 것 단순화)
* loop 처리

```
    @Override
    public List<BookDto> findAll(BooksQuery query) {
        final var book = QBook.book;
        var list = select(BookDto.class, getExpressions())
                .from(book)
                .where(Predicates.start()
                    .optional(book.categoryId::eq, query.getCategoryId())
                .end())
                .orderBy(book.title.asc())
                .fetch();

        return list;
    }
```

예제 추가 필요

# ValueObjectId & QueryDSL
ValueObjectId를 property로 사용하는 경우
QueryDSL로 쿼리시 id proertyName이 2개 이상이여서 충돌이 발생한다.
'Multiple entries with same key jpa' 에러 발생

생각해 볼 수 있는 해결책

## propertyName에 항상 Entity 명을 prefix로 사용
BookId.id -> BookId.booId  
CategoryId.id -> CategoryId.categoryId

## Expresion을 Projections.as(expression, alias)로 변경
```
// book.categoryId.id 가 'id=' 가 아닌 'category_id='로 조건이 걸리도록 수정  
var newExpression = Projections.as(expression, "category_id"
```

### SharedQuerydslRepositorySupport와 함께 사용한 경우 생성자에서 변경
아래 방법보다 좀 더 깔끔하게 해결할 방법이 없을까?
```
public class CustomBookRepositoryImpl extends SharedQuerydslRepositorySupport implements CustomBookRepository {
    public CustomBookRepositoryImpl() {
        super(Book.class, QBook.class, QBook.book);

        final var book = QBook.book;
        // https://github.com/querydsl/querydsl/issues/1214
        var newExpressions = Arrays.stream(getExpressions()).map(expression -> {
            if (expression.toString().equals("book.categoryId.id")) {
                return as(expression, "category_id");
            }
            return expression;
        }).toArray(Expression[]::new);
        setExpressions(newExpressions);
    }
    ...
}
```


# Reference
[[gradle] 그레이들 Annotation processor와 Querydsl](http://honeymon.io/tech/2020/07/09/gradle-annotation-processor-with-querydsl.html)

