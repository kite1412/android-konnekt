package nrr.konnekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import nrr.konnekt.designsystem.theme.KonnektTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KonnektTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        Greeting()
                        Greeting(
                            textStyle = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Greeting(
                            textStyle = LocalTextStyle.current.copy(
                                fontStyle = FontStyle.Italic
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current
) {
    Text(
        text = "Hello Android!",
        modifier = modifier,
        style = textStyle
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KonnektTheme {
        Greeting()
    }
}