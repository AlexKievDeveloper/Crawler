package com.orlovkiy2.entity;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Vinyl {
    String release;
    String artist;
    String genre;
    String price;
    String vinylLink;
    String imageLink;
}
