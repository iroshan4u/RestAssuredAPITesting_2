package apipkg;

import modelpkg.Product;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ApiTest {

    @Test
    public void getCategories(){
        String endpoint = "http://localhost/api_testing/category/read.php";
        var response = given().port(80).when().get(endpoint).then();
        response.log().body();
    }

    @Test
    public void getProduct(){
        String endpoint = "http://localhost/api_testing/product/read_one.php";
        var response =
                given().queryParam("id", 2).port(80).
                        when().get(endpoint).
                        then();
        response.log().body();
    }

    @Test
    public void createProduct(){
        String endpoint = "http://localhost/api_testing/product/create.php";
        String body = """
                {
                "name": "T Shirt ABC",
                "description": "T Shirt Red colour",
                "price": "400.00",
                "category_id": 3
                }
                 """;
        var response = given().port(80).body(body).when().post(endpoint).then();
        response.log().body();
    }

    @Test
    public void updateProduct(){
        String endpoint = "http://localhost/api_testing/product/update.php";
        String body = """
                {
                "id": 1000,
                "name": "T Shirt ABC",
                "description": "T Shirt Red colour",
                "price": "200.00",
                "category_id": 2
                }
                """;
        var response = given().port(80).body(body).when().put(endpoint).then();
        response.log().body();
    }

    @Test
    public void deleteProduct(){
        String endpoint = "http://localhost/api_testing/product/delete.php";
        String body = """
                {
                "id": 1000
                }
                """;

        var response = given().port(80).body(body).when().delete(endpoint).then();
        response.log().body();
    }

     // using the serialize object instead of String for the body is alternative approach
    @Test
    public void createSeriializeProduct(){
        String endpoint = "http://localhost/api_testing/product/create.php";
        Product product = new Product(
                "Water Bottle",
                "Water Bottle 500ml",
                350,
                3
        );

        var response = given().port(80).body(product).when().post(endpoint).then();
        response.log().body();
    }

    // assertions
    // reset assured library contain 'hamcrest' class of function for compare expected to actual results
    @Test
    public void getProductAssert(){
        String endpoint = "http://localhost/api_testing/product/read_one.php";
        var response =
                given().queryParam("id", 2).port(80).
                        when().get(endpoint).
                        then().
                        assertThat().
                        statusCode(200).
                        body("id", equalTo("2")).
                        body("name", equalTo("Cross-Back Training Tank")).
                        body("description", equalTo("The most awesome phone of 2013!")).
                        body("price", equalTo("299.00")).
                        body("category_id", equalTo("2")).
                        body("category_name", equalTo("Active Wear - Women"));
    }

    @Test
    public void getAllProductsVerify(){
        String endpoint = "http://localhost/api_testing/product/read.php";
        var response =
                        given().port(80).
                        when().get(endpoint).
                        then().
                        assertThat().
                        statusCode(200).
                        header("Content-Type", equalTo("application/json; charset=UTF-8")).
                        body("records.size()", greaterThan(0)).
                        body("records.id", everyItem(notNullValue())).
                        body("records.name", everyItem(notNullValue())).
                        body("records.description", everyItem(notNullValue())).
                        body("records.price", everyItem(notNullValue())).
                        body("records.category_id", everyItem(notNullValue())).
                        body("records.category_name", everyItem(notNullValue())).
                        body("records.id[0]", equalTo("1001")).
                        log().headers();
    }

    @Test
    public void getDeserializedProduct(){
        String endpoint = "http://localhost/api_testing/product/read_one.php";
        Product expectedProduct = new Product(
                2,
                "Cross-Back Training Tank",
                "The most awesome phone of 2013!",
                299.00,
                2,
                "Active Wear - Women"
        );

        Product actualProduct =
                given().port(80).
                queryParam("id", "2").
                when().
                get(endpoint).
                as(Product.class);

        assertThat(actualProduct, samePropertyValuesAs(expectedProduct));
    }

}
