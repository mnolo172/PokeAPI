package com.example.pokeapi.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokeapi.databinding.ActivityMainBinding
import com.example.pokeapi.model.PokeListAdapter
import com.example.pokeapi.model.PokeListViewModel

class PokemonList : AppCompatActivity() {

    private lateinit var viewModel: PokeListViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var pokeListAdapter: PokeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PokeListViewModel::class.java]

        initUI()
    }

    private fun initUI() {
        binding.pokelistRecyclerView.layoutManager = LinearLayoutManager(this)

        pokeListAdapter = PokeListAdapter { pokemonId ->
            val intent = Intent(this, PokeInfoActivity::class.java)
            intent.putExtra("id", pokemonId)
            startActivity(intent)
        }

        binding.pokelistRecyclerView.adapter = pokeListAdapter

        // Manejar clics en el botón de favoritos
        pokeListAdapter.setFavoriteClickListener { pokemonId, isFavorite ->
            if (isFavorite) {
                viewModel.markAsFavorite(pokemonId)
            } else {
                viewModel.unmarkAsFavorite(pokemonId)
            }
        }

        viewModel.getPokemonList()
        viewModel.pokemonList.observe(this, Observer { list ->
            pokeListAdapter.setData(list)
        })
    }
}
