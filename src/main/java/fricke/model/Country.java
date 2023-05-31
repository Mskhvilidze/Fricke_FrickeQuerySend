package fricke.model;

import java.util.Arrays;

public class Country {

    private final String[] countries;
    private static final Country newInstance = new Country();

    private Country() {
        countries = new String[]{
                "DE",
                "AT", "BE", "CH", "CH-", "CZ", "SK", "DK", "ES", "PT", "FR", "FR-", "HU", "IT", "NL", "NO", "NO-", "PL", "RO",
                "SE", "EE", "LT", "LV", "GB", "GB-", "IE", "IE-"
        };
    }

    public String[] getCountries() {
        Arrays.sort(countries);
        return countries;
    }

    public static Country getInstance() {
        return newInstance;
    }
} 
