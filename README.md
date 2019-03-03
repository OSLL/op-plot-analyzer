# op-plot-analyzer

Утилита analyzer обрабатывает именованный параметры:
	-about - выводит сообщение;
	--director_uniq_words [имя_режиссера]
	--filename [имя_файла] - (обязательный параметр) - название источника, который нужно парсить

В данный момент утилита построчно читает файл.



Работа с утилитой в командной строке:
КОМПИЛЯЦИЯ:
javac -cp ./jars/*: OnPlotAnalyzer.java 

ЗАПУСК:
java -cp ./jars/*: OnPlotAnalyzer --director_uniq_words hisname --filename plots.csv

