package testingapp.dickyleehk.dickytestappcollection.Fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.odml.MapLoader;
import com.here.android.mpa.odml.MapPackage;

import java.util.List;

import testingapp.dickyleehk.dickytestappcollection.R;

/**
 * Created by dickyleehk on 6/8/15.
 */
public class FragmentHereMapOffline extends Fragment {

    static String TAG = "FragmentHereMapOffline";

    // map embedded in the map fragment
    private Map map = null;
    // map fragment embedded in this activity
    private MapMarker selfMarker = null;

    private TextView tvLat = null;
    private TextView tvLong = null;

    public static Fragment newInstance(Context context) {
        FragmentHereMapOffline f = new FragmentHereMapOffline();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_heremap_offline, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        tvLat = (TextView) getView().findViewById(R.id.tvLat);
        tvLong = (TextView) getView().findViewById(R.id.tvLong);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    private void initOfflineMap() {

        MapLoader.Listener mapLoaderListener = new MapLoader.Listener() {
            public void onUninstallMapPackagesComplete(MapPackage rootMapPackage,
                                                       MapLoader.ResultCode mapLoaderResultCode) {
            }
            public void onProgress(int progressPercentage) {
            }
            public void onPerformMapDataUpdateComplete(MapPackage rootMapPackage,
                                                       MapLoader.ResultCode mapLoaderResultCode) {
            }
            public void onInstallationSize(long diskSize, long networkSize) {
            }
            public void onInstallMapPackagesComplete(MapPackage rootMapPackage,
                                                     MapLoader.ResultCode mapLoaderResultCode) {
            }
            public void onGetMapPackagesComplete(MapPackage rootMapPackage,
                                                 MapLoader.ResultCode mapLoaderResultCode) {
                if(mapLoaderResultCode == MapLoader.ResultCode.OPERATION_SUCCESSFUL){
                    Log.d(TAG, "Get MapPackages success");
                    Log.d(TAG, "childPackage[" + rootMapPackage.getId() + "] Title:" + rootMapPackage.getTitle() + " Size:" + rootMapPackage.getSize());
                    List<MapPackage> ls = rootMapPackage.getChildren();
                    for(MapPackage childPackage:ls){
                        Log.d(TAG,"childPackage["+childPackage.getId()+"] Title:" + childPackage.getTitle() + " Size:" + childPackage.getSize());
                    }

                    MapPackage mpk = rootMapPackage;
                    while (mpk.getChildren().size() > 0){
                        mpk = mpk.getChildren().get(0);
                    }
                    Log.d(TAG,"childPackage["+mpk.getId()+"] Title:" + mpk.getTitle() + " Size:" + mpk.getSize());

                }else{
                    Log.d(TAG, "Get MapPackages fail");
                }
            }
            public void onCheckForUpdateComplete(boolean updateAvailable,
                                                 String currentMapVersion,String newestMapVersion,
                                                 MapLoader.ResultCode mapLoaderResultCode) {
            }
        };

        MapLoader mapLoader = MapLoader.getInstance();
        mapLoader.addListener(mapLoaderListener);


        if(mapLoader.getMapPackages()){
            Log.d(TAG, "Offline map installed");
        }else{
            Log.d(TAG, "No offline map installed");
        }
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
