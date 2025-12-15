package com.shoaib.demodatadog.presentation.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.shoaib.demodatadog.R
import com.shoaib.demodatadog.databinding.ActivityArticleDetailBinding
import com.shoaib.demodatadog.domain.model.Article
import com.shoaib.demodatadog.util.DateFormatter
import com.shoaib.demodatadog.util.DatadogTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticleDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleDetailBinding
    private val viewModel: ArticleDetailViewModel by viewModels()
    private lateinit var article: Article

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        article =  intent.getParcelableExtra<Article>(EXTRA_ARTICLE) ?: return

        DatadogTracker.startScreen(
            "article_detail",
            "Article Detail",
            mapOf(
                "article_id" to article.id,
                "article_title" to article.title,
                "article_source" to (article.sourceName ?: "unknown")
            )
        )
        DatadogTracker.trackArticleViewed(article.id, article.title, article.sourceName)

        setupEdgeToEdge()
        setupToolbar()
        setupViews()
        observeViewModel()
        viewModel.checkFavorite(article.id)
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        val appBarLayout = binding.root.findViewById<com.google.android.material.appbar.AppBarLayout>(R.id.appBarLayout)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, v.paddingBottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.favoriteFab) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = v.layoutParams as android.view.ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = systemBars.bottom + 16
            v.layoutParams = layoutParams
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViews() {
        binding.titleText.text = article.title
        binding.descriptionText.text = article.description
        binding.contentText.text = article.content ?: article.description
        binding.sourceText.text = article.sourceName
        binding.publishedText.text = DateFormatter.formatDate(article.publishedAt)
        binding.imageView.load(article.urlToImage) {
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }

        binding.favoriteFab.setOnClickListener {
            DatadogTracker.trackButtonClick(
                "favorite",
                mapOf(
                    "article_id" to article.id,
                    "article_title" to article.title,
                    "current_favorite_state" to viewModel.isFavorite.value.toString()
                )
            )
            viewModel.toggleFavorite(article)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                binding.favoriteFab.setImageResource(
                    if (isFavorite) android.R.drawable.btn_star_big_on
                    else android.R.drawable.btn_star_big_off
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_article_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                shareArticle()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareArticle() {
        DatadogTracker.trackButtonClick(
            "share",
            mapOf(
                "article_id" to article.id,
                "article_title" to article.title
            )
        )
        DatadogTracker.trackArticleShared(article.id, article.title, "system_share")
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, article.title)
            putExtra(Intent.EXTRA_TEXT, article.url)
        }
        startActivity(Intent.createChooser(intent, "Share article"))
    }

    override fun onDestroy() {
        DatadogTracker.stopScreen("article_detail")
        super.onDestroy()
    }

    companion object {
        const val EXTRA_ARTICLE = "extra_article"
    }
}

