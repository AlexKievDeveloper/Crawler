package com.orlovkiy2.entity;

import lombok.Data;

@Data
public class Shop {
    private long id;
    private String name;
    private String linkToMainPage;
    private String linkToCatalogPage;
    private String linkToLogo;
}
