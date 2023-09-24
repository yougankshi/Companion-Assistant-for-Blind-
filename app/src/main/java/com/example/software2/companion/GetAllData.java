package com.example.software2.companion;
import android.app.IntentService; //IntentService is a base class for services that handle asynchronous requests using Intent messages.
import android.content.Intent;  //Intent is a messaging object that is used to communicate between components in an Android application.
import android.location.Address;  //Address represents a geographical address, including a street address, city, and country.
import android.location.Geocoder;  //Geocoder is a class for handling geocoding and reverse geocoding, which is the process of converting between a location's latitude and longitude coordinates and its corresponding address.
import android.location.Location; //Location represents a geographic location that can be obtained from a GPS, network location provider, or other sources.
import android.os.Bundle; //Bundle is a container for passing data between activities in an Android application.
import android.os.ResultReceiver; //ResultReceiver is a base class for an intent service that can send results back to the calling component.
import android.speech.tts.TextToSpeech; //TextToSpeech is a class that provides text-to-speech functionality for Android applications.
import android.util.Log; //Log is a utility class for logging messages to the system log.
import java.util.List; //List is an interface that represents an ordered collection of elements.
import java.util.Locale; //Locale represents a specific geographic, cultural, or linguistic region.
import java.util.Objects; //Objects provides utility methods for working with objects, such as checking for null values.
import androidx.annotation.Nullable; //Nullable is an annotation that indicates that a parameter, return value, or field can be null.
public class GetAllData extends IntentService {
    private static final String IDENTIFIER = "GetAddressIntentService";
    private ResultReceiver addressResultReceiver;
    private TextToSpeech texttospeech;

    public GetAllData() {
        super(IDENTIFIER);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //onHandleIntent is a intent service for passing the data
        String msg;
        addressResultReceiver = Objects.requireNonNull(intent).getParcelableExtra("add_receiver");
        if (addressResultReceiver == null) {
            Log.e("GetAddressIntentService", "No receiver, not processing the request further");
            return;
        }
        Location location = intent.getParcelableExtra("add_location");
        if (location == null) {
            msg = "No location, can't go further without location";
            texttospeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
            sendResultsToReceiver(0, msg);
            return;
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch (Exception ioException) {
            Log.e("", "Error in getting address for the location");
        }
        if (addresses == null || addresses.size() == 0) {
            msg = "No address found for the location";
            sendResultsToReceiver(1, msg);
        }
        else {
            Address address = addresses.get(0);
            String addressDetails = address.getFeatureName() +"."+  "\n" +
                    "Locality is, " + address.getLocality() + "."+ "\n" + "City is ," + address.getSubAdminArea()+"." + "\n" +
                    "State is, " + address.getAdminArea()+"."+ "\n" + "Country is, " + address.getCountryName() +"."+ "\n";
            sendResultsToReceiver(2, addressDetails);
        }
    }
    private void sendResultsToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString("address_result", message);
        addressResultReceiver.send(resultCode, bundle);
    }
}