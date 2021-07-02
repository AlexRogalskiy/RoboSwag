package ru.touchin.roboswag.mvi_arch.core

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.touchin.extensions.setOnRippleClickListener
import ru.touchin.roboswag.mvi_arch.di.ViewModelAssistedFactory
import ru.touchin.roboswag.mvi_arch.di.ViewModelFactory
import ru.touchin.roboswag.mvi_arch.marker.ViewAction
import ru.touchin.roboswag.mvi_arch.marker.ViewState
import ru.touchin.roboswag.navigation_base.activities.BaseActivity
import ru.touchin.roboswag.navigation_base.fragments.BaseFragment
import ru.touchin.roboswag.navigation_base.fragments.EmptyState
import javax.inject.Inject

/**
 *  Base [Fragment] to use in MVI architecture.
 *
 *  @param NavArgs Type of arguments class of this screen.
 *  It must implement [NavArgs] interface provided by navigation library that is a part of Google Jetpack.
 *  An instance of this class is generated by [SafeArgs](https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args)
 *  plugin according to related configuration file in navigation resource folder of your project.
 *
 *  @param State Type of view state class of this screen.
 *  It must implement [ViewState] interface. Usually it's a data class that presents full state of current screen's view.
 *  @see [ViewState] for more information.
 *
 *  @param Action Type of view actions class of this screen.
 *  It must implement [Action] interface. Usually it's a sealed class that contains classes and objects representing
 *  view actions of this view, e.g. button clicks, text changes, etc.
 *  @see [Action] for more information.
 *
 *  @param VM Type of view model class of this screen.
 *  It must extends [MviViewModel] class with the same params.
 *  @see [MviViewModel] for more information.
 *
 *  @author Created by Max Bachinsky and Ivan Vlasov at Touch Instinct.
 */
abstract class MviFragment<NavArgs, State, Action, VM>(
        @LayoutRes layout: Int,
        navArgs: NavArgs = EmptyState as NavArgs
) : BaseFragment<BaseActivity>(layout)
        where NavArgs : Parcelable,
              State : ViewState,
              Action : ViewAction,
              VM : MviViewModel<NavArgs, Action, State> {

    companion object {
        const val INIT_ARGS_KEY = "INIT_ARGS"
    }

    /**
     * Use [viewModel] extension to get an instance of your view model class.
     */
    protected abstract val viewModel: VM

    /**
     * Used for smooth view model injection to this class.
     */
    @Inject
    lateinit var viewModelMap: MutableMap<Class<out ViewModel>, ViewModelAssistedFactory<out ViewModel>>

    init {
        arguments?.putParcelable(INIT_ARGS_KEY, navArgs) ?: let {
            arguments = bundleOf(INIT_ARGS_KEY to navArgs)
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner, Observer(this::renderState))
    }

    /**
     * Use this method to subscribe on view state changes.
     *
     * You should render view state here.
     *
     * Must not be called before [onAttach] and after [onDetach].
     */
    protected open fun renderState(viewState: State) {}

    /**
     * Use this method to dispatch view actions to view model.
     */
    protected fun dispatchAction(actionProvider: () -> Action) {
        viewModel.dispatchAction(actionProvider.invoke())
    }

    /**
     * Use this method to dispatch view actions to view model.
     */
    protected fun dispatchAction(action: Action) {
        viewModel.dispatchAction(action)
    }

    protected fun addOnBackPressedCallback(actionProvider: () -> Action) {
        addOnBackPressedCallback(actionProvider.invoke())
    }

    protected fun addOnBackPressedCallback(action: Action) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dispatchAction(action)
            }
        })
    }

    /**
     * Lazily provides view model of this screen with transmitted arguments if exist.
     *
     * Value of this lazily providing must not be accessed before [onAttach] and after [onDetach].
     */
    @MainThread
    protected inline fun <reified ViewModel : VM> viewModel(): Lazy<ViewModel> =
            lazy {
                val fragmentArguments = arguments ?: bundleOf()

                ViewModelProvider(
                        viewModelStore,
                        ViewModelFactory(viewModelMap, this, fragmentArguments)
                ).get(ViewModel::class.java)
            }

    /**
     * Simple extension for dispatching view events to view model with on click.
     */
    protected fun View.dispatchActionOnClick(actionProvider: () -> Action) {
        setOnClickListener { dispatchAction(actionProvider) }
    }

    /**
     * Simple extension for dispatching view events to view model with on click.
     */
    protected fun View.dispatchActionOnClick(action: Action) {
        setOnClickListener { dispatchAction(action) }
    }

    /**
     * Simple extension for dispatching view events to view model with on ripple click.
     */
    protected fun View.dispatchActionOnRippleClick(actionProvider: () -> Action) {
        setOnRippleClickListener { dispatchAction(actionProvider) }
    }

    /**
     * Simple extension for dispatching view events to view model with on ripple click.
     */
    protected fun View.dispatchActionOnRippleClick(action: Action) {
        setOnRippleClickListener { dispatchAction(action) }
    }

}
