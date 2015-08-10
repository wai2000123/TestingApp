package testingapp.dickyleehk.dickytestappcollection.Fragments;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapDataStore;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import testingapp.dickyleehk.dickytestappcollection.R;

/**
 * Created by dickyleehk on 6/8/15.
 */
public class FragmentMapsforge extends Fragment {

    static String TAG = "FragmentMapsforge";

    // name of the map file in the external storage
    private static final String MAPFILE = "Download/saarland.map";

    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;

    private TextView tvLat = null;
    private TextView tvLong = null;

    public static Fragment newInstance(Context context) {
        FragmentMapsforge f = new FragmentMapsforge();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_mapsforge, null);
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
        mapView.destroy();
    }

    private void initNormalMap(){

        AndroidGraphicFactory.createInstance(getActivity().getApplication());

        this.mapView = new MapView(getActivity());
        FrameLayout fl = (FrameLayout)getView().findViewById(R.id.mapsforge_map_container);
        fl.addView(mapView);

        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
        this.mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

        // create a tile cache of suitable size
        this.tileCache = AndroidUtil.createTileCache(getActivity(), "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());
        this.mapView.getModel().mapViewPosition.setCenter(new LatLong(49.375677, 6.8810961));
        this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 12);

        // tile renderer layer using internal render theme
        MapDataStore mapDataStore = new MapFile(getMapFile());
        this.tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                this.mapView.getModel().mapViewPosition, false, true, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        // only once a layer is associated with a mapView the rendering starts
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);


        //GPS
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

        Criteria criteria= new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String pv = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(pv, 500, 1, locationListener);
    }


//    public void updateSelfLocation(Location loc){;
//        if(map==null){
//            return;
//        }
//
//        if (selfMarker == null) {
//            selfMarker = new MapMarker();
//            map.addMapObject(selfMarker);
//        }
//
//        if (selfMarker != null) {
//            selfMarker.setCoordinate(new GeoCoordinate(loc.getLatitude(), loc.getLongitude()));
//        }
//    }

    private File getMapFile() {
        File file = new File(Environment.getExternalStorageDirectory(), MAPFILE);
        return file;
    }
}
