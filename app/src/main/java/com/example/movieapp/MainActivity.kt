package com.example.movieapp



import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.movieapp.ui.theme.MovieappTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private val movieViewModel by viewModels<MovieViewModel>()
    private val preferenceManager by lazy { PreferenceManager(this) }
    private lateinit var db: MovieDao
   // private var selectedPage by mutableStateOf(0)


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = MovieDatabase.getInstance(applicationContext).MovieDao()
        setContent {
            MovieappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedPage by rememberSaveable { mutableStateOf(0) }

                    val lazyPagingItems = when (selectedPage) {
                        0 -> movieViewModel.getPopularMovies().collectAsLazyPagingItems()
                        1 -> movieViewModel.getUpcomingMovies().collectAsLazyPagingItems()
                        2 -> movieViewModel.getTrendingMovies().collectAsLazyPagingItems()
                        3 -> movieViewModel.getTopRatedMovies().collectAsLazyPagingItems()
                        else -> throw IllegalArgumentException("Invalid page number")}
                    // Check internet connectivity
                    val connectivityManager =
                        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val isInternetAvailable =
                        connectivityManager.activeNetworkInfo?.isConnected == true

                    // Determine the start destination based on internet connectivity
                    val defaultScreen =
                        if (isInternetAvailable) preferenceManager.getLastVisitedScreen() else "Watch List"

                    // Initialize NavController and handle navigation
                    val navController = rememberNavController()


                    navController.addOnDestinationChangedListener { _, destination, _ ->
                        preferenceManager.saveLastVisitedScreen(destination.route ?: "")
                    }

                    // Set the default screen as the last visited screen (if available) or "Watch List"
                    NavHost(navController = navController, startDestination = defaultScreen ?: "Watch List") {
                        composable("Landing Page") {
                            LandingPage(navController, movieViewModel, db, preferenceManager)
                        }
                        composable(
                            "Detailed View/{title}/{desc}/{poster}",
                            arguments = listOf(
                                navArgument("title") { type = NavType.StringType },
                                navArgument("desc") { type = NavType.StringType },
                                navArgument("poster") { type = NavType.StringType },
                            )

                        )

                        { navBackStackEntry ->
                            var poster = navBackStackEntry.arguments?.getString("poster") ?: ""
                            var title = navBackStackEntry.arguments?.getString("title") ?: ""
                            var descrptn = navBackStackEntry.arguments?.getString("desc") ?: ""
                            DetailedView(navController, title, descrptn,poster)
                        }
                        composable("Watch List") {
                            Watchlist(navController, movieViewModel, db)

                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        // Handle back button press within the MainActivity
        super.onBackPressed()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//preferenceManager: PreferenceManager
fun LandingPage(navController: NavController,  movieViewModel : MovieViewModel, db : MovieDao,preferenceManager: PreferenceManager){
    val movieType = listOf<String>("popular","upcoming","trending","top-rated")
     var selectedPage by rememberSaveable { mutableStateOf(0) }
  //  var scrollState = rememberLazyListState()
  //  val lazyPagingItems = when (selectedPage)



    Scaffold(
        topBar = { TopAppBar(
            title = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${movieType[selectedPage]} movies",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(330.dp)
                    )
                    Icon(painter = painterResource(R.drawable.baseline_bookmark_24),
                        contentDescription = "",
                        modifier = Modifier
                            .width(20.dp)
                            .clickable { navController.navigate("Watch List") })
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
               containerColor = colorResource(id = R.color.darkgrey),
                titleContentColor = Color.White
            ),


            modifier = Modifier
                .clip(shape = RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp)),
        )},



        content = { Listview(movieViewModel, navController, db,selectedPage) },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    IconButton(
                        onClick = { selectedPage = 0 },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.fire),
                                contentDescription = "",
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "popular",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(
                        onClick = { selectedPage = 1 },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.punch),
                                contentDescription = "",
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "upcoming",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(
                        onClick = { selectedPage =  2 },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.play),
                                contentDescription = "",
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "trending",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(
                        onClick = { selectedPage = 3},
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.star),
                                contentDescription = "",
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "top-rated",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Listview(movieViewModel: MovieViewModel, navController: NavController, db: MovieDao,selectedPage: Int)

{


    val lazyPagingItems = when (selectedPage) {
        0 -> movieViewModel.getPopularMovies().collectAsLazyPagingItems()
        1 -> movieViewModel.getUpcomingMovies().collectAsLazyPagingItems()
        2 -> movieViewModel.getTrendingMovies().collectAsLazyPagingItems()
        3 -> movieViewModel.getTopRatedMovies().collectAsLazyPagingItems()
        else -> throw IllegalArgumentException("Invalid page number")


    }
 //   val watchlistMovieTitles = remember { mutableSetOf<String>() }
    var addedMovieTitles = remember { mutableSetOf<String>() }



    var isAddClicked by remember {
        mutableStateOf(false)
    }

  //  var title by remember {
    //    mutableStateOf("")}

    LazyColumn(modifier = Modifier.padding(top = 65.dp)) {
        items(lazyPagingItems.itemCount) { index ->
            val movie = lazyPagingItems[index]
            movie?.let {
                Card(modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp)
                    .padding(top = 10.dp)
                    .height(200.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(15.dp)),

                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = {

                        val uri = URLEncoder.encode("https://image.tmdb.org/t/p/w500/${it.posterPath}",
                            StandardCharsets.UTF_8.toString())
                        navController.navigate("Detailed View/${it.title}/${it.overview}/${
                            uri}")

                    },
                ) {
                    Row {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w500/${it.posterPath}",
                            contentDescription = "",
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(shape = RoundedCornerShape(15.dp))
                        )
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(180.dp)
                        ) {
                            Column(modifier = Modifier.height(160.dp)) {
                                Text(
                                    text = it.originalTitle ?: "",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = colorResource(id = R.color.navy)
                                )
                                Text(
                                    text = it.overview ?: "",
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                        .wrapContentWidth(),
                                    lineHeight = 15.sp,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = it.voteAverage.toString() ?: "",
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(40.dp)
                                        .padding(top = 10.dp)
                                        .clip(shape = RoundedCornerShape(15.dp))
                                        .background(color = colorResource(id = R.color.navy))
                                        .padding(5.dp),
                                    overflow = TextOverflow.Clip
                                )
                            }
                            Row {
                                val scope = rememberCoroutineScope()
                                it.releaseDate?.let { it1 ->
                                    Text(
                                        text = "release date: ${it1}",
                                        fontSize = 12.sp,
                                        color = colorResource(id = R.color.grey),
                                        modifier = Modifier.width(170.dp)
                                    )
                                }
                                IconButton(onClick = { if (!addedMovieTitles.contains(it.originalTitle)) {
                                    scope.launch {
                                        db.insert(
                                            MovieItem(
                                                it.id?: 0,
                                                System.currentTimeMillis(),

                                               // it.id?: "",
                                                it.posterPath ?: "",
                                                it.title ?: "",
                                                it.overview ?: "",
                                            )
                                        )
                                    }
                                   // title = it.originalTitle ?: ""
                                    isAddClicked = true
                                    addedMovieTitles = addedMovieTitles.plus(it.originalTitle ?: "") as MutableSet<String>
                                }

                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_add_circle_outline_24),
                                        contentDescription = ""
                                    )
                                }
                            }
                        }
                    }

                    if (isAddClicked) {
                        Toast.makeText(
                            LocalContext.current,
                            "${it.originalTitle ?: ""} added to Watchlist",
                            Toast.LENGTH_SHORT
                        ).show()
                        isAddClicked = false
                    }
                }
            }
        }

            // Handle loading state and errors
    /*    when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            // Show loading state if data is being fetched

            }
            is LoadState.Error -> {

                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading data",
                            fontSize = 16.sp,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

             // Show error message if there is a problem loading data initially
            }

            else -> {}
        }

        when (lazyPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }
            // Show error message if there is a problem appending new data
            is LoadState.Error -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading more data",
                            fontSize = 16.sp,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            else -> {}
        }

     */
    }


}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedView(navController: NavController, title : String, descrptn : String, poster : String){
    LazyColumn(modifier = Modifier.fillMaxSize(),state = rememberLazyListState()) {
        item{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(590.dp)
                    .clip(RoundedCornerShape(bottomStart = 35.dp, bottomEnd = 35.dp))
                    .shadow(5.dp)
            ) {
                AsyncImage(
                    model = "$poster",
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        item{
            Column(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 30.dp, bottom = 10.dp)
            ) {
                Text(text = title, fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = descrptn,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
 Box(modifier = Modifier.fillMaxSize()) {

        Icon(painter = painterResource(id = R.drawable.baseline_keyboard_arrow_left_24),
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    navController.navigate("Landing Page")
                })


   }
}

@Composable
fun Watchlist(navController: NavController, movieViewModel: MovieViewModel,db : MovieDao){
    val scope = rememberCoroutineScope()
    val listOfMovies by remember {
        derivedStateOf {
            runBlocking {
                withContext(Dispatchers.IO){
                    db.getAll()
                }
            }
        }
    }
   // val watchlistMovieIds = remember { mutableSetOf<Long>() }

    Column(modifier = Modifier.padding(15.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(painter = painterResource(id = R.drawable.baseline_keyboard_arrow_left_24),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable {
                        navController.navigate("Landing Page")
                    })
            Text(text = "Watchlist", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
        if(listOfMovies.isNotEmpty()){
            LazyColumn(){
                items(items = listOfMovies){ movie ->
                //    if (!watchlistMovieIds.contains(it.time)) {


                        Box(
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp, top = 10.dp)
                                .height(190.dp)
                                .border(2.dp, Color.DarkGray, RoundedCornerShape(10.dp))
                                .clickable {
                                        val uri = URLEncoder.encode("https://image.tmdb.org/t/p/w500/${movie.url}",
                                            StandardCharsets.UTF_8.toString())
                                        navController.navigate("Detailed View/${movie.title}/${movie.descrptn}/${
                                            uri}")
                                    })


                        {

                            AsyncImage(
                                model = "https://image.tmdb.org/t/p/w500/${movie.url}",
                                contentDescription = "",
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black
                                            )
                                        )
                                    )
                            ) {
                                Icon(painter = painterResource(id = R.drawable.baseline_remove_circle_outline_24),
                                    contentDescription = "",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clickable {
                                            scope.launch {
                                                db.deleteId(movie.time)
                                                navController.navigate("Watch List")
                                            }
                                        })
                                Text(
                                    text = movie.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 10.dp, top = 160.dp)
                                )

                            }
                        }

                }
            }
        }
        else{
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                Text(text = "No Movies Added!", textAlign = TextAlign.Center, fontSize = 30.sp, fontWeight = FontWeight.SemiBold)

            }
        }

    }
}