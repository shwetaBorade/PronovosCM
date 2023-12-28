package com.pronovoscm.api;

import com.pronovoscm.model.request.inventory.InventoryRequest;
import com.pronovoscm.model.response.inventorycategories.InventoryCategoriesResponse;
import com.pronovoscm.model.response.inventoryequipment.InventoryEquipmentResponse;
import com.pronovoscm.model.response.inventoryresponse.InvRes;
import com.pronovoscm.model.response.inventorysubcategories.InventorySubCategoriesResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface InventoryApi {

    /**
     * @param header
     * @return
     */

    @POST("transfer/categories")
    Call<InventoryCategoriesResponse> getInventoryCategories(@HeaderMap HashMap<String, String> header);

    /**
     * @param header
     * @return
     */
    @POST("transfer/subcategories")
    Call<InventorySubCategoriesResponse> getInventorySubCategories(@HeaderMap HashMap<String, String> header);

    /**
     * @param header
     * @return
     */
    @POST("transfer/equipments")
    Call<InventoryEquipmentResponse> getEquipmentDetails(@HeaderMap HashMap<String, String> header);

    /**
     * @param header
     * @return
     */
    @POST("transfer/inventory")
    Call<InvRes> getInventory(@HeaderMap HashMap<String, String> header, @Body InventoryRequest inventoryRequest);

}
