package com.pronovoscm.model.response.transfercontacts;

import com.google.gson.annotations.SerializedName;

public class Contacts {
        @SerializedName("phone1")
        private String phone1;
        @SerializedName("phone")
        private String phone_no;
        @SerializedName("name")
        private String name;

        public String getPhone_no() {
            return phone_no;
        }

        public void setPhone_no(String phone_no) {
            this.phone_no = phone_no;
        }

        @SerializedName("users_id")
        private int usersId;

        public String getPhone1() {
            return phone1;
        }

        public void setPhone1(String phone1) {
            this.phone1 = phone1;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getUsersId() {
            return usersId;
        }

        public void setUsersId(int usersId) {
            this.usersId = usersId;
        }
    }
