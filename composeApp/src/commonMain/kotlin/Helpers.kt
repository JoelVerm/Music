import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

@Composable
fun <T> rememberDerived(dep: Any?, derived: () -> T): State<T> = remember(dep) { derivedStateOf(derived) }

fun Int.timeStamp(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "$minutes:${if (seconds < 10) "0" else ""}$seconds"
}

fun String.cutoff(length: Int): String {
    return if (this.length > length) {
        this.substring(0, length - 3) + "..."
    } else {
        this
    }
}