package com.mubir.order.bootstrap;

import com.mubir.order.domain.Customer;
import com.mubir.order.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CarOrderBootstrap implements CommandLineRunner
{
    public static final String TASTING_ROOM = "Tasting Room";
    public static final String CAR_1_UPC = "0631234200036";
    public static final String CAR_2_UPC = "0631234300019";
    public static final String CAR_3_UPC = "0083783375213";

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            customerRepository.save(Customer.builder()
                    .customerName(TASTING_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());
        }
    }
}
