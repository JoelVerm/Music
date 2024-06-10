import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

actual fun log(tag: String, message: String) {
    Log.d(tag, message)
}

@Composable
actual fun requestPermissions(): State<Boolean> {
    val granted = remember { mutableStateOf(false) }
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        listOf(
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.FOREGROUND_SERVICE,
            android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK
        )
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        listOf(
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.FOREGROUND_SERVICE
        )
    else listOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.FOREGROUND_SERVICE
    )
    val ctx = LocalContext.current
    if (permissions.all {ctx.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED })
        granted.value = true
    else {
        var permissionsGrantedTotal = 0
        val requestPermissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    permissionsGrantedTotal++
                    if (permissionsGrantedTotal == permissions.size)
                        granted.value = true
                } else
                    Toast.makeText(
                        ctx,
                        "You need this permission for playing music",
                        Toast.LENGTH_LONG
                    ).show()
            }
        LaunchedEffect(Unit) {
            permissions.map { requestPermissionLauncher.launch(it) }
        }
    }
    return granted
}