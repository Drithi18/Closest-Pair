import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

public class closestpair {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please provide the input file name as a command-line argument.");
            System.exit(1);
        }

        String inputFileName = args[0];

        try {
            // Open the input file for reading
            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
            String line;
            int setNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("** Set No " + setNumber)) {
                    System.out.printf("Set No %d: ", setNumber);
                    ArrayList<Point> points = new ArrayList<>();

                    // Parse and extract points from the input file
                    while ((line = reader.readLine()) != null && !line.equals("------------------------------")) {
                        if (line.isEmpty()) {
                            continue;
                        }
                        Scanner scanner = new Scanner(line);
                        scanner.useDelimiter("[(), ]+");
                        double x = scanner.nextDouble();
                        double y = scanner.nextDouble();
                        points.add(new Point(x, y));
                    }

                    if (points.size() < 2) {
                        System.err.println("Set " + setNumber + " has less than 2 points.");
                        continue;
                    }

                    // Calculate and display the closest pair of points for the current set
                    long startTime = System.currentTimeMillis();
                    Pair closestPair = findClosestPair(points);
                    long endTime = System.currentTimeMillis();
                    double distance = closestPair.distance();

                    System.out.printf("%d points\n", points.size());
                    System.out.printf(" (%10.5f, %10.5f)-(%10.5f, %10.5f)\n",
                            closestPair.point1.x, closestPair.point1.y, closestPair.point2.x, closestPair.point2.y);
                    System.out.printf(" distance = %11.6f (%d ms)\n\n", distance, endTime - startTime);

                    setNumber++;
                }
            }
            System.out.println("**** Asg 6 by DRITHI MADAGANI ****");

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Point Class - Represents a Point with x and y Coordinates
    static class Point {
        double x, y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        // Calculate and return the Euclidean distance between this point and another
        // point
        double distance(Point other) {
            double deltaX = this.x - other.x;
            double deltaY = this.y - other.y;
            return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        }
    }

    // Pair Class - Represents a Pair of Points and Provides a Method to Calculate
    // the Distance
    static class Pair {
        Point point1, point2;

        Pair(Point point1, Point point2) {
            this.point1 = point1;
            this.point2 = point2;
        }

        // Calculate and return the Euclidean distance between the two points in the
        // pair
        double distance() {
            double dx = point1.x - point2.x;
            double dy = point1.y - point2.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }

    // Find Closest Pair of Points among a List of Points
    static Pair findClosestPair(ArrayList<Point> points) {
        int n = points.size();
        if (n < 2) {
            return null;
        }

        // Create arrays for sorting points by x and y coordinates
        Point[] sortedX = points.toArray(new Point[n]);
        Point[] sortedY = points.toArray(new Point[n]);

        // Sort the arrays by x and y coordinates
        quickSort(sortedX); // Sort by x-coordinates
        quickSort(sortedY); // Sort by y-coordinates

        // Find the closest pair of points using a divide-and-conquer algorithm
        return closestPair(sortedX, sortedY, 0, n - 1);
    }

    // Closest Pair Algorithm Using Recursive Divide-and-Conquer Approach
    static Pair closestPair(Point[] sortedX, Point[] sortedY, int low, int high) {
        TreeSet<Point> ySortedSet = new TreeSet<>((p1, p2) -> {
            if (p1.y < p2.y)
                return -1;
            if (p1.y > p2.y)
                return 1;
            return Double.compare(p1.x, p2.x);
        });

        Pair closestPair = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (int i = low; i <= high; i++) {
            Point currentPoint = sortedX[i];

            // Remove points from the y-sorted set that are too far from the current point
            while (!ySortedSet.isEmpty() && currentPoint.x - ySortedSet.first().x > minDistance) {
                ySortedSet.pollFirst();
            }

            // Compare the current point with the remaining points in the y-sorted set
            for (Point candidate : ySortedSet) {
                double distance = currentPoint.distance(candidate);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPair = new Pair(currentPoint, candidate);
                }
            }

            // Add the current point to the y-sorted set
            ySortedSet.add(currentPoint);
        }

        return closestPair;
    }

    // Brute-Force Method for Finding the Closest Pair of Points
    static Pair bruteForce(Point[] points, int low, int high) {
        double minDistance = Double.POSITIVE_INFINITY;
        Point point1 = null;
        Point point2 = null;

        // Compare all pairs of points in the given range to find the closest pair
        for (int x = low; x <= high; x++) {
            for (int y = x + 1; y <= high; y++) {
                double distance = points[x].distance(points[y]);
                if (distance < minDistance) {
                    minDistance = distance;
                    point1 = points[x];
                    point2 = points[y];
                }
            }
        }

        return new Pair(point1, point2);
    }

    // Quick Sort Method for Sorting an Array of Points
    static void quickSort(Point[] points, int low, int high) {
        if (low < high) {
            // Divide the array into two parts and recursively sort them
            int partitionIndex = partition(points, low, high);
            quickSort(points, low, partitionIndex - 1);
            quickSort(points, partitionIndex + 1, high);
        }
    }

    static void quickSort(Point[] points) {
        // Overloaded method to sort an entire array of points
        quickSort(points, 0, points.length - 1);
    }

    static int partition(Point[] points, int low, int high) {
        // Choose a pivot point
        Point pivot = points[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            // Compare points based on y coordinates and then x coordinates
            if (compare(points[j], pivot) < 0) {
                i++;
                // Swap points to maintain the order
                swap(points, i, j);
            }
        }

        // Swap the pivot point with the appropriate position
        swap(points, i + 1, high);
        return i + 1;
    }

    static int compare(Point a, Point b) {
        // Compare points based on y coordinates and then x coordinates
        if (a.y < b.y || (a.y == b.y && a.x < b.x)) {
            return -1;
        } else if (a.y > b.y || (a.y == b.y && a.x > b.x)) {
            return 1;
        } else {
            return 0;
        }
    }

    static void swap(Point[] points, int i, int j) {
        Point temp = points[i];
        points[i] = points[j];
        points[j] = temp;
    }
}
