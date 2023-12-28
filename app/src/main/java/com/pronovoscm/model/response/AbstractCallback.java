package com.pronovoscm.model.response;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Base callback for future Retrofit implementation.
 */
public abstract class AbstractCallback<T> implements Callback<T> {

    private final String TAG = AbstractCallback.class.getName();

    private ErrorResponseParser errorResponseParser = new ErrorResponseParser();

    public void onResponse(Call<T> call, Response<T> response) {
        Log.d("Response--", "" + response.body());


        if (response.code() >= 400) {
            onResponseError(call, response);
        } else {
            this.handleSuccess(response);
        }
        this.handleComplete(call);
    }

    public void onFailure(Call<T> call, Throwable t) {
        Log.e(TAG, t.getMessage(), t);
        this.handleFailure(call, t);
        this.handleComplete(call);
    }

    protected void onResponseError(Call<T> call, Response<T> response) {

        ErrorResponse errorResponse = errorResponseParser.parse(response);

        Log.d(AbstractCallback.class.getSimpleName(), call.request().url().toString());

        switch (response.code()) {
            case 400:
                this.handleError(call, errorResponse);
                break;
            case 404:
                this.handleError(call, errorResponse);
            default:
                this.handleError(call, errorResponse);
                break;
        }
    }

    protected void handleComplete(Call<T> call) {

    }

    protected abstract void handleFailure(Call<T> call, Throwable throwable);

    protected abstract void handleError(Call<T> call, ErrorResponse errorResponse);

    protected abstract void handleSuccess(Response<T> response);

}
