# BBQ Delight (Fabric 1.21.1)

Mod de churrasco para Fabric 1.21.1, escrito do zero (remake independente inspirado no Barbeque's Delight).
Nao usa codigo, texturas ou modelos do mod original e nao depende do Farmer's Delight.

## Como compilar

Requisitos: JDK 21 e Gradle (ou abra a pasta no IntelliJ IDEA, que importa o projeto sozinho).

    gradle wrapper --gradle-version 8.10.2   # gera o gradlew (uma vez)
    ./gradlew build                          # jar em build/libs/
    ./gradlew runClient                      # abre o jogo para testar

As versoes do Fabric (loader, API, mappings) estao em `gradle.properties`.
Se algo nao resolver, confira as versoes atuais para 1.21.1 em https://fabricmc.net/develop.

## Como jogar

- **Bacia de Ingredientes**: clique com ingredientes (um por clique, ate 4). Com um graveto na mao, clique para montar o espeto.
  - Espeto de carne: 2 carnes bovinas crua; de frango: 2 frangos crus; de legumes: cenoura + batata.
  - Mao vazia mostra o conteudo; agachado + mao vazia tira o ultimo ingrediente.
- **Grelha**: coloque sobre uma fogueira acesa (ou fogo/lava/magma) e clique com o espeto cru. Espere a fumaca engrossar (~10 s),
  agache com a mao vazia e clique para virar. Mais ~10 s e fica pronto. Clique normal tira o espeto.
  Se nao virar a tempo, ou se deixar pronto por muito tempo, o espeto queima.
- **Temperos**: cominho (cura ao comer), pimenta (come na metade do tempo), pimenta-malagueta (machuca, mas pode comer com a fome cheia).
  Receita sem forma: espeto cozido + tempero. Vendidos pelo acougueiro.
- Receitas: Grelha = barras de ferro + lingotes; Bacia = tijolos.
- Todos os itens estao na aba criativa "BBQ Delight".

## Tempos

Ficam em `GrillBlockEntity` (constantes `FLIP_AFTER`, `FLIP_GRACE`, `SECOND_SIDE`, `BURN_AFTER_COOKED`, em ticks; 20 ticks = 1 s).

## Troque o nome

O id do mod e o pacote sao `bbqdelight` / `com.example.bbqdelight`. Renomeie antes de publicar.
