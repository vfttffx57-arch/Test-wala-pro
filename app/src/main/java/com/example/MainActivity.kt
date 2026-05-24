package com.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MyApplicationTheme
import com.inmobi.ads.AdMetaInfo
import com.inmobi.ads.InMobiAdRequestStatus
import com.inmobi.ads.InMobiInterstitial
import com.inmobi.ads.listeners.InterstitialAdEventListener
import com.inmobi.sdk.InMobiSdk
import com.inmobi.sdk.SdkInitializationListener
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private val accountId = "1000200331"
    private val interstitialPlacementId = 10000707230L
    private val rewardedPlacementId = 10000707229L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val consentObject = JSONObject()
        InMobiSdk.init(
            this,
            accountId,
            consentObject,
            object : SdkInitializationListener {
                override fun onInitializationComplete(error: Error?) {
                    if (error != null) {
                        Log.e("InMobi", "Failed to init: ${error.message}")
                    } else {
                        Log.d("InMobi", "Initialized")
                    }
                }
            }
        )

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InMobiAdsTestScreen(
                        modifier = Modifier.padding(innerPadding),
                        interstitialPlacementId = interstitialPlacementId,
                        rewardedPlacementId = rewardedPlacementId
                    )
                }
            }
        }
    }
}

@Composable
fun InMobiAdsTestScreen(modifier: Modifier = Modifier, interstitialPlacementId: Long, rewardedPlacementId: Long) {
    val context = LocalContext.current
    var isInterstitialLoading by remember { mutableStateOf(false) }
    var isRewardedLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "InMobi Test",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "App Key: 1000200331\nInterstitial: $interstitialPlacementId\nRewarded: $rewardedPlacementId",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = {
                isInterstitialLoading = true
                val interstitialAd = InMobiInterstitial(context, interstitialPlacementId, object : InterstitialAdEventListener() {
                    override fun onAdLoadSucceeded(ad: InMobiInterstitial, info: AdMetaInfo) {
                        isInterstitialLoading = false
                        ad.show()
                    }

                    override fun onAdLoadFailed(ad: InMobiInterstitial, status: InMobiAdRequestStatus) {
                        isInterstitialLoading = false
                        Toast.makeText(context, "Failed: ${status.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAdDismissed(ad: InMobiInterstitial) { }
                })
                interstitialAd.load()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("show_interstitial_ad_button"),
            enabled = !isInterstitialLoading
        ) {
            Text(if (isInterstitialLoading) "Loading..." else "Show Interstitial Ad")
        }

        Button(
            onClick = {
                isRewardedLoading = true
                val rewardedAd = InMobiInterstitial(context, rewardedPlacementId, object : InterstitialAdEventListener() {
                    override fun onAdLoadSucceeded(ad: InMobiInterstitial, info: AdMetaInfo) {
                        isRewardedLoading = false
                        ad.show()
                    }

                    override fun onAdLoadFailed(ad: InMobiInterstitial, status: InMobiAdRequestStatus) {
                        isRewardedLoading = false
                        Toast.makeText(context, "Failed: ${status.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAdDismissed(ad: InMobiInterstitial) { }
                    
                    override fun onRewardsUnlocked(ad: InMobiInterstitial, rewards: Map<Any, Any>?) {
                        Toast.makeText(context, "Reward Unlocked!", Toast.LENGTH_SHORT).show()
                    }
                })
                rewardedAd.load()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("show_rewarded_ad_button"),
            enabled = !isRewardedLoading
        ) {
            Text(if (isRewardedLoading) "Loading..." else "Show Rewarded Ad")
        }
    }
}
