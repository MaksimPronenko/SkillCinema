package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.ViewedAdapter
import edu.skillbox.skillcinema.databinding.FragmentCollectionBinding
import edu.skillbox.skillcinema.models.FilmDbViewed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "Collection.Fragment"

private const val ARG_COLLECTION_NAME = "collectionName"

@AndroidEntryPoint
class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    private val viewedAdapter =
        ViewedAdapter(limited = true) { filmDbViewed -> onViewedItemClick(filmDbViewed) }

    @Inject
    lateinit var collectionViewModelFactory: CollectionViewModelFactory
    private val viewModel: CollectionViewModel by viewModels {
        collectionViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val collectionName = arguments?.getString(ARG_COLLECTION_NAME) ?: ""
        viewModel.loadData(collectionName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listPageRecycler.adapter = viewedAdapter

        val dividerItemDecorationVertical = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val dividerItemDecorationHorizontal =
            DividerItemDecoration(context, RecyclerView.HORIZONTAL)
        context?.let { ContextCompat.getDrawable(it, R.drawable.divider_drawable) }
            ?.let {
                dividerItemDecorationVertical.setDrawable(it)
                dividerItemDecorationHorizontal.setDrawable(it)
            }
        binding.listPageRecycler.addItemDecoration(dividerItemDecorationVertical)
        binding.listPageRecycler.addItemDecoration(dividerItemDecorationHorizontal)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

//        binding.mainButton.setOnClickListener {
//            findNavController().navigate(R.id.action_CollectionFragment_to_MainFragment)
//        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                binding.progress.isGone = false
                            }
                            else -> {
                                binding.progress.isGone = true
                                if (viewModel.collection.isNotBlank())
                                    binding.listName.text = viewModel.collection
                                viewModel.filmsListFlow.onEach {
                                    viewedAdapter.setData(it)
                                    Log.d(TAG, "viewedAdapter.setData. Размер= ${it.size}")
                                    Log.d(
                                        TAG,
                                        "viewedAdapter.itemCount = ${viewedAdapter.itemCount}"
                                    )
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                        }
                    }
            }
    }

    private fun onViewedItemClick(
        item: FilmDbViewed
    ) {
        val bundle = Bundle().apply {
            putInt(
                "filmId",
                item.filmDb.filmTable.filmId
            )
        }
        findNavController().navigate(
            R.id.action_CollectionFragment_to_FilmFragment,
            bundle
        )
    }
}