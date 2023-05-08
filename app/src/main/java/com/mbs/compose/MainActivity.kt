package com.mbs.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mbs.compose.composables.InputField
import com.mbs.compose.composables.RoundIconButton
import com.mbs.compose.ui.theme.ComposeTheme
import com.mbs.compose.util.calculateTotalPerPerson
import com.mbs.compose.util.calculateTotalTip

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    ComposeTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column() {
                content()
            }
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Float) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(all = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson).replace(",", ".")

            Text(
                text = "Total per person",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Preview
@Composable
fun MainContent() {
    BillForm()
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    var splitValue by remember {
        mutableStateOf(1)
    }
    var sliderPositionState by remember {
        mutableStateOf(0f)
    }
    val tip = remember(totalBillState.value, sliderPositionState) {
        if (totalBillState.value.isNotEmpty()) {
            calculateTotalTip(
                totalBillState.value.toFloat(),
                (sliderPositionState * 100).toInt()
            )
        } else 0f
    }
    val totalPerPerson = remember(totalBillState.value, sliderPositionState, splitValue) {
        if (totalBillState.value.isNotEmpty()) {
            calculateTotalPerPerson(
                totalBill = totalBillState.value.toFloat(),
                splitBy = splitValue,
                tipPercentage = (sliderPositionState * 100).toInt()
            )
        } else 0.0f
    }

    TopHeader(totalPerPerson)

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            InputField(
                modifier = Modifier.fillMaxWidth(),
                valueState = totalBillState,
                labelId = "Enter Bill",
                isSingleLine = true,
                keyboardType = KeyboardType.Number,
                onActions = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValueChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            if (validState) {


                Row(
                    Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove,
                            tint = MaterialTheme.colorScheme.onBackground,
                            onClick = {
                                if (splitValue > 1) splitValue--
                            })

                        Text(
                            text = "$splitValue",
                            modifier = Modifier
                                .align(CenterVertically)
                                .padding(horizontal = 9.dp)
                        )

                        RoundIconButton(imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onBackground,
                            onClick = { splitValue++ })
                    }
                }
                Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = "$${"%.2f".format(tip)}",
                        modifier = Modifier.align(CenterVertically)
                    )
                }
                Column(
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${(sliderPositionState * 100).toInt()}%"
                    )
                    Spacer(modifier = Modifier.width(14.dp))

                    Slider(
                        value = sliderPositionState, onValueChange = { newValue ->
                            sliderPositionState = newValue
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}













