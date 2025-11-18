import androidx.compose.web.renderComposable
import org.jetbrains.compose.web.dom.*

fun main() {
    renderComposable(rootElementId = "root") {
        Calculator()
    }
}
