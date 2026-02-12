package com.example.app.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoIpService {

    private final RestTemplate restTemplate;
    private final @NonNull String apiBaseUrl;

    public GeoIpService(
        RestTemplateBuilder restTemplateBuilder,
        @Value("${geoip.api.base-url:http://ip-api.com/json}") String apiBaseUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.apiBaseUrl = Objects.requireNonNull(apiBaseUrl, "geoip.api.base-url must not be null");
    }

    public GeoIpData resolve(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return GeoIpData.unknown();
        }

        String url = "%s/%s?fields=status,country,city,regionName,lat,lon,timezone".formatted(apiBaseUrl, ipAddress);
        try {
            GeoIpApiResponse response = restTemplate.getForObject(Objects.requireNonNull(url), GeoIpApiResponse.class);
            if (response == null || !"success".equalsIgnoreCase(response.getStatus())) {
                return GeoIpData.unknown();
            }
            return new GeoIpData(
                safe(response.getCountry()),
                safe(response.getCity()),
                safe(response.getRegionName()),
                response.getLat() != null ? String.valueOf(response.getLat()) : "Unknown",
                response.getLon() != null ? String.valueOf(response.getLon()) : "Unknown",
                safe(response.getTimezone())
            );
        } catch (RestClientException e) {
            return GeoIpData.unknown();
        }
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Unknown" : value;
    }

    public record GeoIpData(
        String country,
        String city,
        String region,
        String latitude,
        String longitude,
        String timezone
    ) {
        public static GeoIpData unknown() {
            return new GeoIpData("Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown");
        }
    }

    public static final class GeoIpApiResponse {
        private String status;
        private String country;
        private String city;
        private String regionName;
        private Double lat;
        private Double lon;
        private String timezone;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLon() {
            return lon;
        }

        public void setLon(Double lon) {
            this.lon = lon;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
    }
}
