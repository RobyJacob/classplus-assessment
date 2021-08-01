import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

class Portal {
    private Universe from;
    private Universe to;
    private int travelTime;
    private int id;

    Portal() {
        this.from = new Universe();
        this.to = new Universe();
        this.travelTime = -1;
        this.id = 1;
    }

    Portal(Universe from, Universe to, int travelTime, int id) {
        this.from = from;
        this.to = to;
        this.travelTime = travelTime;
        this.id = id;
    }

    Universe getFrom() {
        return from;
    }

    Universe getTo() {
        return to;
    }

    int getTime() {
        return travelTime;
    }

    int getId() {
        return id;
    }
}

class Universe {
    private int name;
    private List<Portal> portals;

    Universe() {
        this.name = 0;
        this.portals = new ArrayList<>();
    }

    Universe(int name) {
        this.name = name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getName() {
        return this.name;
    }

    public void addPortal(Portal portal) {
        if (this.portals == null)
            this.portals = new ArrayList<>();
        this.portals.add(portal);
    }

    public List<Portal> getPortals() {
        return this.portals;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}

public class Multiverse {
    private Map<Integer, Universe> universes;
    private Map<Integer, Portal> portals;

    Multiverse() {
        this.universes = new HashMap<>();
        this.portals = new HashMap<>();
    }

    public void addPortal(int from, int to, int time, int portalId) {
        Universe fromUniverse, toUniverse;
        Portal portal;

        if (universes.containsKey(from)) {
            fromUniverse = universes.get(from);
        } else {
            fromUniverse = new Universe(from);
            universes.put(from, fromUniverse);
        }

        if (universes.containsKey(to)) {
            toUniverse = universes.get(to);
        } else {
            toUniverse = new Universe(to);
            universes.put(to, toUniverse);
        }

        if (portals.containsKey(portalId)) {
            portal = portals.get(portalId);
        } else {
            portal = new Portal(fromUniverse, toUniverse, time, portalId);
            portals.put(portalId, portal);
        }

        // assuming duplicate portals are not created
        fromUniverse.addPortal(portal);
    }

    private int findSafeTime(int currentTime, Set<Integer> unsafeTimes) {
        int safeTime = 0;

        if (unsafeTimes.isEmpty())
            return safeTime;

        // if (!unsafeTimes.contains(currentTime))

        for (int i = 0; i < unsafeTimes.size(); i++) {
            if (!unsafeTimes.contains(currentTime))
                return safeTime;

            currentTime += 1;

            if (unsafeTimes.contains(currentTime))
                safeTime += currentTime;
        }

        return safeTime;
    }

    public int findPathLength(int src, int dest, Map<Integer, Set<Integer>> demonTimes) {
        Universe srcUniverse, destUniverse;
        int totalTimeTaken = 0, minTime = Integer.MAX_VALUE;
        Stack<Portal> stack = new Stack<>();

        if (!universes.containsKey(src) || !universes.containsKey(dest))
            return totalTimeTaken;

        srcUniverse = universes.get(src);
        destUniverse = universes.get(dest);

        totalTimeTaken += findSafeTime(0, demonTimes.get(srcUniverse.getName()));
        for (Portal portal : srcUniverse.getPortals()) {
            stack.push(portal);
        }

        while (!stack.isEmpty()) {
            Portal portal = stack.pop();

            totalTimeTaken += portal.getTime();
            totalTimeTaken += findSafeTime(totalTimeTaken, demonTimes.get(portal.getTo().getName()));

            if (portal.getTo() == destUniverse) {
                minTime = Math.min(minTime, totalTimeTaken);
                totalTimeTaken = 0;
                continue;
            }

            for (Portal p : portal.getTo().getPortals()) {
                stack.push(p);
            }
        }

        return minTime;
    }

    @Override
    public String toString() {
        String parsedStr = "";

        for (Map.Entry<Integer, Portal> entry : portals.entrySet()) {
            parsedStr += entry.getKey().toString();
            parsedStr += "\t ---->\t";
            parsedStr += String.format("{%s, %s, %s}\n", entry.getValue().getFrom().toString(),
                    entry.getValue().getTo().toString(), entry.getValue().getTime());
        }

        return parsedStr;
    }

    public static void main(String[] args) {
        /*
         * 1 --3--> 2 | | | | 2 2 | | | | V V 3 --3--> 4
         */
        Multiverse multiverse = new Multiverse();
        Map<Integer, Set<Integer>> demonTimes = new HashMap<>();

        // multiverse.addPortal(1, 2, 3, 1);
        // multiverse.addPortal(1, 3, 2, 2);
        // multiverse.addPortal(2, 4, 2, 3);
        // multiverse.addPortal(3, 4, 3, 4);

        // demonTimes.put(1, new HashSet<>());
        // demonTimes.put(2, new HashSet<>());
        // demonTimes.get(2).add(4);
        // demonTimes.put(3, new HashSet<>());
        // demonTimes.get(3).add(2);
        // demonTimes.get(3).add(3);
        // demonTimes.put(4, new HashSet<>());

        // // System.out.println(multiverse);

        // int travelCost = multiverse.findPathLength(1, 4, demonTimes);

        Scanner scanner = new Scanner(System.in);

        String[] s = scanner.nextLine().split(" ");
        int n = Integer.parseInt(s[0]);

        for (int i = 0; i < Integer.valueOf(s[1]); i++) {
            String[] in = scanner.nextLine().split(" ");
            multiverse.addPortal(Integer.valueOf(in[0]), Integer.valueOf(in[1]), Integer.valueOf(in[2]), i + 1);
        }

        for (int i = 0; i < n; i++) {
            String[] in = scanner.nextLine().split(" ");
            Set<Integer> times = new HashSet<>();
            if (in.length == 1)
                demonTimes.put(i + 1, times);
            for (int j = 1; j < in.length; j++) {
                // System.out.println("debug=" + in[j] == "");
                times.add(Integer.parseInt(in[j]));
                demonTimes.put(i + 1, times);
            }
        }

        int result = multiverse.findPathLength(1, n, demonTimes);

        System.out.println(result);
    }
}
