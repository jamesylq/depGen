package com.example.depgen.view.components

import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.depgen.R
import com.example.depgen.ctxt
import com.example.depgen.model.OnBoardModel
import com.example.depgen.navController
import com.example.depgen.view.fragments.OnBoardItem

@Composable
fun OnboardingScreen() {
    val pages = listOf(
        OnBoardModel(
            title = "Welcome to depGen!",
            description = "This is where you can find all the features of depGen!",
            imageRes = R.drawable.onboarding_1
        ),
        OnBoardModel(
            title = "Accessibility",
            description = "All your deployment data - just a few clicks away!",
            imageRes = R.drawable.onboarding_2
        ),
        OnBoardModel(
            title = "Customisability",
            description = "Customise your own account however you like!",
            imageRes = R.drawable.onboarding_3
        ),
        OnBoardModel(
            title = "Robustness",
            description = "Our One-Time and Repeating Deployment pages can adapt to any situation you provide it with!",
            imageRes = R.drawable.onboarding_4
        ),
        OnBoardModel(
            title = "Convenience",
            description = "Export to Excel file for ease of sharing!",
            imageRes = R.drawable.onboarding_5
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size + 1 })

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == pages.size) {
            getDefaultSharedPreferences(ctxt).edit().apply {
                putBoolean("COMPLETED_ONBOARDING", true)
                commit()
            }
            navController.navigate("Login")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            if (page < pages.size) {
                OnBoardItem(pages[page])
            } else {
                Column (
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.fake_login), "",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)

        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .width(if (isSelected) 18.dp else 8.dp)
                            .height(8.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF707784),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                color = if (isSelected) Color(0xFF3B6C64) else Color(0xFFFFFFFF),
                                shape = CircleShape
                            )
                    )
                }
            }

        }
    }
}