package com.shepherd.shepslibrary.data.model;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE");

    private final String genderStr;

    Gender(String genderStr) {
        this.genderStr = genderStr;
    }
}