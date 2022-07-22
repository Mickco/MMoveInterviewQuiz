package com.example.mmoveinterviewquiz.viewmodel.common

import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseViewModel: ViewModel() {
    protected var loadingCount = AtomicInteger(0)

    protected fun AtomicInteger.start() {
        loadingCount.incrementAndGet()
        onLoadingCountChanged()
    }

    protected fun AtomicInteger.end() {
        loadingCount.decrementAndGet()
        onLoadingCountChanged()
    }

    protected open fun onLoadingCountChanged() {}

}