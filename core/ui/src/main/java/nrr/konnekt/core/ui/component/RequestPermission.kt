package nrr.konnekt.core.ui.component

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestPermission(
    permission: String,
    onGranted: () -> Unit
) {
    var firstLaunch by retain {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onGranted()
    }

    LaunchedEffect(Unit) {
        if (
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
            && !firstLaunch
        ) {
            launcher.launch(permission)
            firstLaunch = true
        } else onGranted()
    }
}