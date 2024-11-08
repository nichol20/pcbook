package com.github.nichol20.pcbook.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface ImageStore {
    String Save(String laptopID, String imageType, ByteArrayOutputStream ImageData) throws IOException;
}
