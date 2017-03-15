package br.com.gpma.agenda;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Gustavo on 05/05/2016.
 */
public class WebClient {
    public String post(String json) {

        try {
            URL url = new URL("https://www.caelum.com.br/mobile");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("content-type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true); //usado para post

            PrintStream output = new PrintStream(con.getOutputStream());
            output.println(json);

            con.connect();
            Scanner scanner = new Scanner(con.getInputStream());
            String resposta = scanner.next();

            return resposta;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
