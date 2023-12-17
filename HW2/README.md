# Преамбула

Было реализовано 2 способа QuickSort:

- SEQ - последовательный bfs
- PAR - none-deterministics bfs

# Результаты замеров

## SEQ

Среднее: `25519`

- `26587`
- `25536`
- `25751`
- `25191`
- `24533`



## PAR

Среднее: `18660`

- `19022`
- `19538`
- `18189`
- `18382`
- `18169`

# Сравнение реализаций


|     | ms     | ratio |
|-----|--------|-------|
| SEQ | 25519   | 1.0   |
| PAR | 18660   | 1.37  |


# Запуск

```shell
mkdir out
javac src/* -d out/
cd out
java -Xms2G -Xmx10G -Xss512k -XX:ActiveProcessorCount=4 Main
```