package com.example.mycbseguideimproved.ui.screen

import android.text.Html
import android.text.Spanned
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mycbseguideimproved.data.model.Content
import com.example.mycbseguideimproved.ui.viewmodel.ContentUiState
import com.example.mycbseguideimproved.ui.viewmodel.ContentViewModel

private val GradientColors = listOf(
    Color(0xFF1A237E), // Deep Indigo
    Color(0xFF0D47A1), // Deep Blue
    Color(0xFF01579B)  // Deep Cyan
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(
    viewModel: ContentViewModel,
    contentId: Int,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // Load content on first composition
    LaunchedEffect(contentId) {
        viewModel.loadContent(contentId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = GradientColors
                )
            )
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "CBSE",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp
                    )
            ) {
                when (uiState) {
                    is ContentUiState.Loading -> {
                        LoadingContentAnimation()
                    }
                    is ContentUiState.Success -> {
                        val content = (uiState as ContentUiState.Success).content
                        ContentView(content = content)
                    }
                    is ContentUiState.Error -> {
                        ContentErrorMessage(message = (uiState as ContentUiState.Error).message)
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingContentAnimation() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = Color.White,
            strokeWidth = 6.dp
        )
    }
}

@Composable
fun ContentErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
            )
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ContentView(content: Content) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Title Section
            Text(
                text = content.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Description Section
            if (content.description.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    )
                ) {
                    HtmlText(
                        html = content.description,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main content
            HtmlContent(
                htmlContent = content.content,
                modifier = Modifier.fillMaxWidth()
            )

            // Download section if available
            if (content.hasDownload && content.fileUrl != null) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Download Available",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = content.fileName ?: "PDF File",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        FilledTonalButton(onClick = { /* Open URL in browser */ }) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Download")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier
) {
    val spanned = remember(html) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT) as Spanned
    }
    
    Text(
        text = spanned.toString(),
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun HtmlContent(
    htmlContent: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = false
                settings.loadsImagesAutomatically = true
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
            }
        },
        update = { webView ->
            val cssStyle = """
                <style>
                    body {
                        font-family: 'Roboto', sans-serif;
                        line-height: 1.6;
                        color: #333333;
                        padding: 0;
                        margin: 0;
                    }
                    img {
                        max-width: 100%;
                        height: auto;
                    }
                    table {
                        border-collapse: collapse;
                        width: 100%;
                        margin: 16px 0;
                    }
                    th, td {
                        border: 1px solid #ddd;
                        padding: 8px;
                    }
                    th {
                        background-color: #f2f2f2;
                    }
                    h1, h2, h3, h4 {
                        color: #1A237E;
                    }
                    hr {
                        border: 0;
                        height: 1px;
                        background: #ddd;
                        margin: 16px 0;
                    }
                    a {
                        color: #01579B;
                        text-decoration: none;
                    }
                    ol, ul {
                        padding-left: 20px;
                    }
                </style>
            """.trimIndent()
            
            val formattedHtml = "<html><head>$cssStyle</head><body>$htmlContent</body></html>"
            webView.loadDataWithBaseURL(null, formattedHtml, "text/html", "UTF-8", null)
        }
    )
} 