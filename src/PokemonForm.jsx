import { useState } from 'react'

const STATS = [
  ['hp', 'HP'],
  ['attack', 'Attack'],
  ['defense', 'Defense'],
  ['specialAttack', 'Special Attack'],
  ['specialDefense', 'Special Defense'],
  ['speed', 'Speed'],
]

function PokemonForm({ pokemon, tipos, onSave, onCancel }) {
  const [form, setForm] = useState(
    pokemon ?? {
      name: '',
      hp: 50,
      attack: 50,
      defense: 50,
      specialAttack: 50,
      specialDefense: 50,
      speed: 50,
      types: [],
    },
  )
  const [erro, setErro] = useState('')
  const [salvando, setSalvando] = useState(false)

  const alterarTipo = (indice, valor) => {
    const types = [...form.types]
    types[indice] = valor
    setForm({ ...form, types: types.filter(Boolean) })
  }

  const enviar = async (e) => {
    e.preventDefault()
    setErro('')
    setSalvando(true)
    try {
      await onSave({
        ...form,
        ...Object.fromEntries(STATS.map(([campo]) => [campo, Number(form[campo])])),
      })
    } catch (err) {
      setErro(err.message)
      setSalvando(false)
    }
  }

  return (
    <div className="modal-fundo" onClick={onCancel}>
      <form className="modal" onClick={(e) => e.stopPropagation()} onSubmit={enviar}>
        <h2>{pokemon ? `Editar ${pokemon.name}` : 'Cadastrar Pokémon'}</h2>

        <label>
          Nome
          <input
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
        </label>

        {STATS.map(([campo, rotulo]) => (
          <label key={campo}>
            {rotulo}
            <input
              type="number"
              min="0"
              max="255"
              value={form[campo]}
              onChange={(e) => setForm({ ...form, [campo]: e.target.value })}
              required
            />
          </label>
        ))}

        {[0, 1].map((i) => (
          <label key={i}>
            Tipo {i + 1}
            <select value={form.types[i] ?? ''} onChange={(e) => alterarTipo(i, e.target.value)}>
              <option value="">—</option>
              {tipos
                .filter((t) => t.name !== 'Typeless')
                .map((t) => (
                  <option key={t.id} value={t.name}>
                    {t.name}
                  </option>
                ))}
            </select>
          </label>
        ))}

        {erro && <p className="erro">{erro}</p>}

        <div className="modal-acoes">
          <button type="button" onClick={onCancel}>
            Cancelar
          </button>
          <button type="submit" disabled={salvando}>
            {salvando ? 'Salvando...' : 'Salvar'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default PokemonForm
