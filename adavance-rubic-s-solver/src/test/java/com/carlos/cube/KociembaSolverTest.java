package com.carlos.cube;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class KociembaSolverTest {

    private static final String SOLVED_STATE = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";

    @Test
    public void testSolvedState() {
        RubiksCube cube = new RubiksCube();
        KociembaSolver solver = new KociembaSolver(cube);
        List<String> solution = solver.solve();
        
        // A solved cube should yield an empty solution list
        assertTrue(solution.isEmpty() || solution.size() == 0, 
            "Solved state should require 0 moves");
    }

    @Test
    public void testClassicScrambleSolvability() {
        // Preset 1: Classic Scramble "F R' B L D' U2 R' F2 B D' F L"
        RubiksCube cube = new RubiksCube();
        String scramble = "F R' B L D' U2 R' F2 B D' F L";
        for (String move : scramble.split("\\s+")) {
            cube.rotate(move);
        }
        
        // Solve the scrambled cube
        KociembaSolver solver = new KociembaSolver(cube);
        List<String> solution = solver.solve();
        
        assertNotNull(solution, "Solution should not be null");
        assertFalse(solution.isEmpty(), "Solution should contain moves");
        
        // Apply the solution to the cube
        for (String move : solution) {
            cube.rotate(move);
        }
        
        // Verify it returns to the fully solved state
        assertEquals(SOLVED_STATE, cube.getFaceletString(), 
            "Applying the computed solution must solve the cube");
    }

    @Test
    public void testCheckerboardSolvability() {
        // Preset 2: Checkerboard "U2 D2 R2 L2 F2 B2"
        RubiksCube cube = new RubiksCube();
        String scramble = "U2 D2 R2 L2 F2 B2";
        for (String move : scramble.split("\\s+")) {
            cube.rotate(move);
        }
        
        // Solve the scrambled cube
        KociembaSolver solver = new KociembaSolver(cube);
        List<String> solution = solver.solve();
        
        assertNotNull(solution, "Solution should not be null");
        assertFalse(solution.isEmpty(), "Solution should contain moves");
        
        // Apply the solution to the cube
        for (String move : solution) {
            cube.rotate(move);
        }
        
        // Verify it returns to the fully solved state
        assertEquals(SOLVED_STATE, cube.getFaceletString(), 
            "Applying the computed solution must solve the checkerboard cube");
    }

    @Test
    public void testSuperflipSolvability() {
        // Preset 3: Superflip "U R2 F B R B2 R U2 F2 R2 U' D' F2 D L2 B2 U L D2"
        RubiksCube cube = new RubiksCube();
        String scramble = "U R2 F B R B2 R U2 F2 R2 U' D' F2 D L2 B2 U L D2";
        for (String move : scramble.split("\\s+")) {
            cube.rotate(move);
        }
        
        // Solve the scrambled cube
        KociembaSolver solver = new KociembaSolver(cube);
        List<String> solution = solver.solve();
        
        assertNotNull(solution, "Solution should not be null");
        assertFalse(solution.isEmpty(), "Solution should contain moves");
        
        // Apply the solution to the cube
        for (String move : solution) {
            cube.rotate(move);
        }
        
        // Verify it returns to the fully solved state
        assertEquals(SOLVED_STATE, cube.getFaceletString(), 
            "Applying the computed solution must solve the superflip cube");
    }
}
