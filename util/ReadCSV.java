package util;

import model.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadCSV {
    public List<Player> readFile(String path){
        List<Player> players = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linha = br.readLine(); // pula a primeira linha
            while ((line = br.readLine()) != null){
                String[] data = line.split(",");
                players.add(new Player(data[1], data[11], Double.parseDouble(data[10]), Integer.parseInt(data[8])));
                //players.add(new Player(data[0],Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return players;
    }
}
