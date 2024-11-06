package com.example.tasky.agenda.agenda_presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.tasky.R
import com.example.tasky.agenda.agenda_presentation.viewmodel.PhotoAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.PhotoViewModel
import com.example.tasky.ui.theme.AppTheme

@Composable
internal fun PhotoScreen(
    onNavigateBack: () -> Unit,
    onDeletePhoto: (String) -> Unit
) {
    val photoViewModel = hiltViewModel<PhotoViewModel>()
    val photoUrl = photoViewModel.photoUrl
    val photoId = photoViewModel.photoKey

    PhotoContent(
        photoId = photoId,
        photoUrl = photoUrl,
        onAction = { action ->
            when (action) {
                PhotoAction.OnNavigateBack -> onNavigateBack()
                is PhotoAction.OnDeletePhoto -> {
                    photoId?.let { onDeletePhoto(it) }
                }
            }
        })
}

@Composable
fun PhotoContent(
    photoId: String?,
    photoUrl: String?,
    onAction: (PhotoAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.black)
    ) {
        Header(onAction = onAction, photoId = photoId)

        Spacer(modifier = Modifier.height(AppTheme.dimensions.large32dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(10.dp))
                .padding(horizontal = AppTheme.dimensions.default16dp)
        ) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Uploaded image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(bottom = 128.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
            )
        }
    }

}

@Composable
fun Header(onAction: (PhotoAction) -> Unit, photoId: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppTheme.dimensions.large24dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            Icons.Outlined.Close,
            contentDescription = "Close icon",
            tint = AppTheme.colors.white,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onAction(PhotoAction.OnNavigateBack)
                }
        )

        Text(
            text = stringResource(R.string.Photo),
            style = AppTheme.typography.bodyLarge.copy(
                lineHeight = 12.sp, fontWeight = FontWeight.W600
            ),
            color = AppTheme.colors.white
        )

        Icon(
            Icons.Outlined.Delete,
            contentDescription = "Deleted icon",
            tint = AppTheme.colors.white,
            modifier = Modifier.clickable {
                photoId?.let { safePhotoId -> onAction(PhotoAction.OnDeletePhoto(safePhotoId)) }
            }
        )
    }
}

@Preview
@Composable
fun PhotoContentPreview() {
    AppTheme {
        PhotoContent(photoId = "dfdfdf", photoUrl = "https://picsum.photos/500/800") { action ->
            when (action) {
                PhotoAction.OnNavigateBack -> {}
                is PhotoAction.OnDeletePhoto -> {}
            }
        }
    }
}