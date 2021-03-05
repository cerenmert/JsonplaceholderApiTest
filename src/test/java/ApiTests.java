import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

public class ApiTests {

    public static String recordNotFound = "[]";
    public static String ourbaseurl = "https://jsonplaceholder.typicode.com";

    @Test
    public void testResponseCode() {
        RestAssured.baseURI = ourbaseurl;
        Response resp = given().get("/comments/2");
        int code = resp.getStatusCode();
        System.out.println("status code is " + code);
        //Assert.assertEquals(code, 200);
    }

    @Test
    public void testResponseBody() {
        Response resp = RestAssured.get("https://jsonplaceholder.typicode.com/comments/2");
        String data = resp.asString();
        System.out.println("Response Data is " + data);
    }

    @Test //should be Fail
    public void validateUserEmail() {
        RestAssured.baseURI = ourbaseurl;
        String expectedEmail = "Joana.Schoen@leora.co.uk";
        String actualEmail =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .when()
                        .get("/comments/88")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("email");
        Assert.assertEquals(actualEmail, expectedEmail); //actualEmail's value comes from server
    }

    @Test //should be Fail
    public void validateAllDataWithClass() {
        RestAssured.baseURI = ourbaseurl;
        Response response = given().get("/comments/69");
        ValidateSuccessResponse successResponse = response.getBody().as(ValidateSuccessResponse.class);
        Assert.assertEquals(successResponse.email, "Joana.Schoen@leora.co.uk");
        Assert.assertEquals(successResponse.postId, 14);
        Assert.assertEquals(successResponse.name, "at aut ea iure accusantium voluptatum nihil ipsum");
        Assert.assertEquals(successResponse.body, Matchers.containsString("omnis dolor autem qui est natus"));
    }

    @Test
    public void performQueryParameters() {
        RestAssured.baseURI = ourbaseurl;
        Response response = given().get("/posts/2");
        ValidateResponsePost successResponse = response.getBody().as(ValidateResponsePost.class);
        Assert.assertEquals(successResponse.id, 2);
        Assert.assertEquals(successResponse.title, "qui est esse");
    }

    @Test
    public void lengthOfArray() {
        RestAssured.baseURI = ourbaseurl;
        Response response = given().get("/comments");
        int size = response.getBody().jsonPath().getList("id").size();
        Assert.assertEquals(size, 500);
    }

    @Test
    public void shouldSearchWithWebsiteAndPhone() {
        Response response = RestAssured.given()
                .queryParam("website", "anastasia.net")
                .queryParam("phone", "010-692-6593 x09125")
                .get("https://jsonplaceholder.typicode.com/users")
                .then()
                .statusCode(200)
                .extract().response();
        assertThat(response.getBody().jsonPath().getString("username"), Matchers.containsString("Antonette"));
        String data = response.asString();
        System.out.println("Response Data is " + data);
    }

    @Test
    public void checkUserIsNotExist() {
        Response response = RestAssured.given()
                .queryParam("name", "Ceren") // böyle bir name olmadığı için response olarak [] dönüyor.
                .get("https://jsonplaceholder.typicode.com/users")
                .then()
                .statusCode(200)
                .extract().response();

        assertThat(response.asString(), Matchers.is(recordNotFound));
    }

    @Test
    public void PostRequest() {
        RestAssured.baseURI = ourbaseurl;
        RequestSpecification request = RestAssured.given();

        // Create a JSON request which contains all the fields
        JsonObject requestParams = new JsonObject();
        requestParams.addProperty("userId", 1);
        requestParams.addProperty("title", "cerenTestTitle");
        requestParams.addProperty("body", "cerenTestBody");

        // userId=1&title=cerenTestTitle

        // Add JSON body in the request and send the Request
        // Add a header stating the Request body is a JSON
        request.header("Content-Type", "application/json");
        // Add the Json to the body of the request
        request.body(requestParams.getAsJsonObject());
        // Post the request and check the response
        Response response = request.post("/posts");

        //Validate the Response
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 201);
        response.prettyPeek(); //write response to the console.

//        String successCode = response.jsonPath().get("SuccessCode");
//        Assert.assertEquals( "Correct success code was returned", successCode);
    }

    @Test
    public void PutRequest() {
        //Step 1: Create a variable which we intend to update with our PUT request.
        int id = 1;
        //this id has been previously created as a resource on the server
        //and we will update the associated user's information with PUT request.

        //Step 2: Create a Request pointing to the Service Endpoint
        RestAssured.baseURI = ourbaseurl;
        RequestSpecification requestPut = RestAssured.given();

        //Step 3: Create a JSON request which contains all the fields which we wish to update.
        JsonObject requestParams = new JsonObject();
        requestParams.addProperty("userId", 8);
        // Meanwhile, we have already 10 users which have userId=8 and now we'll add 11th user
        requestParams.addProperty("title", "cerenTestTitleUpdated");
        requestParams.addProperty("body", "cerenTestBodyUpdated");

        //Step 4: Send JSON content in the body of Request and pass PUT Request
        // Add a header stating the Request body is a JSON
        requestPut.header("Content-Type", "application/json ; charset=UTF-8");
        // Add the Json to the body of the request
        requestPut.body(requestParams.getAsJsonObject());
        //Here, we capture the response for PUT request by passing the associated idD in the baseURI
        Response response = requestPut.put("/posts/" +id);

        // Step 5: Validate the PUT Request response received
        int statusCode = response.getStatusCode();
        System.out.println(response.asString());
        Assert.assertEquals(statusCode, 200);
    }

}
