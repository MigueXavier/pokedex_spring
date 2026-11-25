const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

async function request(path, options) {
  const resp = await fetch(`${API_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!resp.ok) {
    let mensagem = `Erro ${resp.status}`
    try {
      const corpo = await resp.json()
      if (corpo.message) mensagem = corpo.message
      else if (corpo.detail) mensagem = corpo.detail
    } catch {
      // resposta sem corpo JSON
    }
    throw new Error(mensagem)
  }
  return resp.status === 204 ? null : resp.json()
}

export const listarPokemons = () => request('/pokemons')
export const listarTipos = () => request('/types')
export const criarPokemon = (pokemon) =>
  request('/pokemons', { method: 'POST', body: JSON.stringify(pokemon) })
export const atualizarPokemon = (id, pokemon) =>
  request(`/pokemons/${id}`, { method: 'PUT', body: JSON.stringify(pokemon) })
export const removerPokemon = (id) => request(`/pokemons/${id}`, { method: 'DELETE' })
