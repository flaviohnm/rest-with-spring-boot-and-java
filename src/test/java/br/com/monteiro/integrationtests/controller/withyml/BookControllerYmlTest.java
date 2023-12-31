package br.com.monteiro.integrationtests.controller.withyml;

import br.com.monteiro.configs.TestConfigs;
import br.com.monteiro.integrationtests.controller.withyml.mapper.YMLMapper;
import br.com.monteiro.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.monteiro.integrationtests.vo.AccountCredentialsVO;
import br.com.monteiro.integrationtests.vo.BookVO;
import br.com.monteiro.integrationtests.vo.TokenVO;
import br.com.monteiro.integrationtests.vo.pagedmodels.PagedModelBook;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerYmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YMLMapper objectMapper;
    private static BookVO book;

    @BeforeAll
    public static void setup() {
        objectMapper = new YMLMapper();

        book = new BookVO();

    }

    @Test
    @Order(0)
    public void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        var accessToken = given()
                .config(RestAssuredConfig.config().encoderConfig(
                        EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(
                                        TestConfigs.CONTENT_TYPE_YML,
                                        ContentType.TEXT)))
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .body(user, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class, objectMapper)
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
    public void testCreate() {
        mockBook();

        var persistedBook = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(
                        EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(
                                        TestConfigs.CONTENT_TYPE_YML,
                                        ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .body(book, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, objectMapper);

        book = persistedBook;

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
    public void testUpdate() {

        book.setTitle("Docker Deep Dive - Updated");

        var bookUpdated = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(
                        EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(
                                        TestConfigs.CONTENT_TYPE_YML,
                                        ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .body(book, objectMapper)
                .when()
                .put("{id}", book.getId())
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, objectMapper);

        book = bookUpdated;

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
    public void testFindById() {
        mockBook();

        var persistedBook = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(
                        EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(
                                        TestConfigs.CONTENT_TYPE_YML,
                                        ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, objectMapper);

        book = persistedBook;

        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getPrice());
        assertEquals(persistedBook.getId(), book.getId());
        assertEquals("Docker Deep Dive - Updated", persistedBook.getTitle());
        assertEquals("Nigel Poulton", persistedBook.getAuthor());
        assertEquals(55.99, persistedBook.getPrice());
    }

    @Test
    @Order(5)
    public void testDelete() {
        given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(
                        EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(
                                        TestConfigs.CONTENT_TYPE_YML,
                                        ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .pathParam("id", book.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    public void testFindAll() {

        var wrapper = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(
                        EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(
                                        TestConfigs.CONTENT_TYPE_YML,
                                        ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .queryParams("page", 0, "limit", 5, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedModelBook.class, objectMapper);

        var books = wrapper.getContent();

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

    private void mockBook() {
        book.setTitle("Docker Deep Dive");
        book.setAuthor("Nigel Poulton");
        book.setPrice(Double.valueOf(55.99));
        book.setLaunchDate(new Date());
    }

}
