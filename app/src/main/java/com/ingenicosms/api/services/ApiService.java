package com.ingenicosms.api.services;

import com.ingenicosms.model.FunctionResponse;
import com.ingenicosms.model.Job;
import com.ingenicosms.model.ReviewJobResponse;
import com.ingenicosms.model.SparepartResponse;
import com.ingenicosms.model.SymptomResponse;
import com.ingenicosms.model.Technician;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ILM on 6/29/2016.
 */
public interface ApiService {

    //Login - Test Success
    @GET("technician/auth/login/technician_code/{technician_code}/password/{password}")
    Call<Technician> loginTechnician(@Path("technician_code") String tech_code,
                                     @Path("password") String pass,
                                     @Query("X-API-KEY") String api_key);

    //test - done
    @GET("technician/auth/logout/technician_code/{technician_code}/{attendance_id}/password/{password}")
    Call<Technician> logoutTechnician(@Path("technician_code") String tech_code,
                                      @Path("attendance_id") String attendance_id,
                                      @Path("password") String pass,
                                      @Query("X-API-KEY") String api_key);

    //test done
    @FormUrlEncoded
    @POST("technician/startJob")
    Call<Job> startJob(@Field("technician_code") String techCode,
                              @Field("terminal_code") String termCode,
                              @Field("X-API-KEY") String apiKey);

    //test done
    @FormUrlEncoded
    @POST("technician/endJob")
    Call<Job> endJob(@Field("technician_code") String techCode,
                     @Field("terminal_code") String termCode,
                     @Field("function_code") String funcCode,
                     @Field("symptom_code") String sympCode,
                     @Field("spareparts") String spareCode,
                     @Field("start_time") String startTime,
                     @Field("X-API-KEY") String apiKey);

//    technician_code/$technician_code/
//    terminal_code/$terminal_code/
//    function_code/$function_code/
//    symptom_code/$symptom_code/
//    spareparts/$spareparts/
//    start_time/$start_time

    //test- done
    @GET("technician/getAllHistory/{technician_code}")
    Call<ReviewJobResponse> getAllHistory(@Path("technician_code") String tech_code,
                                          @Query("X-API-KEY") String api_key);

    @GET("job/getJob/{job_id}?X-API-KEY={api_key}")
    Call<Technician> getDetailJobByJobId(@Path("job_id") String job_id,
                                         @Path("api_key") String apiKey);

    // test done
    @GET("func/getAllFunction")
    Call<FunctionResponse> getAllFunction(@Query("X-API-KEY") String api_key);

    @GET("func/getFunction/{function_code}?X-API-KEY={api_key}")
    Call<Technician> getFunctionDetailByCode(@Path("function_code") String function_code,
                                             @Path("api_key") String api_key);

    // test done
    @GET("symptom/getAllSymptomByFunctionCode/{func_code}")
    Call<SymptomResponse> getAllSymptom(@Path("func_code") String func_code,
                                        @Query("X-API-KEY") String api_key);

    @GET("symptom/getSymptom/{symptom_code}?X-API-KEY={api_key}")
    Call<Technician> getSymptomDetailByCode(@Path("symptom_code") String symptom_code,
                                             @Path("api_key") String api_key);

    // test done
    @GET("sparepart/getAllSparepart")
    Call<SparepartResponse> getAllSparepart(@Query("X-API-KEY") String api_key);

    @GET("sparepart/getSparepart/{sparepart_code}?X-API-KEY={api_key}")
    Call<Technician> getSparepartDetailByCode(@Path("sparepart_code") String sparepart_code,
                                             @Path("api_key") String api_key);

}
