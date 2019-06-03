package com.bootdo.meituan.banma.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.primitives.Ints;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.springframework.util.StringUtils;

import java.awt.*;
import java.util.List;

/**
 * Created by majing06 on 2019/1/11.
 */
public class CoordinateUtil {
    private static final Log logger = LogFactory.getLog(CoordinateUtil.class);

    /**
     * 判断坐标是否在电子围栏范围内
     *
     * @param latitude
     * @param longitude
     * @param shippingArea
     * @return
     */
    public static boolean isInPolygonalArea(long latitude, long longitude, String shippingArea) {
        if (!StringUtils.hasText(shippingArea)) {
            return false;
        }

        //处理输入为json的情况
        if (shippingArea.indexOf('{') != -1) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode node = objectMapper.readTree(shippingArea);
                List<ShippingAreaPoint> shippingAreaPointList = objectMapper.readValue(node, new TypeReference<List<ShippingAreaPoint>>() {
                });
                Polygon polygon = new Polygon();
                for (ShippingAreaPoint point : shippingAreaPointList) {
                    if (!StringUtils.hasText(point.getX()) || !isNumeric(point.getX()) || !StringUtils.hasText(point.getY()) || !isNumeric(point.getY())) {
                        continue;
                    }
                    polygon.addPoint(((Long) Long.parseLong(point.getX())).intValue(), ((Long) Long.parseLong(point.getY())).intValue());
                }

                if (polygon.contains(((Long) latitude).intValue(), ((Long) longitude).intValue())) {
                    return true;
                }
                return false;

            } catch (Exception e) {
                logger.error("isInPolygonalArea# check point in polygonal area failed! ", e);
                return false;
            }
        }

        try {
            //处理输入为非json的情况
            String[] polyvertexs = shippingArea.split(":");
            outer:
            for (String polyvertex : polyvertexs) {
                String[] vertexs = removeBrackets(polyvertex).split(";");
                if (vertexs.length < 1) {
                    continue;
                }

                Polygon polygon = new Polygon();
                for (String vertex : vertexs) {
                    String[] point = vertex.split(",");
                    if (point.length < 2 || !StringUtils.hasText(point[0].trim()) || !StringUtils.hasText(point[1].trim()) ||
                            (Ints.tryParse(point[0].trim()) == null) || (Ints.tryParse(point[1].trim()) == null)) {
                        continue outer;
                    }
                    polygon.addPoint(((Long) Long.parseLong(point[0].trim())).intValue(), ((Long) Long.parseLong(point[1].trim())).intValue());
                }

                if (polygon.contains(((Long) latitude).intValue(), ((Long) longitude).intValue())) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            logger.error("isInPolygonalArea# check point in polygonal area failed! ", e);
            return false;
        }
    }

    public static String removeBrackets(String str) {
        int start = str.indexOf('(');
        if (start == -1) {
            start = 0;
        } else {
            start = start + 1;
        }

        int end = str.lastIndexOf(')');
        if (end == -1) {
            end = str.length();
        }

        return str.substring(start, end);
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static class ShippingAreaPoint {
        private String x; //longitude
        private String y; //latitude

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "ShippingAreaPoint{" +
                    "x='" + x + '\'' +
                    ", y='" + y + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
//        String s = "[{\"x\":39984996,\"y\":116305754},{\"x\":39985407,\"y\":116324658},{\"x\":39981050,\"y\":116324873},{\"x\":39980877,\"y\":116316901},{\"x\":39982620,\"y\":116316783},{\"x\":39982382,\"y\":116305904},{\"x\":39982365,\"y\":116305904}]";
        String s = "[{\"x\":39978929,\"y\":116308522},{\"x\":39977704,\"y\":116309026},{\"x\":39975829,\"y\":116309091},{\"x\":39976298,\"y\":116325088},{\"x\":39980984,\"y\":116324873},{\"x\":39980795,\"y\":116316901},{\"x\":39978773,\"y\":116317105},{\"x\":39978682,\"y\":116313007}]";
        System.out.println(isInPolygonalArea(39980836, 116320024, s));
    }
}
