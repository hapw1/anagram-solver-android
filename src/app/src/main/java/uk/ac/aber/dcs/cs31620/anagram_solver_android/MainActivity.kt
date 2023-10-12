package uk.ac.aber.dcs.cs31620.anagram_solver_android

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.*
import uk.ac.aber.dcs.cs31620.anagram_solver_android.ui.theme.AnagramsolverandroidTheme


var savedInstanceState2: Bundle? = null

//Opens and closes the instruction dialogs for each type of anagram
private val showCompleteDialog = mutableStateOf(false)
private val showMissingDialog = mutableStateOf(false)
private val showCrosswordDialog = mutableStateOf(false)
private val showCheckedStateDialog = mutableStateOf(false)

private var checkedState = mutableStateOf(false)

val mainColor = Color(60, 172, 174)
val accentColor = Color(200, 244, 249)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState2 = savedInstanceState

        setContent {
            AnagramsolverandroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = mainColor
                ) {
                    ScreenSetup()
                    //Checks whether or not the dialogs should be open
                    if(showCompleteDialog.value){
                        CompleteDialog()
                    }
                    if(showMissingDialog.value){
                        MissingDialog()
                    }
                    if(showCrosswordDialog.value){
                        CrosswordDialog()
                    }
                    if(showCheckedStateDialog.value){
                        TooManyCharactersAlert()
                    }
                }
            }
        }

        //MobileAds.initialize(this){}

    }

}

/**
 * Sets up the main screen
 *
 * @param viewModel as an anagramViewModel
 */
@Composable
fun ScreenSetup(viewModel: AnagramViewModel = viewModel()){
    MainScreen(
        matches = viewModel.matches,
        words = viewModel.words,
        readAnagram = {viewModel.readAnagram(it, checkedState)},
        sortWords = {viewModel.sortWords(it)}
    )
}

/**
 * Creates the top app bar and the information button used for instructions
 */
@Composable
fun AnagramAppBar(sortWords: (Int) -> Unit){

    var isExpanded by remember { mutableStateOf(false)}
    var isFilterExpanded by remember { mutableStateOf(false)}
    var isSettingsExpanded by remember { mutableStateOf(false)}



    TopAppBar(
        backgroundColor = mainColor,
        title =
        {
            Text(text = "Anagram Solver", color = Color.White)
        },
        actions =
        {

            IconButton(
                onClick = { isFilterExpanded = !isFilterExpanded}
            ){
                Icon(Icons.Default.Sort, "Sort", tint = Color.White)
            }
            IconButton(
                onClick = { isExpanded = !isExpanded }
            ) {
                Icon(Icons.Default.Info, "Overflow Menu", tint = Color.White)
            }
            IconButton(
                onClick = {isSettingsExpanded = !isSettingsExpanded}
            ){
                Icon(Icons.Default.Settings, "Settings Menu", tint = Color.White)
            }


            DropdownMenu(expanded = isFilterExpanded,
                onDismissRequest = { isFilterExpanded = false }
            ) {
                //Opens the filter menu
                FilterMenu(sortWords)
            }
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) 
            {
                //Opens the overflow menu
                OverflowMenu()
            }

            DropdownMenu(expanded = isSettingsExpanded,
                onDismissRequest = { isSettingsExpanded = false }
            ) {
                //Opens the settings menu
                SettingsMenu()
            }

        }
    )
}

@Composable
fun FilterMenu(sortWords: (Int) -> Unit){
    DropdownMenuItem(onClick = { sortWords(1) }
    ) {
        Text(text = "A - Z")
    }
    DropdownMenuItem(onClick = { sortWords(2) }
    ) {
        Text(text = "Z - A")
    }
    DropdownMenuItem(onClick = { sortWords(3) }
    ) {
        Text(text = "Length - Asc")
    }
    DropdownMenuItem(onClick = { sortWords(4) }
    ) {
        Text(text = "Length - Desc")
    }
}

@Composable
fun SettingsMenu(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ){
        Text(text = "Show Sub Anagrams")

        Switch(
            checked = checkedState.value,
            onCheckedChange = {checkedState.value = it}
        )
    }
}


@Composable
fun OverflowMenu(){
    DropdownMenuItem(
        onClick = { showCompleteDialog.value = true }
    )
    {
        Text(text = "Solve Complete")
    }
    DropdownMenuItem(
        onClick = { showMissingDialog.value = true }
    )
    {
        Text(text = "Solve Missing")
    }
    DropdownMenuItem(
        onClick = { showCrosswordDialog.value = true }
    )
    {
        Text(text = "Solve Crossword")
    }
}

@Composable
fun CompleteDialog(){
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            TextButton(onClick = { showCompleteDialog.value = false })
            {
                Text(text = "OK")
            }
        },
        title = {
            Text(
                text = "How to Solve - Complete Anagrams",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)},
        text = { Text(text = "Enter all the letters contained " +
                "in the anagram and tap solve")}
    )
}
@Composable
fun MissingDialog(){
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            TextButton(onClick = { showMissingDialog.value = false })
            {
                Text(text = "OK")
            }
        },
        title = {
            Text(
                text = "How to Solve - Missing Letter Anagrams",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)},
        text = { Text(text = "Enter all the letters " +
                "you know in the anagram " +
                "followed by a plus (+) for each " +
                "unknown letter and tap solve")}
    )
}
@Composable
fun CrosswordDialog(){
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            TextButton(onClick = { showCrosswordDialog.value = false })
            {
                Text(text = "OK")
            }
        },
        title = {
            Text(
                text = "How to Solve - Crossword Anagrams",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)},
        text = { Text(text = "Enter all the known " +
                "letters in their position " +
                "with a period (.) in every position where the " +
                "letter is unknown and tap solve")}
    )
}

@Composable
fun MainScreen(
    matches: Int,
    words: MutableList<String>,
    readAnagram: (String) -> Unit,
    sortWords: (Int) -> Unit
){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "solve_screen"
    ){
        composable("solve_screen"){
            SolveScreen(matches = matches, words = words, readAnagram = readAnagram, sortWords = sortWords)
        }

    }
}

@Composable
fun SolveScreen(
    matches: Int,
    words: MutableList<String>,
    readAnagram: (String) -> Unit,
    sortWords: (Int) -> Unit){
    Scaffold(
        topBar = {
            AnagramAppBar(sortWords = sortWords)
        },
        bottomBar = {
            //AdView()
        }
    )
    {

        Column(modifier = Modifier.background(Color.White)){

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            {
                var anagramState by remember { mutableStateOf("")}

                val onTextChange = {text : String ->
                    anagramState = text
                }

                InputRow(
                    anagramState = anagramState,
                    onTextChange = onTextChange,
                    readAnagram = readAnagram
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            {
                Text(text = "Matches: $matches")
            }

            OutputRow(words = words)

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ){

            }
        }
    }
}

@Composable
fun InputRow(
    anagramState: String,
    onTextChange: (String) -> Unit,
    readAnagram: (String) -> Unit){
    Row(verticalAlignment = Alignment.CenterVertically){
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = anagramState,
                onValueChange = {onTextChange(it)},
                singleLine = true,
                label = { Text(text = "Enter anagram")},
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = mainColor,
                    cursorColor = mainColor,
                    focusedLabelColor = mainColor,
                unfocusedBorderColor = mainColor),
                modifier =
                Modifier
                    .padding(8.dp)
            )
        }

        //Starts the process of solving the anagram
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = mainColor),
            onClick =
            {
                if((anagramState.contains('+') && checkedState.value) || (anagramState.contains('.') && checkedState.value)){
                    showCheckedStateDialog.value = true
                }else{
                    readAnagram(anagramState)
                }
            },
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(Icons.Default.Search, "Solve", tint = Color.White)
        }
    }
}

@Composable
fun TooManyCharactersAlert(){
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            TextButton(onClick = { showCheckedStateDialog.value = false })
            {
                Text(text = "OK")
            }
        },
        title = {
            Text(
                text = "Sub Anagrams Enabled",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)},
        text = { Text(text = "The option to search for sub anagrams" +
                "cannot be enabled whilst the anagram " +
                "contains either a (+) or a (.)")}
    )
}

@Composable
fun OutputRow(words: MutableList<String>){
    LazyColumn(
        modifier = Modifier
            .padding(4.dp, 4.dp)
            .fillMaxSize()
    ){
        items(words){word ->
            WordCard(word)
        }
    }
}

/**
 * Displays one of the results for the anagram
 *
 * @param word ass a string
 */
@Composable
fun WordCard(word: String){
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(4.dp, 4.dp),
        elevation = 8.dp
    ) {
        WordCardRow(word)
    }
}

/**
 * Displays the word along with the search button
 *
 * @param word as a string
 */
@Composable
fun WordCardRow(word: String){
    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .background(mainColor)
    ){
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = word,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier
                    .padding(8.dp, 0.dp)
            )
        }
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            //Searches for the word's definition on the internet
            IconButton(
                onClick = {
                    //Calls the web search function
                    webSearchWord(word, context)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    "Search",
                    tint = Color.White)
            }
        }
    }
}

/**
 * Displays the banner advert at the bottom of the screen
 */
@Composable
fun AdView(){
    val adWidth = LocalConfiguration.current.screenWidthDp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ){
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(
                        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize
                            (
                            context,
                            adWidth
                        )
                    )
                    adUnitId = "ca-app-pub-8568528808185925/7160375269"

                    //adUnitId = "ca-app-pub-8568528808185925/5772191185"

                    //adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}



/**
 * Searches for the word chosen on the web
 *
 * @param word as a string
 */
private fun webSearchWord(word: String, context: Context){
    val intent = Intent(Intent.ACTION_WEB_SEARCH)

    //Creates a query
    val searchQuery = "define $word"

    //Adds the search query to the intent
    intent.putExtra(SearchManager.QUERY, searchQuery)

    //Launches the web browser and searches for the word
    startActivity(context, intent, savedInstanceState2)
}

/**
@Preview(showBackground = true)
@Composable
fun DefaultPreview(model: AnagramViewModel = viewModel()) {
    AnagramsolverandroidTheme {
        //MainScreen(matches = , words = , readAnagram = )
    }
}
 */
