package ru.vtarasov.mp.student.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vtarasov.mp.student.Student;

/**
 * @author vtarasov
 * @since 27.09.2019
 */
public class StudentControllerTest {
    private static final String URL = "http://localhost:8181";
    private static final ObjectMapper JSON = new ObjectMapper();

    private static HttpClient CLIENT_WITH_AUTH;
    private static HttpClient CLIENT_WITH_WRONG_AUTH;
    private static HttpClient CLIENT_WITH_NO_AUTH;

    private static Student notRegisteredStudent;
    private static Student registeredStudent;

    @BeforeClass
    public static void setUpClass() throws Exception {
        CLIENT_WITH_AUTH = buildClientWithAuth("user", "user");
        CLIENT_WITH_WRONG_AUTH = buildClientWithAuth("wrongUser", "wrongUser");
        CLIENT_WITH_NO_AUTH = buildClientWithNoAuth();

        notRegisteredStudent = new Student(null, "Student", 16);
        registeredStudent = registerStudentAndReturn(notRegisteredStudent);
    }

    private static HttpClient buildClientWithAuth(String name, String password) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(name, password));

        return HttpClientBuilder.create().disableAuthCaching().setDefaultCredentialsProvider(credentialsProvider).build();
    }

    private static HttpClient buildClientWithNoAuth() {
        return HttpClientBuilder.create().build();
    }

    @Test
    public void shouldNotFoundStudentIfNotRegistered() throws Exception {
        HttpGet request = new HttpGet(URL + "/student/id-not-registered");
        HttpResponse response = CLIENT_WITH_AUTH.execute(request);

        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void shouldFoundStudentIfRegistered() throws Exception {
        HttpGet request = new HttpGet(URL + "/student/" + registeredStudent.getId());
        HttpResponse response = CLIENT_WITH_AUTH.execute(request);

        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_OK));
        Assert.assertThat(headerValue(response, HttpHeaders.CONTENT_TYPE), Matchers.equalTo(MediaType.APPLICATION_JSON));

        Student registered = JSON.readValue(response.getEntity().getContent(), Student.class);

        Assert.assertThat(registered, Matchers.equalTo(registeredStudent));
    }

    @Test
    public void shouldReturnBadRequestWhenTryingToRegisterStudentWithNonNullId() throws Exception {
        HttpResponse response = registerStudentAndReturnResponse(registeredStudent);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void shouldReturnBadRequestWhenTryingToRegisterStudentWithEmptyNullOrNotPresentedName() throws Exception {
        Student emptyNameStudent = new Student(null, "", 16);
        Student nullNameStudent = new Student(null, null, 16);

        HttpResponse response = registerStudentAndReturnResponse(emptyNameStudent);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_BAD_REQUEST));

        response = registerStudentAndReturnResponse(nullNameStudent);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_BAD_REQUEST));

        response = registerStudentAndReturnResponse("{\"age\": 16}");
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void shouldReturnBadRequestWhenTryingToRegisterStudentWithNullLessThanSixteenOrNotPresentedAge() throws Exception {
        Student nullAgeStudent = new Student(null, "Student", null);
        Student lessThanSixteenAgeStudent = new Student(null, "Student", 15);

        HttpResponse response = registerStudentAndReturnResponse(nullAgeStudent);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_BAD_REQUEST));

        response = registerStudentAndReturnResponse(lessThanSixteenAgeStudent);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_BAD_REQUEST));

        response = registerStudentAndReturnResponse("{\"name\": \"Student\"}");
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void shouldUnregisterStudentIfRegistered() throws Exception {
        Student forRemoving = registerStudentAndReturn(new Student(null, "Vova", 16));
        HttpDelete delete = new HttpDelete(URL + "/student/" + forRemoving.getId());
        HttpResponse response = CLIENT_WITH_AUTH.execute(delete);

        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void shouldNotFoundStudentWhenUnregisteringOfNotRegistered() throws Exception {
        HttpDelete delete = new HttpDelete(URL + "/student/id-not-registered");
        HttpResponse response = CLIENT_WITH_AUTH.execute(delete);

        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void shouldReturnUnauthorizedWhenTryingToFindStudentWithNoOrWrongCredentials() throws Exception {
        HttpGet request = new HttpGet(URL + "/student/id-not-registered");

        HttpResponse response = CLIENT_WITH_NO_AUTH.execute(request);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_UNAUTHORIZED));

        response = CLIENT_WITH_WRONG_AUTH.execute(request);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_UNAUTHORIZED));
    }

    @Test
    public void shouldReturnUnauthorizedWhenTryingToRegisterStudentWithNoOrWrongCredentials() throws Exception {
        HttpPost post = new HttpPost(URL + "/student");
        post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        post.setEntity(new StringEntity(JSON.writeValueAsString(new Student(null, "Vova", 16)), ContentType.APPLICATION_JSON));

        HttpResponse response = CLIENT_WITH_NO_AUTH.execute(post);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_UNAUTHORIZED));

        response = CLIENT_WITH_WRONG_AUTH.execute(post);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_UNAUTHORIZED));
    }

    @Test
    public void shouldReturnUnauthorizedWhenTryingToUnregisterStudentWithNoOrWrongCredentials() throws Exception {
        Student forRemoving = registerStudentAndReturn(new Student(null, "Vova", 16));
        HttpDelete delete = new HttpDelete(URL + "/student/" + forRemoving.getId());

        HttpResponse response = CLIENT_WITH_NO_AUTH.execute(delete);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_UNAUTHORIZED));

        response = CLIENT_WITH_WRONG_AUTH.execute(delete);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_UNAUTHORIZED));
    }

    private static HttpResponse registerStudentAndReturnResponse(Student student) throws Exception {
        return registerStudentAndReturnResponse(JSON.writeValueAsString(student));
    }

    private static HttpResponse registerStudentAndReturnResponse(String studentJson) throws Exception {
        HttpPost post = new HttpPost(URL + "/student");
        post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        post.setEntity(new StringEntity(studentJson, ContentType.APPLICATION_JSON));

        return CLIENT_WITH_AUTH.execute(post);
    }

    private static Student registerStudentAndReturn(Student student) throws Exception {
        HttpResponse response = registerStudentAndReturnResponse(JSON.writeValueAsString(student));

        String location = headerValue(response, HttpHeaders.LOCATION);
        String id = location.substring(location.lastIndexOf("/") + 1);

        return new Student(id, student.getName(), student.getAge());
    }

    private static String headerValue(HttpResponse response, String headerName) {
        return response.getHeaders(headerName)[0].getValue();
    }
}
