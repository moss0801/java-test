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
var newExpression = Projections.as(expression, "categoryId");
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
                return as(expression, "categoryId");
            }
            return expression;
        }).toArray(Expression[]::new);
        setExpressions(newExpressions);
    }
    ...
}
```

# 고민해볼 문제

## [우아콘2020] 수십억건에서 QUERYDSL 사용하기

### JpaRepository 및 CustomRepositoryImpl 없이 JPAQueryFactory 사용

* 내가 생각하는 Aggregate 당 Repository 한 개가 성립 조건이 충족하지 않는데, 이를 다시 생각할 만한 강한 동기가 필요

### 동적쿼리는 BooleanBuilder대신 BooleanExpression 사용 - null인 경우 조건이 제거된다. - 모든 조건이 null인 경우 주의 필요

* 내가 개발한 Predicates도 모든 조건이 null인 경우를 대응할 수 없을까? 모든 조건이면 Exception이 발생하면 좋을 것 같다.
* 영상의 예제는 조건마다 함수를 만들어야 하기에 너무 불편해 보인다.

### select 성능개선

* exist 메소드 금지
* 실제 exist는 count 보다 빠르다(하나만 발견되어도 반환)
* 하지만 querydsl의 exist는 'count > 0' 조건으로 체크한다.
* 이에 직접 구현해보자.
   * 1. limit 1로 조회 제한(fecthFirst()) 후 결과 null 여부 확인

### select 성능개선 - cross join 회피

* 묵시적 join 으로 cross join이 발생 (corss join[상호 조인]: 한 쪽 테이블의 모든 행들과 다른 테이블의 모든 행을 조인)
* 명시적 Join 사용해서 해결한다.

### select 성능개선 - Entity 보다는 Dto 를 우선

* Entity 조회시
   * Hiberbate 캐시
   * 불필요한 컬럼 조회
   * OneToOne N+1 쿼리 등
   * 단순 조회 기능에서는 성능 이슈 요소가 많다.
* Entity와 Dto 조회의 구분
   * Entity 조회: 실시간으로 Entity 변경이 필요한 경우
   * Dto 조회: 고강도 성능 개선 or 대량의 데이터 조회가 필요한 경우
* 개선
   * 조회 컬럼 최소화하기: 이미 알고 있는 값 대체 id -> Expressions.asNumber(id).as("id") -> as 컬럼은 select에서 제외된다.
   * Select 컬럼에 Entity 자제 - 불필요한 컬럼 조회: 사용하지 않는 컬럼까지 모두 조회된다.
   * Select 컬럼에 Entity 자제 - N+1: OneToOne 관계인 Entity가 매건마다 조회된다. OneToOne은 Laze Loading이 되지 않아서 매 건마다 조회된다. entity가 아닌 id컬럼만 조회
   * Select 컬럼에 Entity 자제 - distinct : select 에 선언된 Entity의 컬럼 전체가 distinct 대상이 된다. (distinct를 위한 임시 테이블의 공간, 시간이 소요됨)
    
### select 성능개선 - Group By 최적화
* MySQL에서 Group By를 실행하면 Filesort 가 필수로 발생 (Index가 아닌 경우)
* order by null을 사용하면 Filesort가 제거된다. 하지만 querydsl에서 order by null 문법을 제공하지 않는다.
* 이에 직접 order by null 조건 클래스 개발
  ```
  public class OrderByNull extends OrderSpecifier {
    public static final OrderByNull DEFAULT = enw OrderByNull();
  
    private OrderByNull() {
    super(Order.ASC, NullExpression.DEFAULT, Default);
    } 
  }
    
  ...
    
  .ordeBy(OrderByNull.DEFAULT)
  .fetch();
  ```
    
  ```
  -- GroupBy
  select 1
  from ad_item_sum
  group by created_date  -- 47 s 945 ms
    
  -- Order By null
  select 1
  from ad_item_sum
  group by created_date
  order by null  -- 8 s 465 ms
  ```
    
* 정렬이 필요하더라도, 조회 결과가 100건 이하라면 애플리케이션에서 정렬한다.  
  DB가 WAS보다 비싸다. 저렴한 WAS를 사용하자.
  ```
  result.sort(comparingLong(PointCalculateAmount::getPotinAmount));
  ```
* 단, 페이징일 경우, order by null을 사용하지 못한다
    
### select 성능개선 - 커버링 인덱스

커버링 인덱스: 쿼리를 충족시키는데 필요한 모든 컬럼을 갖고 있는 인덱스

select / where / order by / group by 등에서 사용되는 모든 컬럼이 인덱스에 포함된 상태

NoOffset 방식과 더불어 페이징 조회 성능을 향상시키는 가장 보편적인 방법

아래 예시에서 join절 안에 select 쿼리가 '커버링 인덱스'를 사용한 쿼리의 예시  
페이징 성능을 향상시킬수 있는 보편적인 방법

```
select *
from academy a
join (select id
      from academy
      order by id
      limit 10000, 10) as temp
on temp.id = a.id;
```

JPQL은 from절의 서브쿼리를 지원하지 않는다.

커버링 인덱스 조회는 나눠서 진행
* Cluster key (PK)를 커버링 인덱스로 빠르게 조회하고,
* 조회된 Key로 SELECT 컬럼들을 후속 조회한다.

```
List<Long> ids = queryFactory
    .select(book.id)
    .from(book)
    .where(book.name.like(name + "%"))
    .orderBy(book.id.desc())
    .limit(pageSize)
    .offset(pageNo * pageSize)
    .fetch();

if (CollectionUtils.isEmpty(ids)) {
  return new Array List<>();
}

return queryFactory
    .select(Projections.fiedls(BookPaginationDto.class,
        book.id.as("bookId"),
        book.name,
        book.bookNo,
        book.bookType))
    .from(book)
    .where(book.id.in(ids))
    .orderBy(book.id.desc())
    .fetch();
```

성능차이
* 기존 페이징 : 26s
* jdbc 커버링 : 0.27s
* querydsl 커버링 : 0.58s

### Update/Inser 성능개선

### Update/Inser 성능개선 - 일괄업데이트 최적화

객체지향을 핑계로 성능을 버리진 않는지.
무분별한 DirtyChecking을 꼭 확인해봐야 한다.

DirtyChecking
```
List<Student> students = queryFactory
    .selectFrom(student)
    .where(student.id.loe(studentId))
    .fetch();

for (Student student : students) {
    student.updateName(name);
}
```

Querydsl.update
```
queryFactory.update(student)
    .where(student.id.loe(studentId))
    .set(student.name, name)
    .execute();
```

Querydsl로 한번에 Update하는 것이 비해 DirtyChecking은 성능이 많이 떨어지게 된다.  
성능은 1만건 단일컬럼 기준 약 2000배 차이 (272ms vs 9m 10s 357ms)

단점: 하이버네이트 캐시는 일괄 업데이트시 캐시 갱신이 안됨  
이럴 경우엔 업데이트 대상들에 대한 Cache Eviction이 필요

정리
* DirtyChecking : 실시간 비지니스처리, 실시간 단건 처리시
* Querydsl.update : 대량의 데이터를 일괄로 Update 처리시

**진짜 Entity가 필요한게 아니라면 Querydsl과 Dto를 통해 딱 필요한 항목들만 조회하고 업데이트 한다.**

### Bulk Insert - JPA로 Bulk Insert는 자제한다
JDBC의 rewirteBatchedStatements 으로 Insert 합치기 옵션을 넣어도,  
JPA는 auto_increment일때 Insert 합치기가 적용되지 않는다.

단일 Entity 1만건 save 기준
* JPA.merge : 62s
* JPA.persist : 61s
* Jdbc.Batch : 0.58s

**이에 JdbTemplate으로 Bulk Insert를 처리**  
JdbcTemplate으로 Bulk Insert는 처리되나,  
문자열로 작성되다보니 컴파일체크, 코드-테이블간의 불일치 체크 등 Type Safe 개발이 어려움

```
String sql =
    "INSERT INTO store (" +
    "tx_year, " +
    "tx_month) " +
    "VALUES(:txYear, :txMonth)";

SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(list.toArray());

parameterJdbcTemplate.batchUpdate(sql, batch);
```

### Buik Insert - TypeSafe 한 방식으로 Bulk Insert를 처리할 순 없을까?
적용할 것인지 고민 후, 괜찮다고 생각되면 적용하자.

**Querydsl != Querydsl-JPA**

Native SQL를 생성하는 Querydsl-SQL  
QClass 기반으로 Native SQL을 사용할 수 있는 Querydsl-SQL???

왜 Querydsl-SQL를 사용하지 않을까?

아래는 Querydsl-SQL를 사용하기 위한 절차 (테이블을 Scan 해서 QClass를 반드는 방식이라)
1. 로컬 PC에 DB를 설치하고 실행한뒤,
2. Gradle/Maven에 로컬 DB 정보를 등록해서
3. flyway로 테이블을 생성하고,
4. Querydsl-SQL 플러그인으로 테이블 Scan하면 QClass를 생성하게 됨

### Buik Insert - JPA 어노테이션으로 Querydsl-SQL QClass를 생성할 순 없을까?

**EntityQL** : JPA Entity를 기반으로 Querydsl-SQL QClass를 생성해준다.

EntityQL로 만들어진 Querydsl-SQL의 QClass를 이용하면 BulkInsert 가능!

```
// 단일 Entity
SQLInertSlause insert = sqlQueryFactory.insert(qAcademy);

for (int i = 0; i <= 1000; i++) {
    insert.populate(new Academy("address", "name"), EntityMapper.DEFAULT).addBatch();
}

insert.execute();

// OneToMany
SQLInsertClause insert = sqlQueryFactory.insert(qStudent);

for (int i = 1; i <= 1000; i++) {
    Academy academy = academyRepository.save(new Academy("address", name");
    
    insert.populate(new Student("student", 1, academy). EntityMapper.DEFAULT).addbacth();
    insert.populate(new Student("student", 2, academy). EntityMapper.DEFAULT).addbacth();
}

insert.execute();
```

**성능 비교**  
단일 Entity 1만건 (약 135배 차이)
* JPA: 1m 7s 535ms
* Querydsl-SQL: 565ms

OneToMany Entity 1만건  
* JPA: 12m 6s 828ms
* Querydsl-SQL: 2m 49s 163ms

#### EntityQL -단점
* Gradle 5이상 필요
    * 플러그인이 Gradle 5이상에서만 작동
* 어노테이션에 (name="") 필수
    * @Column의 name값으로 QClass 필드가 선언된다.
    * @Table의 name 값으로 테이블을 찾을 수 있다.
* primitive type 사용 X
    * int, double, boolean 등을 사용할 수 없다.
    * 모든 컬럼은 Wrapper Class로 선언해야 한다.
* 복잡한 설정
    * querydsl-sql이 개선되지 못해 불편한 설정이 많음.
* @Embedded 미지원
    * @Embedded 어노테이션을 통한 컬럼 인식을 못한다.
    * @JoinColumn은 지원한다.
* Querydsl-SQL의 미지원으로 insert쿼리를 @Column의 name으로 만들 수가 없음 (컬럼명과 빌드명이 일치해야하는 BeanMapper만 지원)
    * 즉, @Column용 Mapper가 별도로 필요 - 이에 별도로 개발하였음
    
### 마무리
* 상속과 구현없는 QuerydslRepository
* exist 최적화
* 동적쿼리
* Group By 최적화
* slect문 최적화
* 커버링 인덱스
* cross join
* 일괄 Update
* Bulk Insert

1. 상황에 따라 ORM / 전통적 Query 방식을 골라 사용할 것
2. JPA / Querydsl로 발생하는 쿼리 한번 더 확인하기



# Reference
[[gradle] 그레이들 Annotation processor와 Querydsl](http://honeymon.io/tech/2020/07/09/gradle-annotation-processor-with-querydsl.html)

[[우아콘2020] 수십억건에서 QUERYDSL 사용하기](https://youtu.be/zMAX7g6rO_Y)

