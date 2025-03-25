package it.uniupo.ktt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.secondary
import it.uniupo.ktt.ui.theme.titleColor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PageTitle(
    navController: NavController,
    title: String
)
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(10.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(4.dp, shape = CircleShape, clip = false)
        ) {
            FilledIconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.size(34.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = primary,
                    contentColor = titleColor
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .padding(end = 34.dp)
                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false)
                .background(
                    color = secondary,
                    shape = MaterialTheme.shapes.extraExtraLarge
                )
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = titleColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}