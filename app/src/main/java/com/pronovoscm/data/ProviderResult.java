package com.pronovoscm.data;

/**
 * Models a result from a data provider.
 */
public interface ProviderResult<T> {

    void success(T result);

    void AccessTokenFailure(String message);

    void failure(String message);

}
