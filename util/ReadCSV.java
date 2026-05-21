package util;

import model.Player;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadCSV {
    public Map<String, List<Player>> readFiles(String[] files, String[] keys){

        Map<String, List<Player>> players = new HashMap<>();
        String line;

        for(int i = 0; i< files.length; i++){
            try (BufferedReader br = new BufferedReader(new FileReader(files[i]))) {
                List<Player> aux = new ArrayList<>();
                while ((line = br.readLine()) != null){
                    String[] data = line.split(",");
                    aux.add(new Player(data[1], data[11], Double.parseDouble(data[10]), Integer.parseInt(data[8]),
                            data[6], data[7]));
                }
                players.put(keys[i], aux);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return players;
    }
}
