# MoviesSearchPos
A simple movie search site to ES Pos-BigData FASAM

## Dataset

O dataset a ser usado neste projeto é de filmes com suas caracteisticas e avaliações.

Os dados devem ser baixados do [kaggle](https://www.kaggle.com/rounakbanik/the-movies-dataset/data). Voce irá utilizar movies_metadata.cvs e ratings.csv;

O ratings.csv deverá ser processado por um script que adicione a localização (lat, lng) do usuário que fez a avaliação. Esse script esta disponível no diretório raíz do projeto (add_loc.py).

## add_loc.py

Para utilizar o add_loc.py, faça:

1. abra um terminal
1. navegue até a pasta deste projeto
1. certifique-se que o arquivo ratings.csv já foi baixado e esta nesta pasta
1. execute: `python add_loc.py ratings.csv`
1. será criado um novo arquivo chamado ratings_with_latlng.csv

No projeto deve ser usado o ratings_with_latlng.csv.


## Integração própria com o Elasticsearch

O Spring trás uma integração direta com o Elasticsearch.

Porém só funciona com a versão antiga do Elasticsearch (2.4.0).

Desta forma, se for usar a versão mais atualizada (5.4.0 por exemplo) não é possível a utilização.

No site do [mkyong](https://www.mkyong.com/spring-boot/spring-boot-spring-data-elasticsearch-example/) apresenta um exemplo simples de como funciona essa integração. 