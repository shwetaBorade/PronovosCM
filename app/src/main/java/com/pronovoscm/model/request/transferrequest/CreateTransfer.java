package com.pronovoscm.model.request.transferrequest;

import com.pronovoscm.model.response.transfercontacts.Contacts;
import com.pronovoscm.model.response.transfercontacts.TransferContactsResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationVendorResponse;

import java.util.Calendar;

public class CreateTransfer {

    private String pickUpDate;
    private Calendar pickUpCalendar;
    private Calendar dropoffCalendar;
    private String pickUpTime;
    private boolean pickUpIsJobSite;
    private Contacts pickUpContacts;
    private Contacts dropOffContacts;
    private String dropOffDate;
    private String dropOffTime;
    private boolean dropOffIsJobSite;
    private TransferLocationResponse.Locations dropOffLocation;
    private TransferLocationResponse.Locations pickUpLocation;
    private TransferLocationVendorResponse.Locations pickUpVendorLocation;
    private TransferLocationVendorResponse.Locations dropOffVendorLocation;
    private int roundTrip;
    private int unloading;

    public int getUnloading() {
        return unloading;
    }

    public void setUnloading(int unloading) {
        this.unloading = unloading;
    }

    public CreateTransfer() {
        pickUpDate = "";
        pickUpTime = "";
        pickUpIsJobSite = true;
        dropOffDate = "";
        dropOffTime = "";
        dropOffIsJobSite = true;
    }

    public int getRoundTrip() {
        return roundTrip;
    }

    public void setRoundTrip(int roundTrip) {
        this.roundTrip = roundTrip;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public String getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public boolean isPickUpIsJobSite() {
        return pickUpIsJobSite;
    }

    public void setPickUpIsJobSite(boolean pickUpIsJobSite) {
        this.pickUpIsJobSite = pickUpIsJobSite;
    }

    public TransferLocationResponse.Locations getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(TransferLocationResponse.Locations pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public TransferLocationVendorResponse.Locations getPickUpVendorLocation() {
        return pickUpVendorLocation;
    }

    public void setPickUpVendorLocation(TransferLocationVendorResponse.Locations pickUpVendorLocation) {
        this.pickUpVendorLocation = pickUpVendorLocation;
    }

    public Contacts getPickUpContacts() {
        return pickUpContacts;
    }

    public void setPickUpContacts(Contacts pickUpContacts) {
        this.pickUpContacts = pickUpContacts;
    }

    public String getDropOffDate() {
        return dropOffDate;
    }

    public void setDropOffDate(String dropOffDate) {
        this.dropOffDate = dropOffDate;
    }

    public String getDropOffTime() {
        return dropOffTime;
    }

    public void setDropOffTime(String dropOffTime) {
        this.dropOffTime = dropOffTime;
    }

    public boolean isDropOffIsJobSite() {
        return dropOffIsJobSite;
    }

    public void setDropOffIsJobSite(boolean dropOffIsJobSite) {
        this.dropOffIsJobSite = dropOffIsJobSite;
    }

    public TransferLocationResponse.Locations getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(TransferLocationResponse.Locations dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public TransferLocationVendorResponse.Locations getDropOffVendorLocation() {
        return dropOffVendorLocation;
    }

    public void setDropOffVendorLocation(TransferLocationVendorResponse.Locations dropOffVendorLocation) {
        this.dropOffVendorLocation = dropOffVendorLocation;
    }

    public Contacts getDropOffContacts() {
        return dropOffContacts;
    }

    public void setDropOffContacts(Contacts dropOffContacts) {
        this.dropOffContacts = dropOffContacts;
    }

    public Calendar getPickUpCalendar() {
        return pickUpCalendar;
    }

    public void setPickUpCalendar(Calendar pickUpCalendar) {
        this.pickUpCalendar = pickUpCalendar;
    }

    public Calendar getDropoffCalendar() {
        return dropoffCalendar;
    }

    public void setDropoffCalendar(Calendar dropoffCalendar) {
        this.dropoffCalendar = dropoffCalendar;
    }
}
