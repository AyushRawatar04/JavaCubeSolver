package com.carlos.cube;

import java.util.*;

public class KociembaSolver {
    private RubiksCube cube;

    public KociembaSolver(RubiksCube cube) {
        this.cube = cube;
    }

    public List<String> solve() {
        String facelets = cube.getFaceletString();
        Search search = new Search();
        String result = search.solution(facelets, 21, 100000000, 0, 0);
        if (result.startsWith("Error")) {
            return Collections.emptyList();
        }
        
        String[] moves = result.trim().split("\\s+");
        List<String> solution = new ArrayList<>();
        for (String move : moves) {
            if (!move.isEmpty()) {
                solution.add(move);
            }
        }
        return solution;
    }
}
