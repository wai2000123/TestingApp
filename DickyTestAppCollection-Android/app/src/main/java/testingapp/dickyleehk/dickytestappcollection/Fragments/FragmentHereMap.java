package testingapp.dickyleehk.dickytestappcollection.Fragments;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import testingapp.dickyleehk.dickytestappcollection.R;

/**
 * Created by dickyleehk on 6/8/15.
 */
public class FragmentHereMap extends Fragment {

    static String TAG = "FragmentHereMap";

    // map embedded in the map fragment
    private Map map = null;
    // map fragment embedded in this activity
    private MapFragment mapFragment = null;
    private MapMarker selfMarker = null;

    private TextView tvLat = null;
    private TextView tvLong = null;

    public static Fragment newInstance(Context context) {
        FragmentHereMap f = new FragmentHereMap();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_heremap, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        tvLat = (TextView) getView().findViewById(R.id.tvLat);
        tvLong = (TextView) getView().findViewById(R.id.tvLong);
        initNormalMap();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
    }

    private void initNormalMap(){
        mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();
                    // Set the map center coordinate to the Vancouver region (no animation)
                    map.setCenter(new GeoCoordinate(22.3593252, 114.1408686, 0.0), //Hong Kong
                            Map.Animation.NONE);
                    // Set the map zoom level to the average between min and max (no animation)
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);

                    //updateSelfLocation(selfLoc);
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });

        //Map
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location loc) {

                String latitude = "Latitude: " + loc.getLatitude();
//                Log.v(TAG, latitude);
                String longitude = "Longitude: " + loc.getLongitude();
//                Log.v(TAG, longitude);
                if(tvLat != null)tvLat.setText(latitude);
                if(tvLong != null)tvLong.setText(longitude);


                 /*------- To get city name from coordinates -------- */
                //Currently not working
                String cityName = null;
                Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(loc.getLatitude(),
                            loc.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        System.out.println(addresses.get(0).getLocality());
                        cityName = addresses.get(0).getLocality();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Current Geo:" + cityName);
                /*-------------------------------------*/

                updateSelfLocation(loc);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "ProviderDisabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "ProviderEnabled");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "StatusChanged");
            }
        };

        Criteria criteria= new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String pv = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(pv, 500, 1, locationListener);
    }


    public void updateSelfLocation(Location loc){;
        if(map==null){
            return;
        }

        if (selfMarker == null) {
            selfMarker = new MapMarker();
            map.addMapObject(selfMarker);
        }

        if (selfMarker != null) {
            selfMarker.setCoordinate(new GeoCoordinate(loc.getLatitude(), loc.getLongitude()));
        }
    }
}
