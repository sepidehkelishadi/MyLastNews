package com.pishtaz.mylastnews.ui.fragments.register;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pishtaz.mylastnews.R;


/**
 * Created by sepideh on 3/31/2015.
 */
public class RoleFragment extends Fragment {

    WebView webViewRoles;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null) {
            view = inflater.inflate(R.layout.role_fragment, container, false);
            webViewRoles = (WebView) view.findViewById(R.id.webViewRoles);

            webViewRoles.getSettings().setBuiltInZoomControls(true);
            webViewRoles.getSettings().setLoadWithOverviewMode(true);
            webViewRoles.getSettings().setUseWideViewPort(true);
            webViewRoles.setWebViewClient(new myWebClient());
            webViewRoles.loadUrl("file:///android_asset/html/roles.html");
        }else{

        }
        return view;
    }

    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);

        }
    }


}