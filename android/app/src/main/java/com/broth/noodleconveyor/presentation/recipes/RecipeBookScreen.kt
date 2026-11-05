package com.broth.noodleconveyor.presentation.recipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import com.broth.noodleconveyor.core.ui.AppIcons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.broth.noodleconveyor.R
import com.broth.noodleconveyor.core.di.AppViewModelFactory
import com.broth.noodleconveyor.core.theme.BgCream
import com.broth.noodleconveyor.core.theme.Cocoa
import com.broth.noodleconveyor.core.theme.CocoaSoft
import com.broth.noodleconveyor.core.theme.Mustard
import com.broth.noodleconveyor.core.theme.OutlineSand
import com.broth.noodleconveyor.core.ui.ClayCard
import com.broth.noodleconveyor.core.ui.ScreenScaffold
import com.broth.noodleconveyor.core.ui.ingredientDrawable
import com.broth.noodleconveyor.core.ui.spriteForSlot
import com.broth.noodleconveyor.presentation.decor.ShopHeader

private val RecipeOverlay = Brush.verticalGradient(
    listOf(Color(0xF2FFF9F0), Color(0xE6FFE8CE))
)

@Composable
fun RecipeBookRoute(onBack: () -> Unit) {
    val recipeViewModel: RecipeBookViewModel = viewModel(factory = AppViewModelFactory())
    val state by recipeViewModel.state.collectAsStateWithLifecycle()

    RecipeBookScreen(state = state, onBack = onBack)
}

@Composable
private fun RecipeBookScreen(state: RecipeUiState, onBack: () -> Unit) {
    ScreenScaffold(overlay = RecipeOverlay) {
        Column(modifier = Modifier.fillMaxSize()) {
            ShopHeader(
                title = stringResource(R.string.recipe_book),
                stamps = null,
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.rows, key = { it.recipe.id }) { row ->
                    RecipeRow(row = row)
                }
            }
        }
    }
}

@Composable
private fun RecipeRow(row: RecipeRowState) {
    val description = stringResource(
        if (row.unlocked) R.string.state_recipe_open else R.string.state_recipe_locked
    )

    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .semantics { stateDescription = description },
        shape = RoundedCornerShape(22.dp),
        fill = BgCream,
        borderColor = if (row.unlocked) Mustard else OutlineSand,
        elevation = if (row.unlocked) 8.dp else 4.dp,
        highlightHeight = 18.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (row.unlocked) {
                Image(
                    painter = painterResource(spriteForSlot(row.recipe.id)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(44.dp)
                )
            } else {
                Icon(
                    imageVector = AppIcons.Lock,
                    contentDescription = null,
                    tint = CocoaSoft,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(row.recipe.nameRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    color = Cocoa,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (row.unlocked) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        row.recipe.ingredients.forEach { ingredient ->
                            Image(
                                painter = painterResource(ingredientDrawable(ingredient)),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(
                            R.string.recipe_locked_fmt,
                            row.recipe.unlockAtBowls
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = CocoaSoft,
                        maxLines = 1
                    )
                }
            }

            if (row.unlocked) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(row.recipe.noteRes),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = CocoaSoft,
                    maxLines = 3,
                    modifier = Modifier.width(112.dp)
                )
            }
        }
    }
}
