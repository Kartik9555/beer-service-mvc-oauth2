package com.example.practice.beerservicemvc.bootstrap;

import com.example.practice.beerservicemvc.entities.Beer;
import com.example.practice.beerservicemvc.entities.Customer;
import com.example.practice.beerservicemvc.model.BeerCSVRecord;
import com.example.practice.beerservicemvc.model.BeerStyle;
import com.example.practice.beerservicemvc.repository.BeerRepository;
import com.example.practice.beerservicemvc.repository.CustomerRepository;
import com.example.practice.beerservicemvc.service.BeerCsvService;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final BeerCsvService beerCsvService;


    @Transactional
    @Override
    public void run(String... args) throws Exception {
        loadBeerData();
        loadCsvData();
        loadCustomerData();
    }


    private void loadCsvData() throws FileNotFoundException {
        if (beerRepository.count() < 10) {
            File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
            List<BeerCSVRecord> records = beerCsvService.convertCSV(file);
            records.forEach(record -> {
                BeerStyle beerStyle = switch (record.getStyle()) {
                    case "American Pale Lager" -> BeerStyle.LAGER;
                    case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" -> BeerStyle.ALE;
                    case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
                    case "American Porter" -> BeerStyle.PORTER;
                    case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
                    case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                    case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                    case "English Pale Ale" -> BeerStyle.PALE_ALE;
                    default -> BeerStyle.PILSNER;
                };
                beerRepository.save(
                    Beer.builder()
                        .name(StringUtils.abbreviate(record.getBeer(), 50))
                        .beerStyle(beerStyle)
                        .upc(record.getRow().toString())
                        .quantityOnHand(record.getCount())
                        .price(BigDecimal.TEN)
                        .build()
                );
            });
        }
    }


    private void loadBeerData() {
        Beer beer1 = Beer.builder()
            .name("Galaxy Cat")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("12345")
            .price(new BigDecimal("12.99"))
            .quantityOnHand(122)
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .build();

        Beer beer2 = Beer.builder()
            .name("Crank")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123456222")
            .price(new BigDecimal("11.99"))
            .quantityOnHand(392)
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .build();

        Beer beer3 = Beer.builder()
            .name("Sunshine City")
            .beerStyle(BeerStyle.IPA)
            .upc("12356")
            .price(new BigDecimal("13.99"))
            .quantityOnHand(144)
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .build();

        beerRepository.saveAll(Arrays.asList(beer1, beer2, beer3));
    }


    private void loadCustomerData() {
        Customer customer1 = Customer.builder()
            .name("Max Muller")
            .email("max.muller@gmail.com")
            .createdDate(LocalDateTime.now())
            .lastModifiedDate(LocalDateTime.now())
            .build();

        Customer customer2 = Customer.builder()
            .name("Henry Ford")
            .email("henry.ford@yahoo.com")
            .createdDate(LocalDateTime.now())
            .lastModifiedDate(LocalDateTime.now())
            .build();

        Customer customer3 = Customer.builder()
            .name("Pam Hilton")
            .email("paris.hilton@gmail.com")
            .createdDate(LocalDateTime.now())
            .lastModifiedDate(LocalDateTime.now())
            .build();

        customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
    }
}
