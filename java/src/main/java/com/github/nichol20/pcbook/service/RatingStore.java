package com.github.nichol20.pcbook.service;

public interface RatingStore {
    Rating Add(String laptopID, double score);
}
