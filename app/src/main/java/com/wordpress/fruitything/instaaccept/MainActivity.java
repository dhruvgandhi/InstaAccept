package com.wordpress.fruitything.instaaccept;


import android.app.Dialog;
import android.content.Intent;

import android.graphics.Bitmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;



public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private  int noOfFail;
    private InterstitialAd mInterstitialAd;
    private MyJS myJS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WebView myWebView = findViewById(R.id.myWebView);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //Initialize ads
        MobileAds.initialize(this, getString(R.string.AD_APP_ID));
        //load ads
        mAdView = findViewById(R.id.adView);
        myJS = new MyJS();
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int errorCode) {
                //Toast.makeText(getApplicationContext(),"Ad failed "+errorCode,Toast.LENGTH_LONG).show();
                mAdView.loadAd(adRequest);
                Log.e("Dhruv Focus","Ad load fail:"+errorCode);
                super.onAdFailedToLoad(errorCode);
            }
        });
        noOfFail = 0;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8826047101130248/8261486834");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdFailedToLoad(int i) {
                Log.e("Dhruv Focus","Interstitial Failed:"+i);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdClosed() {
                acceptRequest();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        // Use an activity context to get the rewarded video instance.
        instantiateRewardedVideoAd();
        loadRewardedVideoAd();
        myWebView.loadUrl("https://www.instagram.com/accounts/activity?followRequests=1");

        myWebView.setWebViewClient(new WebViewClient());
        final WebSettings myWebSettings = myWebView.getSettings();
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebSettings.setJavaScriptEnabled(true);
        myWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //balanceSync.setBalance(balanceSync.getBalance());
        //to display alert
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        final Button acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRewardedVideoAd.isLoaded())
                {
                    noOfFail = 0;
                    mRewardedVideoAd.show();
                }
                else if(mInterstitialAd.isLoaded())
                {
                    noOfFail = 0;
                    mInterstitialAd.show();
                }
                else
                {
                    acceptRequest();
                }

            }
        });


        //Improve performance
        myWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        myWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        myWebSettings.setAppCacheEnabled(true);
        myWebSettings.setDomStorageEnabled(true);
        myWebSettings.setSaveFormData(true);
        myWebSettings.setSavePassword(true);
        myWebSettings.setEnableSmoothTransition(true);
        myWebSettings.setAllowFileAccess(true);



    }

    void instantiateRewardedVideoAd()
    {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView myWebView = findViewById(R.id.myWebView);

        if(myWebView.canGoBack())
        {
            myWebView.goBack();
            return true;
        }
        //if it wasnt then do default
        return super.onKeyDown(keyCode, event);
    }
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-8826047101130248/3196481541",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        noOfFail = 0;

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        acceptRequest();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        //Toast.makeText(getApplicationContext(),"Failed To load Rewarded ad "+i,Toast.LENGTH_LONG).show();
        noOfFail++;
        Log.e("Dhruv Focus","Reward video failed:"+i);
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoCompleted() {
        acceptRequest();


    }
    void loadIntrestitialAd()
    {

    }
    void acceptRequest()
    {
        Toast.makeText(getApplicationContext(),"For safety we will accept 500 requests at a time",Toast.LENGTH_LONG).show();
        WebView myWebView = findViewById(R.id.myWebView);
        myWebView.loadUrl("javascript:"+ myJS.getJS());
        //myWebView.loadUrl("javascript:"+ MyJS.giveJS("500"));
        mRewardedVideoAd.destroy(this);
        instantiateRewardedVideoAd();
        loadRewardedVideoAd();
    }

    class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            if(view.getUrl().contains("instagram")) {
                return false;
            }
            else
            {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,request.getUrl()));
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Toast.makeText(getApplicationContext(),"No Internet is Not BORING",Toast.LENGTH_LONG).show();
            view.loadUrl("file:///android_asset/2048-master/index.html");
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            if(url.contains("follow"))
            {
                if(!view.getSettings().getUserAgentString().contains("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0"))
                {
                    String newUA= "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
                    view.getSettings().setUserAgentString(newUA);
                    view.reload();
                }

                findViewById(R.id.acceptButton).setVisibility(View.VISIBLE);
            }
            else
            {
                if(view.getSettings().getUserAgentString().contains("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0"))
                {
                    view.getSettings().setUserAgentString(null);
                    view.reload();
                }

                findViewById(R.id.acceptButton).setVisibility(View.GONE);
            }
        }
    }


}
