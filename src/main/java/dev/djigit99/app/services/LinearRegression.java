package dev.djigit99.app.services;

import dev.djigit99.app.model.RawData;
import javafx.util.Pair;

import java.util.List;

public class LinearRegression {

    public static Pair<Double, Double> calcRegressionCoef(List<RawData> rawData) {
        int n = rawData.size();
        double sigmaX = 0., sigmaY = 0., sigmaXY = 0.;
        double sigmaSqrX = 0.;
        double sqrSigmaX;

        for (RawData rdata: rawData) {
            double x = rdata.getTimestamp();
            double y = rdata.getClosedPrice();
            sigmaX += x;
            sigmaY += y;
            sigmaXY += x * y;
            sigmaSqrX += x * x;
        }
        sqrSigmaX = sigmaX * sigmaX;

        double a = (n * sigmaXY - sigmaX * sigmaY) / (n * sigmaSqrX - sqrSigmaX);
        double b = (sigmaY - a * sigmaX) / n;

        return new Pair<>(a, b);
    }
}
