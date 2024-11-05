package com.github.nichol20.pcbook.service;

import com.github.nichol20.pcbook.pb.Filter;
import com.github.nichol20.pcbook.pb.Laptop;
import io.grpc.Context;


public interface LaptopStore {
    void Save(Laptop laptop) throws Exception;
    Laptop Find(String id);
    void Search(Context ctx, Filter filter, LaptopStream stream);
}

