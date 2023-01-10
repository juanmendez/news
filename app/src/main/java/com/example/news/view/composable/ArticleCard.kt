package com.example.news.view.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.news.R

@Composable
fun ArticleCard(
    article: com.example.news.model.Article,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(id = R.dimen.card_padding_horizontal_size),
                top = dimensionResource(id = R.dimen.card_padding_vertical_size),
                end = dimensionResource(id = R.dimen.card_padding_horizontal_size),
                bottom = dimensionResource(id = R.dimen.card_padding_vertical_size)
            )
            .clickable(onClick = onClick)
            .background(color = Color(context.resources.getColor(R.color.card_background, null))),
        elevation = dimensionResource(id = R.dimen.card_elevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            if (article.imageUrl.isNotEmpty()) {
                val image = loadPicture(
                    context = context,
                    url = article.imageUrl,
                    defaultImage = DEFAULT_ARTICLE_IMAGE
                ).value

                image?.let { img ->
                    Image(
                        bitmap = img.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.article_image_height)),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.card_padding_horizontal_size),
                        top = dimensionResource(id = R.dimen.card_padding_vertical_size),
                        end = dimensionResource(id = R.dimen.card_padding_horizontal_size),
                        bottom = dimensionResource(id = R.dimen.card_padding_vertical_size)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight(),
                        color = Color(context.resources.getColor(R.color.gray_light, null)),
                        fontSize = dimensionResource(id = R.dimen.article_source_text_size).value.sp,
                        text = article.sourceName
                    )
                    Text(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .align(Alignment.CenterVertically),
                        color = Color(context.resources.getColor(R.color.gray_light, null)),
                        fontSize = dimensionResource(id = R.dimen.article_date_text_size).value.sp,
                        text = article.publishedDate
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = dimensionResource(id = R.dimen.margin_small_size)),
                    color = Color(context.resources.getColor(R.color.black, null)),
                    fontSize = dimensionResource(id = R.dimen.article_title_text_size).value.sp,
                    text = article.title,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (article.description.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = dimensionResource(id = R.dimen.margin_small_size)),
                        color = Color(context.resources.getColor(R.color.gray_dark, null)),
                        fontSize = dimensionResource(id = R.dimen.article_date_text_size).value.sp,
                        text = article.description,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ArticleCardPreview() {
    ArticleCard(
        article = com.example.news.model.Article(
            id = "2474abea-7584-486b-9f88-87a21870b0ec",
            query = "technology",
            sourceId = "",
            sourceName = "IGN",
            author = "John Smith",
            title = "XBOX Series X Launch Date",
            description = "description",
            url = "url",
            imageUrl = "imageUrl",
            publishedDate = "August 08, 2020",
            content = "content"
        )
    ) {}
}
