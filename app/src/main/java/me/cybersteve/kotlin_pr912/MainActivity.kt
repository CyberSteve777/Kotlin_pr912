package me.cybersteve.kotlin_pr912

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.cybersteve.kotlin_pr912.ui.theme.Kotlin_pr912Theme
import java.net.URL

class ImageDownloadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Ссылка на изображение
            val url = URL(inputData.getString("imageUrl"))
            val bitmap = BitmapFactory.decodeStream(withContext(Dispatchers.IO) {
                url.openStream()
            })
            val fileName = inputData.getString("fileName") ?: "temp_image.png"

            // Сохраняем изображение локально (если нужно)
            applicationContext.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                bitmap.compress(CompressFormat.PNG, 100, it)
            }

            // Успешный результат
            Result.success(workDataOf("filePath" to fileName))
        } catch (e: Exception) {
            Log.w("rrr", e.toString())
            Result.failure()
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kotlin_pr912Theme {
                ActivityMain()
            }
        }
    }
}

@Composable
fun ActivityMain() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    name = "Vyacheslav",
                    surname = "Vaganov",
                    group = "IKBO-12-22",
                    navController
                )
            }
            composable("plant") { PlantScreen(navController) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String) {
    TopAppBar(
        title = { Text(text = title) },
        Modifier.fillMaxHeight(0.05f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(name: String, surname: String, group: String, navController: NavController) {
    val arr: List<String> =
        mutableListOf("peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach", "peach")
    val someText = remember { mutableStateOf("This is a card!") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column {
                    Text("Do you really want to see Plant Image?")
                    Button(
                        onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isOpen) close() else open()
                                }
                            }
                            navController.navigate("plant")
                        }
                    ) {
                        Text("Yes, get me to it")
                    }
                }
            }
        },
        gesturesEnabled = false
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("I'm on top") }
                )
            },
            bottomBar = {
                BottomAppBar {
                    Text("I'm at the bottom")
                }
            },
            content = { innerPadding ->
                Modifier.padding(innerPadding)
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxHeight(0.9f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .background(Color.Red)
                            .fillMaxWidth()
                            .fillMaxHeight(0.3f)

                    ) {
                        Text(
                            text = "I'm $name $surname\nI'm a student",
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = group,
                        )
                    }
                    Box(
                        Modifier
                            .background(Color.Green)
                            .fillMaxHeight(0.5f)
                            .fillMaxWidth()

                    ) {
                        LazyRow {
                            items(arr) { item ->
                                Text(
                                    text = item,
                                    Modifier.padding(2.dp),
                                    fontSize = 30.sp
                                )
                            }
                        }
                    }
                    Card(
                        Modifier
                            .background(Color.Blue)
                            .fillMaxHeight(0.8f)
                            .fillMaxWidth()

                    ) {
                        Text(
                            text = someText.value,
                            fontSize = 40.sp
                        )
                    }
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        someText.value = "You pushed me! How dare you!"
                        scope.launch {
                            drawerState.apply {
                                Log.d("rrr", isClosed.toString())
                                if (isClosed) open() else close()
                            }
                        }

                    }
                ) { Text("Pushity push push") }
            }
        )
    }


}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlantScreen(navController: NavController) {
    val constraints = Constraints.Builder().setRequiresBatteryNotLow(true).setRequiredNetworkType(
        NetworkType.CONNECTED
    ).build()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    Scaffold(Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Показ изображения
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Downloaded Image",
                    modifier = Modifier.size(200.dp)
                )
            } else {
                Text("No Image Loaded")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row (modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    val workManager = WorkManager.getInstance(context)

                    val workRequest = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
                        .setInputData(
                            workDataOf(
                                "imageUrl" to "https://plantsvszombies.wiki.gg/images/d/d1/Deodarcedar_HD.png",
                                "fileName" to "downloaded_image.png"
                            )
                        )
                        .setConstraints(constraints).build()
                    workManager.enqueue(workRequest)
                    workManager.getWorkInfoByIdLiveData(workRequest.id)
                        .observe(lifecycleOwner) { workInfo ->
                            Log.d("rrr", "debug")
                            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                                val filePath = workInfo.outputData.getString("filePath")
                                filePath?.let {
                                    val file = context.getFileStreamPath(it)
                                    bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                }
                            }
                        }
                }) {
                    Text("Load Plant Image")
                }
                Button (onClick = {
                    navController.navigate("home")
                }) {
                    Text("Home")
                }
            }

        }
    }
}