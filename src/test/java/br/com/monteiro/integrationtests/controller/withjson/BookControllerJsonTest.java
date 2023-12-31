package br.com.monteiro.integrationtests.controller.withjson;

import br.com.monteiro.configs.TestConfigs;
import br.com.monteiro.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.monteiro.integrationtests.vo.AccountCredentialsVO;
import br.com.monteiro.integrationtests.vo.BookVO;
import br.com.monteiro.integrationtests.vo.TokenVO;
import br.com.monteiro.integrationtests.vo.wrappers.WrapperBookVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static BookVO book;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookVO();

    }

    @Test
    @Order(0)
    public void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        var accessToken = given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class)
                .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/api/book/v1/")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

    }

    @Test
    @Order(1)
    public void testCreate() throws IOException {
        mockBook();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(book)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        book = objectMapper.readValue(content, BookVO.class);

        assertNotNull(book.getId());
        assertNotNull(book.getTitle());
        assertNotNull(book.getAuthor());
        assertNotNull(book.getPrice());
        assertTrue(book.getId() > 0);
        assertEquals("Docker Deep Dive", book.getTitle());
        assertEquals("Nigel Poulton", book.getAuthor());
        assertEquals(55.99, book.getPrice());
    }

    @Test
    @Order(3)
    public void testUpdate() throws IOException {

        book.setTitle("Docker Deep Dive - Updated");

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(book)
                .when()
                .put("{id}", book.getId())
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO bookUpdated = objectMapper.readValue(content, BookVO.class);

        assertNotNull(bookUpdated.getId());
        assertNotNull(bookUpdated.getTitle());
        assertNotNull(bookUpdated.getAuthor());
        assertNotNull(bookUpdated.getPrice());
        assertEquals(bookUpdated.getId(), book.getId());
        assertEquals("Docker Deep Dive - Updated", bookUpdated.getTitle());
        assertEquals("Nigel Poulton", bookUpdated.getAuthor());
        assertEquals(55.99, bookUpdated.getPrice());
    }

    @Test
    @Order(4)
    public void testFindById() throws IOException {
        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO foundBook = objectMapper.readValue(content, BookVO.class);

        assertNotNull(foundBook.getId());
        assertNotNull(foundBook.getTitle());
        assertNotNull(foundBook.getAuthor());
        assertNotNull(foundBook.getPrice());
        assertEquals(foundBook.getId(), book.getId());
        assertEquals("Docker Deep Dive - Updated", foundBook.getTitle());
        assertEquals("Nigel Poulton", foundBook.getAuthor());
        assertEquals(55.99, foundBook.getPrice());
    }

    @Test
    @Order(5)
    public void testDelete() {
        given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("id", book.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    public void testFindAll() throws IOException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .queryParams("page", 0, "size", 5, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();


        WrapperBookVO wrapper = objectMapper.readValue(content, WrapperBookVO.class);

        var books = wrapper.getEmbedded().getBooks();

        BookVO foundBookOne = books.getFirst();

        assertNotNull(foundBookOne.getId());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getPrice());
        assertTrue(foundBookOne.getId() > 0);
        assertEquals("Implantando a governança de TI", foundBookOne.getTitle());
        assertEquals("Aguinaldo Aragon Fernandes e Vladimir Ferraz de Abreu", foundBookOne.getAuthor());
        assertEquals(54.00, foundBookOne.getPrice());

        BookVO foundBookFive = books.get(4);

        assertNotNull(foundBookFive.getId());
        assertNotNull(foundBookFive.getTitle());
        assertNotNull(foundBookFive.getAuthor());
        assertNotNull(foundBookFive.getPrice());
        assertTrue(foundBookFive.getId() > 0);
        assertEquals("Head First Design Patterns", foundBookFive.getTitle());
        assertEquals("Eric Freeman, Elisabeth Freeman, Kathy Sierra, Bert Bates", foundBookFive.getAuthor());
        assertEquals(110.0, foundBookFive.getPrice());
    }

    @Test
    @Order(7)
    public void testHATEOAS() {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .accept(TestConfigs.CONTENT_TYPE_JSON)
                .queryParams("page", 0, "size", 8, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/15\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/9\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/4\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/8\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/7\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/14\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/6\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/1\"}}}"));

        assertTrue(content.contains("{\"first\":{\"href\":\"http://localhost:8888/api/book/v1/?direction=asc&page=0&size=8&sort=author,asc\"}"));
        assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/book/v1/?page=0&size=8&direction=asc\"}"));
        assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/book/v1/?direction=asc&page=1&size=8&sort=author,asc\"}"));
        assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/book/v1/?direction=asc&page=1&size=8&sort=author,asc\"}}"));

        assertTrue(content.contains("\"page\":{\"size\":8,\"totalElements\":15,\"totalPages\":2,\"number\":0}}"));

    }

    private void mockBook() {
        book.setTitle("Docker Deep Dive");
        book.setAuthor("Nigel Poulton");
        book.setPrice(55.99);
        book.setLaunchDate(new Date());
    }

}
