package com.example.bloodbank.DataModels;

public class DonorModel {
    private String name,department,batch,district,number,blood_group,photo;
    public DonorModel() {
        // Default constructor is required by Firebase.
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public DonorModel(String name, String department, String batch, String district, String number, String blood_group, String photo) {
        this.name = name;
        this.department = department;
        this.batch = batch;
        this.district = district;
        this.number = number;
        this.blood_group = blood_group;
        this.photo = photo;
    }
}
