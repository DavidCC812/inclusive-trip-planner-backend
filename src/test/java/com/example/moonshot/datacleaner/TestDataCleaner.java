package com.example.moonshot.datacleaner;

import com.example.moonshot.accessibilityfeature.AccessibilityFeatureRepository;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.destination.DestinationRepository;
import com.example.moonshot.itinerary.ItineraryRepository;
import com.example.moonshot.itineraryaccessibility.ItineraryAccessibilityRepository;
import com.example.moonshot.itinerarystep.ItineraryStepRepository;
import com.example.moonshot.review.ReviewRepository;
import com.example.moonshot.saveditinerary.SavedItineraryRepository;
import com.example.moonshot.setting.SettingRepository;
import com.example.moonshot.user.UserRepository;
import com.example.moonshot.useraccessibilityfeature.UserAccessibilityFeatureRepository;
import com.example.moonshot.usercountryaccess.UserCountryAccessRepository;
import com.example.moonshot.userselecteddestination.UserSelectedDestinationRepository;
import com.example.moonshot.usersetting.UserSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataCleaner {

    @Autowired
    SavedItineraryRepository savedItineraryRepository;
    @Autowired
    UserAccessibilityFeatureRepository uafRepository;
    @Autowired
    UserCountryAccessRepository ucaRepository;
    @Autowired
    UserSelectedDestinationRepository usdRepository;
    @Autowired
    UserSettingRepository userSettingRepository;

    @Autowired
    ItineraryStepRepository stepRepository;
    @Autowired
    ItineraryRepository itineraryRepository;

    @Autowired
    DestinationRepository destinationRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    SettingRepository settingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItineraryAccessibilityRepository itineraryAccessibilityRepository;
    @Autowired
    AccessibilityFeatureRepository accessibilityFeatureRepository;
    @Autowired
    ReviewRepository reviewRepository;


    public void cleanAll() {
        System.out.println("Cleaning test data...");

        // Delete deepest dependencies first
        itineraryAccessibilityRepository.deleteAll();
        reviewRepository.deleteAll();
        savedItineraryRepository.deleteAll();
        stepRepository.deleteAll();

// Then intermediate tables
        userSettingRepository.deleteAll();
        uafRepository.deleteAll();
        ucaRepository.deleteAll();
        usdRepository.deleteAll();

// Then high-level
        itineraryRepository.deleteAll();
        accessibilityFeatureRepository.deleteAll();
        destinationRepository.deleteAll();
        settingRepository.deleteAll();
        countryRepository.deleteAll();
        userRepository.deleteAll();


        System.out.println("Clean complete.");
    }


}
