package testingapp.dickyleehk.dickytestappcollection.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import testingapp.dickyleehk.dickytestappcollection.R;

/**
 * Created by dickyleehk on 6/8/15.
 */
public class FragmentVideoBgView extends Fragment {

    static String TAG = "FragmentStreamingVideo";

    VideoView vidView = null;

    public static Fragment newInstance(Context context) {
        FragmentVideoBgView f = new FragmentVideoBgView();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_streaming, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
       initVideoPlayer();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    private void initVideoPlayer(){
        vidView = (VideoView)getActivity().findViewById(R.id.myVideo);

        String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        Uri vidUri = Uri.parse(vidAddress);

        setDimension(1.5f);

        vidView.setVideoURI(vidUri);
        vidView.start();
    }

    private void setDimension(float scale) {
        // Adjust the size of the video
        // so it fits on the screen
        float videoProportion = scale;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        float screenProportion = (float) screenHeight / (float) screenWidth;
        ViewGroup.LayoutParams lp = vidView.getLayoutParams();

        if (videoProportion < screenProportion) {
            lp.height= screenHeight;
            lp.width = (int) ((float) screenHeight / videoProportion);
        } else {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth * videoProportion);
        }
        vidView.setLayoutParams(lp);
    }
}
