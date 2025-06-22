package com.example.myapplication.data.model;

import java.io.Serializable;
public class TrafficSign implements Serializable{
    private String id;
    private String name;
    private String description;
    private String image;
    private String type;
    private String status; // "not_started", "in_progress", "learned", "studying"

    public TrafficSign(String id, String name, String description, String image, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.image = image;
        this.description = description;
        this.status = "not_started";
    }

    public TrafficSign(String id, String name, String description, String image, String type, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.image = image;
        this.description = description;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Định nghĩa loại câu hỏi
    public enum QuestionType {
        DEFINITION_TO_TERM, // Định nghĩa -> Tên
        TERM_TO_DEFINITION, // Tên -> Định nghĩa
        IMAGE_TO_TERM,      // Hình ảnh -> Tên
        TERM_TO_IMAGE,      // Tên -> Hình ảnh
        IMAGE, IMAGE_TO_DEFINITION // Hình ảnh -> Định nghĩa
    }


}
