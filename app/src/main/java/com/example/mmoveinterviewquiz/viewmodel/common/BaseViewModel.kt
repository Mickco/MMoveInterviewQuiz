package com.example.mmoveinterviewquiz.viewmodel.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseViewModel: ViewModel() {
    protected var loadingCount = AtomicInteger(0)

    protected fun AtomicInteger.start() {
        this.incrementAndGet()
        onLoadingCountChanged()
    }

    protected fun AtomicInteger.end() {
        this.decrementAndGet()
        onLoadingCountChanged()
    }

    protected open fun onLoadingCountChanged() {}

    protected fun launchLoadingScope(func: suspend CoroutineScope.() -> Unit) {
        loadingCount.start()
        viewModelScope.launch(Dispatchers.Main) {
            func(this)
            loadingCount.end()
        }
    }
}