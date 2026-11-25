"""Converte o Script_Pokedex.sql nos CSVs de seed usados pelo backend.

Uso (na raiz do repositório):  python scripts/gerar_seed.py
"""
import re
from collections import defaultdict
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
SQL = (RAIZ / "Script_Pokedex.sql").read_text(encoding="utf-8")
SAIDA = RAIZ / "backend" / "src" / "main" / "resources" / "seed"


def linhas(tabela):
    """Retorna as tuplas do INSERT INTO <tabela> como listas de valores."""
    m = re.search(rf"INSERT INTO {tabela}\s*\(.*?\)\s*VALUES(.*?);", SQL, re.S)
    corpo = m.group(1)
    for t in re.finditer(r"\(((?:[^()']|'(?:[^']|'')*')*)\)", corpo):
        valores = re.findall(r"'((?:[^']|'')*)'|(-?\d+)", t.group(1))
        yield [s.replace("''", "'") if s or not n else int(n) for s, n in valores]


tipos = {i: nome for i, nome in linhas("Types")}
tipos_pokemon = defaultdict(list)
for id_pokemon, id_tipo in linhas("Rel_Pokemon_Type"):
    tipos_pokemon[id_pokemon].append(tipos[id_tipo])
pokemons_treinador = defaultdict(list)
for id_treinador, id_pokemon in linhas("Rel_Trainer_Pokemon"):
    pokemons_treinador[id_treinador].append(id_pokemon)

SAIDA.mkdir(parents=True, exist_ok=True)

with open(SAIDA / "pokemons.csv", "w", encoding="utf-8", newline="\n") as f:
    f.write("id,name,hp,attack,defense,specialAttack,specialDefense,speed,types\n")
    n = 0
    for p in linhas("Pokemon"):
        f.write(",".join(map(str, p)) + "," + "|".join(tipos_pokemon[p[0]]) + "\n")
        n += 1
print("pokemons:", n)

with open(SAIDA / "types.csv", "w", encoding="utf-8", newline="\n") as f:
    f.write("id,name\n")
    for i, nome in tipos.items():
        f.write(f"{i},{nome}\n")
print("types:", len(tipos))

with open(SAIDA / "trainers.csv", "w", encoding="utf-8", newline="\n") as f:
    f.write("id,name,badges,pokemonIds\n")
    n = 0
    for i, nome, insignias in linhas("Trainers"):
        f.write(f"{i},{nome},{insignias},{'|'.join(map(str, pokemons_treinador[i]))}\n")
        n += 1
print("trainers:", n)
