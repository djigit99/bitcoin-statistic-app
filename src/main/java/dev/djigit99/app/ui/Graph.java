package dev.djigit99.app.ui;

import dev.djigit99.app.model.RawData;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Graph extends JPanel {

    private static final int MAX_PRICE = 60_000;
    private static final int PREF_W = 800;
    private static final int PREF_H = 650;
    private static final int BORDER_GAP = 30;
    private static final Color GRAPH_COLOR = Color.green;
    private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
    private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
    private static final int GRAPH_POINT_WIDTH = 2;
    private static final int GRAPH_HATCH_WIDTH = 10;
    private static final int Y_HATCH_CNT = 10;
    private final List<RawData> rawData;
    private final Pair<Double, Double> coef;

    public Graph(List<RawData> rawData, Pair<Double, Double> coef) {
        this.rawData = rawData;
        this.coef = coef;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2 = (Graphics2D) g;

        // draw title
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g2.drawString("BTCUSD Graph for last weak.", getWidth() / 2, BORDER_GAP);
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 10));

        // create x and y axes
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

        // create hatch marks for y axis.
        for (int i = 0; i < Y_HATCH_CNT; i++) {
            int x0 = BORDER_GAP;
            int x1 = GRAPH_HATCH_WIDTH + BORDER_GAP;
            int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
            int y1 = y0;
            g2.drawLine(x0, y0, x1, y1);
            g2.drawString(String.valueOf((i+1) * MAX_PRICE / Y_HATCH_CNT), BORDER_GAP / 2, y0);
        }

        // and for x axis
        for (int i = 0; i < rawData.size() - 1; i++) {
            int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (rawData.size() - 1) + BORDER_GAP;
            int x1 = x0;
            int y0 = getHeight() - BORDER_GAP;
            int y1 = y0 - GRAPH_HATCH_WIDTH;
            g2.drawLine(x0, y0, x1, y1);
        }

        double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (rawData.size() - 1);
        double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (MAX_PRICE - 1);

        g2.setColor(GRAPH_POINT_COLOR);
        for (RawData rdata : rawData) {
            int x = (int) (rdata.getTimestamp() * xScale - GRAPH_POINT_WIDTH / 2 + BORDER_GAP);
            int y = (int) ((MAX_PRICE - rdata.getClosedPrice()) * yScale) + BORDER_GAP;
            System.out.println("x: " + x + ", y: " + y + ", price: " + rdata.getClosedPrice());
            g2.fillOval(x, y, GRAPH_POINT_WIDTH, GRAPH_POINT_WIDTH);
        }

        g2.setColor(GRAPH_COLOR);
        double y0_p = coef.getKey() * rawData.get(0).getTimestamp() + coef.getValue(),
                yn_p = coef.getKey() * rawData.get(rawData.size()-1).getTimestamp() + coef.getValue();
        int x0 = (int)(rawData.get(0).getTimestamp() * xScale + BORDER_GAP),
                xn = (int)(rawData.get(rawData.size() - 1).getTimestamp() * xScale + BORDER_GAP);
        int y0 = (int) ((MAX_PRICE - y0_p) * yScale) + BORDER_GAP,
                yn = (int) ((MAX_PRICE - yn_p) * yScale) + BORDER_GAP;
        g2.drawLine(x0, y0, xn, yn);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }
}
