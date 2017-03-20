package com.mybottle.ingenicoversion2.api.services;

import com.mybottle.ingenicoversion2.model.CloseReasonResponse;
import com.mybottle.ingenicoversion2.model.CustomerResponse;
import com.mybottle.ingenicoversion2.model.FunctionResponse;
import com.mybottle.ingenicoversion2.model.Job;
import com.mybottle.ingenicoversion2.model.ResolutionResponse;
import com.mybottle.ingenicoversion2.model.ReviewJobResponse;
import com.mybottle.ingenicoversion2.model.SparepartResponse;
import com.mybottle.ingenicoversion2.model.SymptomResponse;
import com.mybottle.ingenicoversion2.model.Technician;

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
    //-----------------------------------Phase 0.1--------------------------------------------------

    //Login
    @GET("technician/auth/login/technician_code/{technician_code}/password/{password}")
    Call<Technician> loginTechnician(@Path("technician_code") String tech_code,
                                     @Path("password") String pass,
                                     @Query("X-API-KEY") String api_key);

    //Logout
    @GET("technician/auth/logout/technician_code/{technician_code}/{attendance_id}/password/{password}")
    Call<Technician> logoutTechnician(@Path("technician_code") String tech_code,
                                      @Path("attendance_id") String attendance_id,
                                      @Path("password") String pass,
                                      @Query("X-API-KEY") String api_key);

    //Start Job EDC
    @FormUrlEncoded
        @POST("technician/startJob")
    Call<Job> startJob(@Field("technician_code") String techCode,
                       @Field("terminal_code") String termCode,
                       @Field("X-API-KEY") String apiKey);

    //Finish Job EDC
    @FormUrlEncoded
    @POST("technician/endJob")
    Call<Job> endJob(@Field("technician_code") String techCode,
                     @Field("terminal_code") String termCode,
                     @Field("function_code") String funcCode,
                     @Field("symptom_code") String sympCode,
                     @Field("spareparts") String spareCode,
                     @Field("start_time") String startTime,
                     @Field("X-API-KEY") String apiKey);

    //Finish Job EDC
    @FormUrlEncoded
    @POST("technician/endJob")
    Call<Job> endJob2Hit(@Field("technician_code") String techCode,
                         @Field("function_code") String funcCode,
                         @Field("symptom_code") String sympCode,
                         @Field("spareparts") String spareCode,
                         @Field("job_id") String jobId,
                         @Field("resolutions") String resolution,
                         @Field("customer_code") String customer,
                         @Field("terminal_code") String termCode,
                         @Field("X-API-KEY") String apiKey,
                         @Field("closereason") String closeReason);

    //Sparepart
    @GET("sparepart/getAllSparepartByCategoryCode/{category_code}")
    Call<SparepartResponse> getAllSparepartByCategory(@Path("category_code") String ctgry_code,
                                                      @Query("X-API-KEY") String api_key);

    //History Today
    @GET("technician/getHistoryToday/{technician_code}")
    Call<ReviewJobResponse> getHistoryToday(@Path("technician_code") String tech_code,
                                            @Query("X-API-KEY") String api_key);

    //History Yesterday
    @GET("technician/getHistoryYesterday/{technician_code}")
    Call<ReviewJobResponse> getHistoryYesterday(@Path("technician_code") String tech_code,
                                                @Query("X-API-KEY") String api_key);

    //Job Detail by Job id
    @GET("job/getJob/{job_id}?X-API-KEY={api_key}")
    Call<Technician> getDetailJobByJobId(@Path("job_id") String job_id,
                                         @Path("api_key") String apiKey);

    //Function
    @GET("func/getAllFunctionByBrand/{brand_code}")
    Call<FunctionResponse> getAllFunction(@Path("brand_code")String brandCode,
                                          @Query("X-API-KEY") String api_key);

    //Function Detail by func_code
    @GET("func/getFunction/{function_code}")
    Call<FunctionResponse> getFunctionDetailByCode(@Path("function_code") String function_code,
                                                   @Query("X-API-KEY") String api_key);

    //Symptom
    @GET("symptom/getAllSymptomByFunctionCodeAndBrand/{function_code}/{brand_code}")
    Call<SymptomResponse> getAllSymptom(@Path("function_code") String funcCode,
                                        @Path("brand_code") String brandCode,
                                        @Query("X-API-KEY") String api_key);

    //Sympton Detail by symptom_code
    @GET("symptom/getSymptom/{symptom_code}?X-API-KEY={api_key}")
    Call<Technician> getSymptomDetailByCode(@Path("symptom_code") String symptom_code,
                                            @Path("api_key") String api_key);

    //Sparepart
    @GET("sparepart/getAllSparepart")
    Call<SparepartResponse> getAllSparepart(@Query("X-API-KEY") String api_key);

    //Sparepart Detail by sparepart_code
    @GET("sparepart/getSparepart/{sparepart_code}?X-API-KEY={api_key}")
    Call<Technician> getSparepartDetailByCode(@Path("sparepart_code") String sparepart_code,
                                              @Path("api_key") String api_key);

    //Customer
    @GET("customer/getAll")
    Call<CustomerResponse> getAllCustomer(@Query("X-API-KEY") String api_key);

    //Resolution
    @GET("resolution/getAllByBrand/{brand_code}")
    Call<ResolutionResponse> getAllResolution(@Path("brand_code") String brandCode,
                                              @Query("X-API-KEY") String api_key);

    //------------------------------------------Phase 0.2---------------------------------------------

    // MotherBoard Start
    @FormUrlEncoded
    @POST("technician/startJobMotherBoard")
    Call<Job> startJobMotherboard(@Field("technician_code") String techCode,
                                  @Field("item_code") String itemCode,
                                  @Field("X-API-KEY") String apiKey);

    // MotherBoard Finish
    @FormUrlEncoded
    @POST("technician/endJobMotherBoard")
    Call<Job> finishJobMotherboard(@Field("technician_code") String techCode,
                                   @Field("item_code") String itemCode,
                                   @Field("jobcustom_id") String jobId,
                                   @Field("X-API-KEY") String apiKey,
                                   @Field("problem") String problem,
                                   @Field("resolution") String resolution);

    // Close Reason
    @GET("closereason/getAll")
    Call<CloseReasonResponse> getAllCloseReason(@Query("X-API-KEY") String api_key);

    // Close Reason Detail
    @GET("closereason/getClosereason/{closereason_code}")
    Call<CloseReasonResponse> getAllCloseReasonDetail(@Path("closereason_code")String closeReason,
                                                      @Query("X-API-KEY") String api_key);

    //History EDC Today
    @GET("technician/getHistoryTodayJob/{technician_code}")
    Call<ReviewJobResponse> getHistoryEDCToday(@Path("technician_code") String tech_code,
                                               @Query("X-API-KEY") String api_key);

    //History EDC Yesterday
    @GET("technician/getHistoryYesterdayJob/{technician_code}")
    Call<ReviewJobResponse> getHistoryEDCYesterday(@Path("technician_code") String tech_code,
                                                   @Query("X-API-KEY") String api_key);

    //History Mainboard Today
    @GET("technician/getHistoryTodayJobcustom/{technician_code}")
    Call<ReviewJobResponse> getHistoryMBToday(@Path("technician_code") String tech_code,
                                              @Query("X-API-KEY") String api_key);

    //History Mainboard Yesterday
    @GET("technician/getHistoryYesterdayJobcustom/{technician_code}")
    Call<ReviewJobResponse> getHistoryMBYesterday(@Path("technician_code") String tech_code,
                                                  @Query("X-API-KEY") String api_key);



}
