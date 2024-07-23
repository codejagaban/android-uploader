package com.example.uploader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uploader.features.UIViewModel
import com.example.uploader.ui.theme.UploaderTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.uploader.features.ImageResults
import com.example.uploader.features.UIEvent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UploaderTheme {
                PhotoScreen()
            }
        }
    }
}


@Composable
fun PhotoScreen(
    viewModel: UIViewModel = viewModel()
){

    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current


    val singleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri->
            uri?.let { UIEvent.SingleImageChanged(context,it) }?.let { viewModel.onEvent(it) }
        }
    )
    val multipleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {uris->
            viewModel.onEvent(UIEvent.MultipleImageChanged(context,uris))
        }
    )
    LaunchedEffect(Unit) {
        viewModel.onEvent(UIEvent.GetImages)
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "UC file uploader in Android",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.size(20.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0073E6),
                    contentColor = Color.White,
                ),
                onClick = {
                    if(!uiState.isUploading){
                        singleImagePickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                }) {
                if(!uiState.isUploading){
                    Row {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "upload a file", style = TextStyle(
                            fontSize = 18.sp
                        ))
                    }
                }else{
                    CircularProgressIndicator(
                        color = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0073E6),
                    contentColor = Color.White,
                ),
                onClick = {
                    if(!uiState.isUploading){
                        multipleImagePickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                }) {
                if(!uiState.isUploading){
                    Row {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "upload multiple files ", style = TextStyle(
                            fontSize = 18.sp
                        ))
                    }
                }else{
                    CircularProgressIndicator(
                        color = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0073E6),
                    contentColor = Color.White,
                ),
                onClick = {
                    if(!uiState.isUploading){
                        viewModel.onEvent(UIEvent.GetImages)
                    }
                }) {
                if(!uiState.isUploading){
                    Row {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "get all files", style = TextStyle(
                            fontSize = 18.sp
                        ))
                    }
                }else{
                    CircularProgressIndicator(
                        color = Color.White,
                    )
                }
            }

            if(uiState.images.isNotEmpty()){
                ImageGrid(images = uiState.images)
            }else{
                Spacer(modifier = Modifier.height(40.dp))
                if(!uiState.isUploading){
                    Text(
                        text = "No uploaded images yet",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}


//single image view
@Composable
fun NetworkImage(imageUrl: String) {
    val imageModifier = Modifier
        .padding(8.dp)
        .size(150.dp)
    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
        ),
        contentDescription = "Network Image",
        contentScale = ContentScale.Crop,
        modifier = imageModifier
    )
}



//Image grid that displays all the images
@Composable
fun ImageGrid(images: List<ImageResults>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(images.size) { index ->
            NetworkImage(
                imageUrl = images[index].imageUrl
            )
        }
    }
}




@Preview
@Composable
fun PhotoPickerScreenPreview(){
    PhotoScreen()
}
