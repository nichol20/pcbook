package com.github.nichol20.pcbook.service;

import com.github.nichol20.pcbook.pb.CreateLaptopRequest;
import com.github.nichol20.pcbook.pb.CreateLaptopResponse;
import com.github.nichol20.pcbook.pb.Laptop;
import com.github.nichol20.pcbook.pb.LaptopServiceGrpc;
import com.github.nichol20.pcbook.sample.Generator;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class LaptopServerTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule(); // automatic graceful shutdown channel at the end of test

    private LaptopStore laptopStore;
    private LaptopServer server;
    private ManagedChannel channel;

    @Before
    public void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        InProcessServerBuilder serverBuilder = InProcessServerBuilder.forName(serverName).directExecutor();

        laptopStore = new InMemoryLaptopStore();
        server = new LaptopServer(serverBuilder, 0, laptopStore);
        server.start();

        channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build()
        );
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void createLaptopWithAValidID() {
        Generator generator = new Generator();
        Laptop laptop = generator.NewLaptop();
        CreateLaptopRequest request = CreateLaptopRequest.newBuilder().setLaptop(laptop).build();

        LaptopServiceGrpc.LaptopServiceBlockingStub stub = LaptopServiceGrpc.newBlockingStub(channel);
        CreateLaptopResponse response = stub.createLaptop(request);
        assertNotNull(response);
        assertEquals(laptop.getId(), response.getId());

        Laptop found = laptopStore.Find(response.getId());
        assertNotNull(found);
    }

    @Test
    public void createLaptopWithAnEmptyID() {
        Generator generator = new Generator();
        Laptop laptop = generator.NewLaptop().toBuilder().setId("").build();
        CreateLaptopRequest request = CreateLaptopRequest.newBuilder().setLaptop(laptop).build();

        LaptopServiceGrpc.LaptopServiceBlockingStub stub = LaptopServiceGrpc.newBlockingStub(channel);
        CreateLaptopResponse response = stub.createLaptop(request);
        assertNotNull(response);
        assertFalse(response.getId().isEmpty());

        Laptop found = laptopStore.Find(response.getId());
        assertNotNull(found);
    }

    @Test(expected = StatusRuntimeException.class)
    public void createLaptopWithAnInvalidID() {
        Generator generator = new Generator();
        Laptop laptop = generator.NewLaptop().toBuilder().setId("invalid").build();
        CreateLaptopRequest request = CreateLaptopRequest.newBuilder().setLaptop(laptop).build();

        LaptopServiceGrpc.LaptopServiceBlockingStub stub = LaptopServiceGrpc.newBlockingStub(channel);
        CreateLaptopResponse response = stub.createLaptop(request);
    }

    @Test(expected = StatusRuntimeException.class)
    public void createLaptopWithAnAlreadyExistsID() throws Exception {
        Generator generator = new Generator();
        Laptop laptop = generator.NewLaptop();
        laptopStore.Save(laptop);
        CreateLaptopRequest request = CreateLaptopRequest.newBuilder().setLaptop(laptop).build();

        LaptopServiceGrpc.LaptopServiceBlockingStub stub = LaptopServiceGrpc.newBlockingStub(channel);
        CreateLaptopResponse response = stub.createLaptop(request);
    }

}