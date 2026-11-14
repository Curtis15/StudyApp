package com.example.studyapp.screens


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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studyapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.studyapp.data.Buildings
import com.example.studyapp.data.StudySession
import com.example.studyapp.map.openMaps
import com.example.studyapp.notifications.reminder
import com.example.studyapp.notifications.showCancel
import java.util.UUID


@Composable
fun LoginScreen(viewModel: MainViewModel, navController: NavController){
    var name by remember {mutableStateOf("")}

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "ASTATE Study App",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Log in",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(text = "Enter your name to continue")
        OutlinedTextField(
            value = name,
            onValueChange = {name = it},
            label = {Text("Your name")},
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(name.trim())
                navController.navigate("home") {
                    popUpTo("login") {inclusive = true}
                }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log in")
        }
    }
}

@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController){
    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(
                text = "Home",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineLarge
            )

            OutlinedButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("home") {inclusive = true}
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Text(
                    "Log out",
                    fontSize = 16.sp,
                    textAlign = TextAlign.End
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your sessions",
            style = MaterialTheme.typography.titleLarge
        )

        Box(modifier = Modifier.weight(1f)) {
            if (viewModel.mySessions.isEmpty()){
                Text("You don't have any sessions yet.")
            } else {
                LazyColumn {
                    items(viewModel.mySessions) { s ->
                        val time = SimpleDateFormat("EEE h:mm a", Locale.getDefault())
                            .format(Date(s.startTime))
                        ListItem(
                            headlineContent = { Text(s.title) },
                            supportingContent = {
                                Text("${s.course}\n${s.building}, ${s.room}\n$time\n${s.memberNames.size}/${s.maxStudents} students")
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("details/${s.id}")
                            }
                        )
                    }
                }
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                onClick = {navController.navigate("create")}
            ) {
                Text("Create Session",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            TextButton(
                onClick = {navController.navigate("sessions")}
            ) {
                Text(
                    "View All Sessions",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun AllSessionScreen(viewModel: MainViewModel, navController: NavController){
    var menuOpen by remember {mutableStateOf(false)}

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            OutlinedButton(
                onClick = {navController.popBackStack()},
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Text(
                    "Back",
                    fontSize = 16.sp
                )
            }
            Text(
                "Sessions",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.courseFilter,
            onValueChange = {viewModel.updateCourseFilter(it)},
            label = {Text("Course (like CS 3203)")},
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { menuOpen = true }) {
                Text(viewModel.buildingFilter.ifBlank { "All buildings" })
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = {Text("All buildings")},
                    onClick = {viewModel.updateBuildingFilter(""); menuOpen = false}
                )
                for (b in Buildings){
                    DropdownMenuItem(
                        text = { Text(b.name) },
                        onClick = {viewModel.updateBuildingFilter(b.name); menuOpen = false}
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(onClick = {
                viewModel.updateCourseFilter("")
                viewModel.updateBuildingFilter("")
            }) {
                Text("Clear filters")
            }
        }
        if (viewModel.filteredSessions.isEmpty()) {
            Text("No Sessions")
        } else {
            LazyColumn {
                items(viewModel.filteredSessions) { s ->
                    val time = SimpleDateFormat("EEE h:mm a", Locale.getDefault())
                        .format(Date(s.startTime))
                    ListItem(
                        headlineContent = { Text(s.title) },
                        supportingContent = {
                            Text("${s.course}\n${s.building}, ${s.room}\n$time\n${s.memberNames.size}/${s.maxStudents} students")
                        },
                        modifier = Modifier.clickable {
                            navController.navigate("details/${s.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailsScreen(sessionId: String, viewModel: MainViewModel, navController: NavController){
    val context = LocalContext.current
    val session = viewModel.getSession(sessionId)
    val me = viewModel.currentUser?.name

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Text(
                    "Back",
                    fontSize = 16.sp
                )
            }
            Text(
                "Session\n Details",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineLarge
            )
        }

        if (session == null) {
            Text("Session not found")
        } else {
            val format = SimpleDateFormat("EEE h:mm a", Locale.getDefault())
            val startTime = format.format(Date(session.startTime))
            val endTime = format.format(Date(session.endTime))

            Text(
                text = session.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                "Host: ${session.hostName}",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Course: ${session.course}",
                fontSize = 16.sp
            )
            Text("Where: ${session.building}, ${session.room}",
                fontSize = 16.sp
            )
            Text("When: $startTime to $endTime",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Students (${session.memberNames.size}/${session.maxStudents})",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp
            )
            for ((index, name) in session.memberNames.withIndex()){
                Text("${index + 1}. $name",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {openMaps(context, session.building)},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open in Maps")
            }

            if (session.hostName == me){
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.end(session.id)
                        showCancel(context, session)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("End session")
                }
            }
            else if (me in session.memberNames){
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.leave(session.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Leave")
                }
            }
            else if (session.memberNames.size >= session.maxStudents){
                Spacer(modifier = Modifier.height(18.dp))

                Text("Session full",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            else{
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (viewModel.join(session.id)) {
                            reminder(context, session)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Join")
                }
            }
        }
    }
}

@Composable
fun CreateSessionScreen(viewModel: MainViewModel, navController: NavController){
    var title by remember {mutableStateOf("")}
    var course by remember {mutableStateOf("")}
    var room by remember { mutableStateOf("") }
    var building by remember {mutableStateOf("")}
    var maxStudents by remember {mutableStateOf("")}
    var startTime by remember {mutableStateOf("")}
    var menuOpen by remember {mutableStateOf(false)}
    var startNow by remember {mutableStateOf(false)}
    var sessionLength by remember {mutableStateOf("")}

    val duration = sessionLength.toIntOrNull()
    val max = maxStudents.toIntOrNull()
    val startIn = if (startNow) 0
        else startTime.toIntOrNull()
    val canCreate = title.isNotBlank() &&
            course.isNotBlank() &&
            building.isNotBlank() &&
            max != null && max >= 2 &&
            startIn != null &&
            duration != null && duration > 0
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Text(
                    "Back",
                    fontSize = 16.sp
                )
            }
            Text(
                " Create\nSession",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = course,
            onValueChange = {course = it},
            label = {Text("Course")},
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {menuOpen = true}) {
            Text(building.ifBlank {"Building"})
        }
        DropdownMenu(expanded = menuOpen, onDismissRequest = {menuOpen = false}) {
            for (b in Buildings) {
                DropdownMenuItem(
                    text = {Text(b.name)},
                    onClick = {building = b.name; menuOpen = false}
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = room,
            onValueChange = { room = it },
            label = { Text("Room") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = maxStudents,
            onValueChange = {maxStudents = it.filter { c -> c.isDigit() }},
            label = { Text("Max students (at least 2)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = startNow, onCheckedChange = {startNow = it})
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start now")
        }

        if (!startNow) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = startTime,
                onValueChange = {startTime = it.filter { c -> c.isDigit() }},
                label = {Text("Starts in (min)")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = sessionLength,
            onValueChange = {sessionLength = it.filter {c -> c.isDigit()}},
            label = {Text("Length (min)")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            if (max != null && startIn != null && duration != null) {
                val start = System.currentTimeMillis() + startIn * 60_000L
                val end = start + duration * 60_000L
                val host = viewModel.currentUser?.name ?: ""

                viewModel.create(
                    StudySession(
                        id = UUID.randomUUID().toString(),
                        title = title.trim(),
                        course = course.trim(),
                        building = building,
                        room = room.trim(),
                        startTime = start,
                        endTime = end,
                        maxStudents = max,
                        hostName = host,
                        memberNames = listOf(host)
                    )
                )
                navController.popBackStack()
            }
        },
            enabled = canCreate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create")
        }
    }
}