package com.carlos.cube;

public class RubiksCube {
    private char[] f = new char[54];
    private static final String START = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";

    public RubiksCube() {
        setFacelet(START);
    }

    public char[][] getFaces() {
        char[][] faces = new char[6][9];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                char c = f[i * 9 + j];
                switch(c) {
                    case 'U': faces[i][j] = 'w'; break;
                    case 'R': faces[i][j] = 'r'; break;
                    case 'F': faces[i][j] = 'g'; break;
                    case 'D': faces[i][j] = 'y'; break;
                    case 'L': faces[i][j] = 'o'; break;
                    case 'B': faces[i][j] = 'b'; break;
                    default: faces[i][j] = 'k';
                }
            }
        }
        return faces;
    }

    public String getFaceletString() { return new String(f); }

    public void setFacelet(String state) {
        String s = state.toUpperCase();
        for (int i = 0; i < 54; i++) {
            char c = (i < s.length()) ? s.charAt(i) : 'U';
            switch(c) {
                case 'W': f[i]='U'; break; case 'R': f[i]='R'; break;
                case 'G': f[i]='F'; break; case 'Y': f[i]='D'; break;
                case 'O': f[i]='L'; break; case 'B': f[i]='B'; break;
                default: f[i]=c;
            }
        }
    }

    public void rotate(String move) {
        int t = 1;
        if (move.endsWith("'")) t = 3; else if (move.endsWith("2")) t = 2;
        for (int i = 0; i < t; i++) apply(move.charAt(0));
    }

    private synchronized void apply(char s) {
        char[] o = f.clone();
        char[] n = f.clone();
        int off = getOff(s);
        int[] r = {6, 3, 0, 7, 4, 1, 8, 5, 2};
        for (int i = 0; i < 9; i++) n[off + i] = o[off + r[i]];

        switch(s) {
            case 'U': mv(n,o, 18,19,20,  9,10,11, 45,46,47, 36,37,38); break;
            case 'D': mv(n,o, 24,25,26, 42,43,44, 51,52,53, 15,16,17); break;
            case 'L': mv(n,o, 18,21,24,  0, 3, 6, 53,50,47, 27,30,33); break;
            case 'R': mv(n,o, 20,23,26, 29,32,35, 51,48,45,  2,  5,  8); break;
            case 'F': mv(n,o,  6,  7,  8, 44,41,38, 29,28,27,  9, 12, 15); break;
            case 'B': mv(n,o,  2,  1,  0, 17,14,11, 33,34,35, 36, 39, 42); break;
        }
        f = n;
    }
    // ... I will skip writing the whole class again and instead fix it correctly in one go
    private void mv(char[] n, char[] o, int... i) {
        n[i[0]]=o[i[3]]; n[i[1]]=o[i[4]]; n[i[2]]=o[i[5]];
        n[i[3]]=o[i[6]]; n[i[4]]=o[i[7]]; n[i[5]]=o[i[8]];
        n[i[6]]=o[i[9]]; n[i[7]]=o[i[10]]; n[i[8]]=o[i[11]];
        n[i[9]]=o[i[0]]; n[i[10]]=o[i[1]]; n[i[11]]=o[i[2]];
    }

    private int getOff(char s) {
        switch(s) {
            case 'U': return 0; case 'R': return 9; case 'F': return 18;
            case 'D': return 27; case 'L': return 36; case 'B': return 45;
            default: return 0;
        }
    }
}
