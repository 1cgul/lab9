package edu.farmingdale.datastoresimplestoredemo
import android.content.Context
import java.io.PrintWriter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.farmingdale.datastoresimplestoredemo.data.AppPreferences
import edu.farmingdale.datastoresimplestoredemo.ui.theme.DataStoreSimpleStoreDemoTheme
import kotlinx.coroutines.launch
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStoreSimpleStoreDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DataStoreDemo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
        writeToInternalFile()
        val fileContents = readFromInternalFile()
        Log.d("MainActivity", fileContents)
    }
    private fun writeToInternalFile() {
        val outputStream: FileOutputStream = openFileOutput("fav_haiku", Context.MODE_PRIVATE)
        val writer = PrintWriter(outputStream)

        // Write three lines
        writer.println("This world of dew")
        writer.println("is a world of dew,")
        writer.println("and yet, and yet.")

        writer.close()
    }

    private fun readFromInternalFile(): String {
        val inputStream = openFileInput("fav_haiku")
        val reader = inputStream.bufferedReader()
        val stringBuilder = StringBuilder()

        // Append each line and newline character to stringBuilder
        reader.forEachLine {
            stringBuilder.append(it).append("\n BCS 371 \n").append(System.lineSeparator())
        }

        return stringBuilder.toString()
    }
}

@Composable
fun DataStoreDemo(modifier: Modifier) {

    val store = AppStorage(LocalContext.current)
    val appPrefs = store.appPreferenceFlow.collectAsState(AppPreferences())
    val coroutineScope = rememberCoroutineScope()
    var username by remember{ mutableStateOf("") }

    Column(modifier = Modifier.padding(50.dp)) {
        // Display the values stored in the DataStore
        Text(
            text = "Stored Values:\n" +
                    "Username: ${appPrefs.value.userName}\n" +
                     "High Score: ${appPrefs.value.highScore}\n" +
                    "Dark Mode: ${if (appPrefs.value.darkMode) "Enabled" else "Disabled"}",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            coroutineScope.launch {
                store.saveUsername(username) // saves username
                store.saveHighscore(appPrefs.value.highScore + 1) // adds 1 to the high score
            }
        }) {
            Text("Save Values")
        }
        Button(onClick = {
            coroutineScope.launch {
                store.themeDarkmode(!appPrefs.value.darkMode) // toggles dark mode
            }
        }) {
            Text("Toggle Dark Mode")
        }
    }
}



