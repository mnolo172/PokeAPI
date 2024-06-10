package com.example.pokeapi.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapi.R
import com.example.pokeapi.api.PokeResult
import com.example.pokeapi.databinding.PokeListBinding

class PokeListAdapter(private val pokemonClick: (Int) -> Unit) :
    RecyclerView.Adapter<PokeListAdapter.SearchViewHolder>() {

    private var pokemonList: List<PokeResult> = emptyList()
    private var favoriteClickListener: ((Int, Boolean) -> Unit)? = null

    fun setData(list: List<PokeResult>) {
        pokemonList = list
        notifyDataSetChanged()
    }

    fun setFavoriteClickListener(listener: (Int, Boolean) -> Unit) {
        favoriteClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = PokeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pokemonList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val binding = holder.binding
        val pokemon = pokemonList[position]

        binding.pokemonText.text = "#${position + 1} - ${pokemon.name}"

        // Cambiar el icono del botón de favoritos según el estado del Pokémon
        binding.favoriteButton.setImageResource(if (pokemon.isFavorite) R.drawable.ic_star else R.drawable.ic_star_outline)

        binding.favoriteButton.setOnClickListener {
            val isFavorite = !pokemon.isFavorite
            pokemon.isFavorite = isFavorite
            favoriteClickListener?.invoke(pokemon.id, isFavorite)
            notifyItemChanged(holder.adapterPosition)
        }

        holder.itemView.setOnClickListener { pokemonClick(pokemon.id) }
    }

    class SearchViewHolder(val binding: PokeListBinding) : RecyclerView.ViewHolder(binding.root)
}
