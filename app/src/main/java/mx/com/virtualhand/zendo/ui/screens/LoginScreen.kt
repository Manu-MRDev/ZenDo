package mx.com.virtualhand.zendo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mx.com.virtualhand.zendo.R

@Composable
fun LoginScreen(
    onGoogleLogin: () -> Unit,
    onGithubLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .systemBarsPadding()
            .imePadding(), // <-- agrega esto
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Zendo Logo",
                modifier = Modifier.size(120.dp)
            )

            Text("Bienvenido a Zendo", style = MaterialTheme.typography.headlineMedium)
            Text("Inicia sesión para continuar", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Google
            Button(
                onClick = onGoogleLogin,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(painter = painterResource(R.drawable.ic_google), contentDescription = "Google", tint = Color.Unspecified)
                Spacer(Modifier.width(8.dp))
                Text("Continuar con Google", color = Color.Black)
            }

            // Botón GitHub
            Button(
                onClick = onGithubLogin,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(painter = painterResource(R.drawable.ic_github), contentDescription = "GitHub", tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Continuar con GitHub", color = Color.White)
            }
        }
    }
}