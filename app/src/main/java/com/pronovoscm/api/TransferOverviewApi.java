package com.pronovoscm.api;

import com.pronovoscm.model.request.createtransfer.CreateTransferRequest;
import com.pronovoscm.model.request.deleteequipment.DeleteEquipmentRequest;
import com.pronovoscm.model.request.transfercontact.TransferContactRequest;
import com.pronovoscm.model.request.transferdetails.TransferDetailRequest;
import com.pronovoscm.model.request.transferlocation.TransferLocationRequest;
import com.pronovoscm.model.request.transferoverview.TransferOverviewRequest;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.model.response.createtransfer.CreateTransferResponse;
import com.pronovoscm.model.response.deleteequipment.DeleteEquipmentResponse;
import com.pronovoscm.model.response.transfercontacts.TransferContactsResponse;
import com.pronovoscm.model.response.transferdelete.TransferDeleteResponse;
import com.pronovoscm.model.response.transferdetail.TransferDetailResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationVendorResponse;
import com.pronovoscm.model.response.transferoverview.TransferOverviewResponse;
import com.pronovoscm.model.response.transferoverviewcount.TransferOverviewCountResponse;
import com.pronovoscm.model.response.transferrequest.TransferRequestResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface TransferOverviewApi {

    @POST("project/transfers")
    Call<TransferOverviewResponse> getTransferOverview(@HeaderMap HashMap<String, String> header, @Body TransferOverviewRequest transferOverviewRequest);

    @POST("project/transfer/overview")
    Call<TransferOverviewCountResponse> getTransferOverviewNew(@HeaderMap HashMap<String, String> header, @Body TransferOverviewRequest transferOverviewRequest);


    @POST("project/transfer/locations")
    Call<TransferLocationResponse> getTransferLocation(@HeaderMap HashMap<String, String> header, @Body TransferLocationRequest transferOverviewRequest);

    @POST("project/transfer/locations")
    Call<TransferLocationVendorResponse> getTransferLocationVendor(@HeaderMap HashMap<String, String> header, @Body TransferLocationRequest transferOverviewRequest);

    @POST("project/transfer/contacts")
    Call<TransferContactsResponse> getTransferContacts(@HeaderMap HashMap<String, String> header, @Body TransferContactRequest transferOverviewRequest);

    @POST("project/create/request")
    Call<TransferRequestResponse> callTransferRequest(@HeaderMap HashMap<String, String> header, @Body TransferRequest transferOverviewRequest);

    @POST("project/create/transfer")
    Call<CreateTransferResponse> callCreateTransfer(@HeaderMap HashMap<String, String> header, @Body CreateTransferRequest transferOverviewRequest);

    @POST("transfer/details")
    Call<TransferDetailResponse> callTransferDetailRequest(@HeaderMap HashMap<String, String> header, @Body TransferDetailRequest transferOverviewRequest);

    @POST("project/delete/transfer")
    Call<TransferDeleteResponse> callTransferDelete(@HeaderMap HashMap<String, String> header, @Body TransferDetailRequest transferOverviewRequest);

    @POST("project/delete/equipment")
    Call<DeleteEquipmentResponse> callDeleteEquipment(@HeaderMap HashMap<String, String> header, @Body DeleteEquipmentRequest transferOverviewRequest);

}
