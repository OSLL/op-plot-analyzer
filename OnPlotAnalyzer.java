import com.opencsv.*;

import java.io.FileReader;
import java.io.IOException;

import java.io.File;
import java.io.FileNotFoundException;
 
class OnPlotAnalyzer{
	private static String director = "";	//имя режиссера
	private static String filename = "";	//имя файла источника
	private static String genre = "";	//название жанра
	private static String country = "";	//название страны
	private static int mode = 0;		//режим работы программы:
						//режим 1:	--film_uniq_words		
						//		--director_uniq_words
						//		--country_uniq_words
						//		--genre_uniq_words
						//		сочетание режимов (director, country, genre)
						//режим 2:
						//режим 3:

	public static void main(String[] args) {
		mode = modeDetector(args); //определение режима выполнения программы
		if ((mode >= 1) && (mode <=3)){
			if (filename.equals("")){   //проверка на наличие названия файла
				System.out.println("The file name has not been set. Please restart the program and specify a source name in --filename argument.");
			}else{
				if(mode == 1){
					int counter = 0;
					try (CSVReader csvReader = new CSVReader(new FileReader(filename));) 						{
					    	String[] values = null;
					    	while (((values = csvReader.readNext()) != null) && (counter < 100000)) {
								//String arrayString = Arrays.toString(values);
								String str1 = String.join(",", values);
								System.out.println(str1);
								System.out.println("");
								System.out.println("");
								System.out.println("");
								System.out.println("");
					    		//records.add(Arrays.asList(values));		//Тестовый вывод в консоль
								/*System.out.println(values[0] + " ");
								System.out.println(values[1] + "       ");
								System.out.println(values[2] + " ");
								System.out.println(values[3] + "      ");
								System.out.println(values[4] + " ");
								System.out.println(values[5] + " ");
								System.out.println(values[6] + "      ");
								System.out.println(values[7] + " ");
								System.out.println("");
								System.out.println("");
								System.out.println("");
								System.out.println("");*/
								counter++;     
    						}
					}catch(IOException e){}
					System.out.println("");
					System.out.println(counter);		//Количество выведенных записей
				}
			}
		}else{
			System.out.println("Something went wrong...");
		}
	}




	public static int modeDetector(String[] args){ //метод определяет режим, в котором будет выполняться программа
		int i = 0;
		int mode = -1;
		
		while(i != args.length) {
            		if (args[i].equals("-about")){
				System.out.println("This a kino analyser!");
			}
			if (args[i].equals("--director_uniq_words")){
				i++;
				director = args[i];
				mode = 1;
				//System.out.println(director);
			}
			if (args[i].equals("--filename")){
				i++;
				filename = args[i];
			}
			i++;
        }
		return mode;
	}
}
