package esi.roadside.assistance.provider.main.presentation.routes.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.presentation.theme.PreviewAppTheme
import esi.roadside.assistance.provider.main.domain.models.UserNotificationModel
import esi.roadside.assistance.provider.main.presentation.components.TopAppBar
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetails(
    notification: UserNotificationModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = notification.title,
                background = R.drawable.vector_6
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(it)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                notification.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    PreviewAppTheme {
        NotificationDetails(
            notification = UserNotificationModel(
                id = "",
                title = "Mohamed",
                text = "Dear [client_name]\n" +
                        "  \n" +
                        "  We noticed that your recent activity violates our Terms of Use\n" +
                        "\n" +
                        "  Please review our guidelines and ensure compliance in the future. Repeated violations may result in account suspension.\n" +
                        "\n" +
                        "  Thank you for your understanding.\n" +
                        "  \n" +
                        "  ",
                isWarning = true,
                image = null,
                createdAt = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()),
            )
        )
    }
}
