package to.sava.savatter.viewmodels

import kotlinx.coroutines.CoroutineScope

abstract class ViewModelBase() {
    protected lateinit var viewModelScope: CoroutineScope

    fun bindScope(scope: CoroutineScope) {
        viewModelScope = scope
    }
}
