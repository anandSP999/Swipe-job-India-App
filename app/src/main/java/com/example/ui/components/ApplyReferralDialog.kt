package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Job
import com.example.ui.theme.SwipePrimary
import com.example.ui.theme.SwipeRejectRed

@Composable
fun ApplyReferralDialog(
    job: Job,
    onDismiss: () -> Unit,
    onSubmit: (referralCode: String) -> Unit
) {
    var referCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = "Referral",
                tint = SwipePrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        },
        title = {
            Text(
                text = "Almost Done!",
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Were you referred by a friend? Enter their refer code below to help them earn cash rewards! Or simply skip.",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = referCode,
                    onValueChange = { referCode = it.uppercase() },
                    label = { Text("Refer Code (Optional)") },
                    placeholder = { Text("e.g. AMIT2023") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "*You cannot use your own referral code.",
                    fontSize = 11.sp,
                    color = SwipeRejectRed
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(referCode.trim()) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SwipePrimary)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Submit", modifier = Modifier.padding(end = 4.dp))
                Text(text = "Submit Application")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onSubmit("") },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Skip")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
