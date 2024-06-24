package com.karina.carawicara.ui.screen.kenaliAku

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.karina.carawicara.R
import com.karina.carawicara.ui.component.ButtonNav
import com.karina.carawicara.ui.component.CameraPreview
import java.io.File
import androidx.core.app.ActivityCompat

private const val REQUEST_CODE_PERMISSIONS = 1001

@Composable
fun KenaliAkuRecordPage(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val filesDir = context.filesDir

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    val CAMERAX_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
    )

    // Check if all required permissions are granted
    val allPermissionsGranted = hasRequiredPermissions(context, CAMERAX_PERMISSIONS)

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            if (allPermissionsGranted) {
                CameraPreview(
                    controller = controller,
                    modifier = Modifier
                        .aspectRatio(3f / 4)
                        .padding(32.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (allPermissionsGranted) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    ButtonNav(
                        onClick = { takePicture(controller, context, filesDir) },
                        icon = R.drawable.ic_record,
                        iconColor = Color.White.toArgb(),
                        borderColor = MaterialTheme.colorScheme.errorContainer.toArgb(),
                        backgroundColor = MaterialTheme.colorScheme.error.toArgb(),
                        enabled = true
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    // Request permissions if not already granted
    if (!allPermissionsGranted) {
        ActivityCompat.requestPermissions(
            context as Activity,
            CAMERAX_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

//    // Update recording time every second when recording is active
//    LaunchedEffect(isRecording) {
//        if (isRecording) {
//            while (true) {
//                delay(1000)
//                recordingTime.value += 1000
//            }
//        }
//    }
}
@SuppressLint("MissingPermission")
private fun toggleRecording(controller: LifecycleCameraController, context: Context, filesDir: File, permissions: Array<String>, recording: MutableState<Recording?>) {
    if (recording.value != null) {
        // Stop recording
        recording.value?.stop()
        recording.value = null
    } else {
        // Start or resume recording
        if (!hasRequiredPermissions(context, permissions)) {
            // Request permissions if not already granted
            ActivityCompat.requestPermissions(
                context as Activity,
                permissions,
                REQUEST_CODE_PERMISSIONS
            )
            return
        }
        val outputFile = File(filesDir, "my-recording.mp4")
        if (outputFile.exists()) {
            // If the file exists, delete it to start a new recording
            outputFile.delete()
        }
        recording.value = controller.startRecording(
            FileOutputOptions.Builder(outputFile).build(), // Use FileOutputOptions for recording to a file
            AudioConfig.create(true),
            ContextCompat.getMainExecutor(context)
        ) { event ->
            // Event handling logic remains unchanged
        }
    }
}

private fun resetRecordingAndTime(
    recording: MutableState<Recording?>,
    recordingTime: MutableState<Long>,
    navHostController: NavHostController
) {
    // Reset recording state
    recording.value?.stop()
    recording.value = null
    recordingTime.value = 0L

    // Navigate to the desired destination with message
    val message = "Tingkat kecocokan: 82%. \n Selamat Anda berhasil!"
    navHostController.navigate("kenaliAkuPage/$message")
}

private fun stopRecording(recording: MutableState<Recording?>) {
    // Stop recording if in progress
    recording.value?.stop()
    recording.value = null
}

private fun hasRequiredPermissions(context: Context, permissions: Array<String>): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}

private fun formatElapsedTime(timeMillis: Long): String {
    val seconds = timeMillis / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

@SuppressLint("MissingPermission")
private fun takePicture(controller: LifecycleCameraController, context: Context, filesDir: File) {
    val outputFile = File(filesDir, "my-picture.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

    controller.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Handle the success of image capture
                Toast.makeText(context, "Picture taken successfully!", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {
                // Handle any errors during image capture
                Toast.makeText(context, "Failed to take picture: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}