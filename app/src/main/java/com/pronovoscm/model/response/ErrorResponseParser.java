package com.pronovoscm.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Handles parsing error responses from API.
 */
class ErrorResponseParser {

    private static final String TAG = ErrorResponseParser.class.getName();

    private static final Gson gson = new GsonBuilder().create();

    ErrorResponse parse(Response<?> response) {
        try {
            ResponseBody body = response.errorBody();
            if (body == null) {
                return new ErrorResponse("No error response provided.");
            }
            return gson.fromJson(body.charStream(), ErrorResponse.class);
        } catch (Throwable t) {
            Log.e(TAG, t.getMessage(), t);
        }
        ErrorResponse errorResponse = new ErrorResponse(response.message());
        errorResponse.setStatus(response.code());
        //errorResponse.setData(new ErrorResponse.Data());
        return errorResponse;

//        return new ErrorResponse(String.format("ApiRequest code  [%s], message [%s]", response.code(), response.message()));
    }

}
