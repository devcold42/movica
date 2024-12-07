package `in`.devcold.movica.ui

data class ListUiState<T>(
    val baseUiState: BaseUiState = BaseUiState.Idle,
    val items: List<T> = listOf()
)