package com.example.pokeapi.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokeapi.api.ApiService
import com.example.pokeapi.api.PokeApiResponse
import com.example.pokeapi.api.PokeResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PokeListViewModel : ViewModel() {

    private val _pokemonList = MutableLiveData<List<PokeResult>>()
    val pokemonList: LiveData<List<PokeResult>> get() = _pokemonList

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: ApiService = retrofit.create(ApiService::class.java)

    // Conjunto para mantener los IDs de los Pokémon favoritos
    private val favoritePokemonIds = mutableSetOf<Int>()

    fun getPokemonList() {
        val call = service.getPokemonList(251, 0)

        call.enqueue(object : Callback<PokeApiResponse> {
            override fun onResponse(call: Call<PokeApiResponse>, response: Response<PokeApiResponse>) {
                response.body()?.results?.let { list ->
                    // Añadir ID extraído y estado de favorito a cada PokeResult
                    val results = list.map { pokeResult ->
                        val id = extractIdFromUrl(pokeResult.url)
                        pokeResult.copy(id = id, isFavorite = favoritePokemonIds.contains(id))
                    }
                    _pokemonList.postValue(results)
                }
            }

            override fun onFailure(call: Call<PokeApiResponse>, t: Throwable) {
                call.cancel()
            }
        })
    }

    private fun extractIdFromUrl(url: String): Int {
        return url.split("/".toRegex()).dropLast(1).last().toInt()
    }

    fun markAsFavorite(pokemonId: Int) {
        favoritePokemonIds.add(pokemonId)
        updateFavoriteStatus(pokemonId, true)
    }

    fun unmarkAsFavorite(pokemonId: Int) {
        favoritePokemonIds.remove(pokemonId)
        updateFavoriteStatus(pokemonId, false)
    }

    private fun updateFavoriteStatus(pokemonId: Int, isFavorite: Boolean) {
        val updatedList = _pokemonList.value?.map { pokeResult ->
            if (pokeResult.id == pokemonId) {
                pokeResult.copy(isFavorite = isFavorite)
            } else {
                pokeResult
            }
        }?.sortedByDescending { it.isFavorite }
        _pokemonList.postValue(updatedList)
    }
}
