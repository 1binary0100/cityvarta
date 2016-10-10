package com.newsvarta.dvimaniya.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.android.volley.toolbox.StringRequest;
import com.newsvarta.dvimaniya.MainActivity;
import com.newsvarta.dvimaniya.R;

/**
 * Created by Sam on 04-06-2016.
 */
public class WebviewFrag extends Fragment {

    WebView wvSourceUrl;
    ImageButton ibCross;
    LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_webview, container,false);
        wvSourceUrl = (WebView)view.findViewById(R.id.wvSourceUrl);
        ibCross = (ImageButton)view.findViewById(R.id.ibCross);
        //linearLayout = (LinearLayou
        String url = getArguments().getString("url");
        wvSourceUrl.getSettings().setJavaScriptEnabled(true);
        wvSourceUrl.getSettings().setBuiltInZoomControls(true);
        wvSourceUrl.loadUrl(url);

        ibCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                //getActivity().onBackPressed();
                getFragmentManager().popBackStack();

            }
        });
        return view;


    }


}
