package com.pronovoscm.model.response.projectteam;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Team implements Cloneable {

    @SerializedName("discipline")
    @Expose
    private String discipline;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("is_client")
    @Expose
    private Integer isClient;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("contacts")
    @Expose
    private List<Contact> contacts = new ArrayList<>();

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getIsClient() {
        return isClient;
    }

    public void setIsClient(Integer isClient) {
        this.isClient = isClient;
    }

    public void setIsClient(int isClient) {
        this.isClient = isClient;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public Team clone() throws CloneNotSupportedException {
        Team t = new Team();

        t.setAddress(this.address);
        t.setCompany(this.company);
        t.setDiscipline(this.discipline);

        t.setOrder(this.order);
        t.setIsClient(this.isClient);
        t.setPhone(this.phone);
        t.setContacts(new ArrayList<>());
        for (Contact c : contacts) {
            t.getContacts().add(c.clone());
        }
        // t.setContacts(this.contacts);

        return t;

    }

    @Override
    public String toString() {
        return "Team{" +
                "discipline='" + discipline + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}
