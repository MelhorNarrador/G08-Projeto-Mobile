package pt.iade.lane.ui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import pt.iade.lane.components.ProfileBottomSheet
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.SessionManager
import pt.iade.lane.ui.viewmodels.EventoViewModel

@Composable
fun HomeRoute(viewModel: EventoViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val sessionManager = remember(context) { SessionManager(context) }

    LaunchedEffect(Unit) {
        viewModel.attachSessionManager(sessionManager)
    }

    val eventos by viewModel.eventos.collectAsState()
    val session by viewModel.session.collectAsState()

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    var isProfileSheetOpen by rememberSaveable { mutableStateOf(false) }

    val participatingEvents = remember(eventos, session.joinedEventIds) {
        eventos.filter { session.joinedEventIds.contains(it.id) }
    }

    val activeEvents = remember(eventos, session.userId) {
        val uid = session.userId
        if (uid == null) emptyList() else eventos.filter { it.creatorId == uid }
    }

    HomeScreen(
        selectedIndex = selectedItemIndex,
        onItemSelected = { index ->
            selectedItemIndex = index
            when (index) {
                0 -> Toast.makeText(
                    context,
                    "Ecrã de pesquisa ainda não implementado.",
                    Toast.LENGTH_SHORT
                ).show()

                1 -> isProfileSheetOpen = true
            }
        },
        onCreateEventClick = {
            context.startActivity(Intent(context, CreateEventActivity::class.java))
        }
    ) {
        MapScreen(viewModel = viewModel)

        ProfileBottomSheet(
            isOpen = isProfileSheetOpen,
            onDismiss = { isProfileSheetOpen = false },
            name = session.name,
            username = session.username,
            bio = session.bio,
            profileImageBase64 = session.profileImageBase64,
            activeEvents = activeEvents,
            participatingEvents = participatingEvents,

            onEditEventClick = { evento ->
                val intent = Intent(context, CreateEventActivity::class.java).apply {
                    putExtra("mode", "edit")
                    putExtra("event", evento)
                }
                context.startActivity(intent)
            },

            onDeleteEventClick = { evento ->
                scope.launch {
                    val ok = viewModel.deleteEvent(evento.id)
                    val msg = if (ok) "Evento apagado." else "Erro ao apagar evento."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            },

            onParticipatingEventLeaveClick = { evento ->
                val currentUserId = session.userId
                if (currentUserId == null) {
                    Toast.makeText(context, "Utilizador não autenticado.", Toast.LENGTH_SHORT).show()
                    return@ProfileBottomSheet
                }
                scope.launch {
                    when (val result = viewModel.leaveEvent(evento.id, currentUserId)) {
                        is EventoRepository.JoinResult.Success -> {
                            viewModel.markLeft(evento.id)
                            viewModel.carregarEventos()
                            Toast.makeText(
                                context,
                                "Saíste do evento ${evento.title}.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is EventoRepository.JoinResult.Error -> {
                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                        }

                        is EventoRepository.JoinResult.AlreadyJoined -> {
                            Toast.makeText(
                                context,
                                "Estado inconsistente ao sair do evento.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },

            onEditProfileClick = {},
            onChangePasswordClick = {},

            onLogoutClick = {
                viewModel.logout()
                (context as? Activity)?.let { activity ->
                    val intent = Intent(activity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    activity.startActivity(intent)
                    activity.finish()
                }
            }
        )
    }
}
