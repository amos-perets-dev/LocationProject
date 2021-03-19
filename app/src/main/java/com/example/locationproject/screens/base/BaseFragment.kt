package com.example.locationproject.screens.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialogFragment
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : AppCompatDialogFragment()  {

    /**
     * Handel the subscribers
     */
    protected val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(getLayout(), container, false)
    }

    /**
     * Get the main layout in the fragment
     */
    @LayoutRes
    abstract fun getLayout() : Int

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

}