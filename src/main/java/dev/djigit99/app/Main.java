package dev.djigit99.app;

import dev.djigit99.app.model.RawData;
import dev.djigit99.app.ui.Graph;
import dev.djigit99.app.services.LinearRegression;
import javafx.util.Pair;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static Logger logger = Logger.getLogger("dev.djigit99.app.Main");

    private static void createAndShowGui(List<RawData> rdata, Pair<Double, Double> coef) {
        Graph mainPanel = new Graph(rdata, coef);

        JFrame frame = new JFrame("BTCUSD Linear Regression");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        try {
            List<RawData> rawData = loadData();
            Pair<Double, Double> coef = LinearRegression.calcRegressionCoef(rawData);
            System.out.printf("A = %f, B = %f", coef.getKey(), coef.getValue());

            SwingUtilities.invokeAndWait(() -> createAndShowGui(rawData, coef));

        } catch (IOException e) {
            logger.log(Level.INFO, "Couldn't load the data.", e);
        } catch (InterruptedException | InvocationTargetException e) {
            logger.log(Level.INFO, "Unable to draw a graph", e);
        }


    }

    private static List<RawData> loadData() throws IOException {
        final int HOURS_IN_WEAK = 24 * 7;
        final URL url = new URL("https://www.cryptodatadownload.com/cdd/Binance_BTCUSDT_1h.csv");


        // Make sure that your JRE trusts a SSL certificate provided by host
        // Guide how to add a SSL certificate:
        // https://mkyong.com/webservices/jax-ws/suncertpathbuilderexception-unable-to-find-valid-certification-path-to-requested-target/

        InputStream responseStream = url.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(responseStream));

        ArrayList<RawData> rawDataList = new ArrayList<>();

        // skip first two lines: that's the headers
        br.readLine();
        br.readLine();

        String line = br.readLine();
        String[] attributes = line.split(",");
        long startTime = Long.parseLong(attributes[0]);
        double startPrice = Double.parseDouble(attributes[6]);
        rawDataList.add(new RawData(0, startPrice));

        while ((line = br.readLine()) != null) {
            try {
                attributes = line.split(",");
                long timestamp = Long.parseLong(attributes[0]) - startTime; // normalization
                double price = Double.parseDouble(attributes[6]);
                rawDataList.add(new RawData(timestamp, price));
            } catch (NumberFormatException e) { // skip old timestamp format (old data)
                //logger.log(Level.INFO, "Unable to parse row data.", e); // comment this to avoid spam
            }
        }
        responseStream.close();

        Collections.sort(rawDataList);
        rawDataList = new ArrayList<>( rawDataList.subList(rawDataList.size() - HOURS_IN_WEAK, rawDataList.size()) );

        long norm = rawDataList.get(0).getTimestamp();
        for (RawData rdata : rawDataList) {
            rdata.normalizeTimestamp(norm);
        }

        return rawDataList;
    }
}
