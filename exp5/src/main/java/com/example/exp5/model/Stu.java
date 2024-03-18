package com.example.exp5.model;

public class Stu {
    private String name;
    private String tel;
    private String id;
    private String img;
    private boolean isUpdate;

    public boolean isUpdate() {
        // 返回是否更新的标志
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public Stu() {
    }

    public Stu(String name, String tel, String id,String img) {
        this.name = name;
        this.tel = tel;
        this.id = id;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Stu{" +
                "name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", id='" + id + '\'' +
                ", img='" + img + '\'' +
                '}';
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
