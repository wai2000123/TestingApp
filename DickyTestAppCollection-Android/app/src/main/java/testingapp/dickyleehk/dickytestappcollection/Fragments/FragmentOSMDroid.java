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
import android.widget.FrameLayout;
import android.widget.TextView;

import org.osmdroid.api.IMap;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import testingapp.dickyleehk.dickytestappcollection.R;

/**
 * Created by dickyleehk on 6/8/15.
 */
public class FragmentOSMDroid extends Fragment {

    static String TAG = "FragmentOSMDroid";


    private TextView tvLat = null;
    private TextView tvLong = null;

    public static Fragment newInstance(Context context) {
        FragmentOSMDroid f = new FragmentOSMDroid();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_osmdroid, null);
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
    }

    private void initNormalMap() {

        ResourceProxyImpl mResourceProxy = new ResourceProxyImpl(getActivity().getApplicationContext());
        MapView mMapView = new MapView(getActivity(), 256, mResourceProxy);

        FrameLayout fl = (FrameLayout)getView().findViewById(R.id.osmdroid_map_container);
        fl.addView(mMapView);

        //GPS
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location loc) {

                String latitude = "Latitude: " + loc.getLatitude();
//                Log.v(TAG, latitude);
                String longitude = "Longitude: " + loc.getLongitude();
//                Log.v(TAG, longitude);
                if (tvLat != null) tvLat.setText(latitude);
                if (tvLong != null) tvLong.setText(longitude);


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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Current Geo:" + cityName);
                /*-------------------------------------*/

//                updateSelfLocation(loc);
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

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String pv = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(pv, 500, 1, locationListener);
    }
}
