package app.moodle.moodle.services;

import java.util.List;
import java.util.Map;

import app.moodle.moodle.models.Course;
import app.moodle.moodle.models.Token;
import app.moodle.moodle.models.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Moodle API endpoints
 */

public interface MoodleService {

    @GET("/login/token.php")
    Call<Token> login(@QueryMap Map<String, String> params);

    @POST("/webservice/rest/server.php")
    @FormUrlEncoded
    Call<List<User>> userInfo(@QueryMap Map<String, String> params,
                              @Field("field") String field,
                              @Field("values[0]") String value);

    @GET("/webservice/rest/server.php")
    Call<List<Course>> listCourses(@QueryMap Map<String, String> params);
}
