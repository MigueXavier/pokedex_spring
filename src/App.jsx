import { useEffect, useState } from 'react'

import {
  atualizarPokemon,
  criarPokemon,
  listarPokemons,
  listarTipos,
  removerPokemon,
} from './api'
import PokemonForm from './PokemonForm'
import './App.css'

const imagem = (id) =>
  `https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png`

const STATS = [
  ['hp', 'HP'],
  ['attack', 'Attack'],
  ['defense', 'Defense'],
  ['specialAttack', 'Special Attack'],
  ['specialDefense', 'Special Defense'],
  ['speed', 'Speed'],
]

function App() {
  const [pokemons, setPokemons] = useState([])
  const [tipos, setTipos] = useState([])
  const [busca, setBusca] = useState('')
  const [tipo, setTipo] = useState('')
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [detalhe, setDetalhe] = useState(null)
  const [formulario, setFormulario] = useState(null) // null = fechado, {} = novo, pokemon = edição

  const [versao, setVersao] = useState(0)
  const recarregar = () => setVersao((v) => v + 1)

  useEffect(() => {
    let ativo = true
    Promise.all([listarPokemons(), listarTipos()])
      .then(([listaPokemons, listaTipos]) => {
        if (!ativo) return
        setPokemons(listaPokemons)
        setTipos(listaTipos)
        setErro('')
      })
      .catch(() => {
        if (ativo) setErro('Não foi possível carregar os Pokémon. Verifique se o backend está rodando.')
      })
      .finally(() => {
        if (ativo) setCarregando(false)
      })
    return () => {
      ativo = false
    }
  }, [versao])

  const filtrados = pokemons.filter(
    (p) =>
      p.name.toLowerCase().includes(busca.trim().toLowerCase()) &&
      (tipo === '' || p.types.includes(tipo)),
  )

  const salvar = async (dados) => {
    if (dados.id) await atualizarPokemon(dados.id, dados)
    else await criarPokemon(dados)
    setFormulario(null)
    setDetalhe(null)
    recarregar()
  }

  const excluir = async (p) => {
    if (!window.confirm(`Excluir ${p.name}?`)) return
    try {
      await removerPokemon(p.id)
      setDetalhe(null)
      recarregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <div className="pokedex">
      <header className="barra">
        <input
          type="search"
          placeholder="Pesquisar Pokémon..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />
        <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
          <option value="">Todos os tipos</option>
          {tipos.map((t) => (
            <option key={t.id} value={t.name}>
              {t.name}
            </option>
          ))}
        </select>
        <button onClick={() => setFormulario({})}>+ Novo Pokémon</button>
      </header>

      {erro && <p className="erro">{erro}</p>}
      {carregando && <p>Carregando...</p>}
      {!carregando && !erro && filtrados.length === 0 && <p>Nenhum Pokémon encontrado.</p>}

      <div className="pokemon-grid">
        {filtrados.map((pokemon) => (
          <div className="pokemon-card" key={pokemon.id} onClick={() => setDetalhe(pokemon)}>
            <h1>{pokemon.name}</h1>
            <img
              src={imagem(pokemon.id)}
              alt={pokemon.name}
              onError={(e) => (e.currentTarget.style.visibility = 'hidden')}
            />
            <div className="tipos">
              {pokemon.types.map((t) => (
                <span className="tipo" key={t}>
                  {t}
                </span>
              ))}
            </div>
            <ul>
              {STATS.map(([campo, rotulo]) => (
                <li key={campo}>
                  {rotulo}: {pokemon[campo]}
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>

      {detalhe && (
        <div className="modal-fundo" onClick={() => setDetalhe(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>
              #{detalhe.id} {detalhe.name}
            </h2>
            <img
              className="detalhe-img"
              src={imagem(detalhe.id)}
              alt={detalhe.name}
              onError={(e) => (e.currentTarget.style.visibility = 'hidden')}
            />
            <div className="tipos">
              {detalhe.types.map((t) => (
                <span className="tipo" key={t}>
                  {t}
                </span>
              ))}
            </div>
            {STATS.map(([campo, rotulo]) => (
              <div className="stat" key={campo}>
                <span>{rotulo}</span>
                <div className="stat-barra">
                  <div style={{ width: `${Math.min(detalhe[campo] / 255, 1) * 100}%` }} />
                </div>
                <strong>{detalhe[campo]}</strong>
              </div>
            ))}
            <div className="modal-acoes">
              <button onClick={() => setDetalhe(null)}>Fechar</button>
              <button onClick={() => setFormulario(detalhe)}>Editar</button>
              <button className="perigo" onClick={() => excluir(detalhe)}>
                Excluir
              </button>
            </div>
          </div>
        </div>
      )}

      {formulario && (
        <PokemonForm
          key={formulario.id ?? 'novo'}
          pokemon={formulario.id ? formulario : null}
          tipos={tipos}
          onSave={salvar}
          onCancel={() => setFormulario(null)}
        />
      )}
    </div>
  )
}

export default App
