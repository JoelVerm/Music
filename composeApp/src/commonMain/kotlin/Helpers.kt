import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.reflect.KProperty

@Composable
fun <T> rememberDerived(dep: Any?, derived: () -> T): State<T> = remember(dep) { derivedStateOf(derived) }

interface RememberWith<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)

}

@Composable
fun <T> rememberWith(it: T, set: (T) -> Unit) = rememberWith(it, it, set)
@Composable
fun <T> rememberWith(key: Any?, it: T, set: (T) -> Unit): RememberWith<T> {
    val state = remember(key) { mutableStateOf(it) }
    return object: RememberWith<T> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = state.getValue(thisRef, property)
        override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            state.setValue(thisRef, property, value)
            set(value)
        }
    }
}

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