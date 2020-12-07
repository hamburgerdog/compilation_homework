package com.xjosiah.grammer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Grammer {
    private String[][] analyzeTable;

    private ArrayList<String> ll1Array;

    private Character beginChar;

    //  终结符集
    private HashSet<Character> vtSet;
    //  非终结符集
    private HashSet<Character> vnSet;
    //  表达式
    private HashMap<Character, ArrayList<String>> expMap;

    private HashMap<Character, HashMap<String, HashSet<Character>>> selMap;

    private HashMap<Character, HashSet<Character>> firstMap;

    private HashMap<Character, HashSet<Character>> followMap;

    public Grammer(ArrayList<String> ll1Array, Character beginChar) {
        this.ll1Array = ll1Array;
        this.beginChar = beginChar;

        vtSet = new HashSet<>();
        vnSet = new HashSet<>();
        expMap = new HashMap<>();

        selMap = new HashMap<>();
        firstMap = new HashMap<>();
        followMap = new HashMap<>();

        initSet();
        initExpMap();
        initFirstMap();
        initFollowMap();
        initSelectMap();
    }

    /**
     * 初始化非终结符和终结符
     */
    public void initSet() {
        for (String sItem : ll1Array) {
            String[] split = sItem.split("->");
            vnSet.add(split[0].charAt(0));
        }
        for (String sItem : ll1Array){
            String[] split = sItem.split("->");
            String vtStr = split[1];
            for (int i = 0; i < vtStr.length(); i++) {
                char cItem = vtStr.charAt(i);
                if (!vnSet.contains(cItem))
                    vtSet.add(cItem);
            }
        }
    }

    public void initExpMap() {
        for (String sItem : ll1Array) {
            String[] split = sItem.split("->");
            char c = split[0].charAt(0);
            if (!expMap.containsKey(c)) {
                ArrayList<String> expArray = new ArrayList<>();
                expArray.add(split[1]);
                expMap.put(c, expArray);
            } else {
                ArrayList<String> expArray = expMap.get(c);
                expArray.add(split[1]);
                expMap.put(c, expArray);
            }
        }
    }

    public void initFirstMap() {
        for (Character c : vnSet) {
            ArrayList<String> expArrayC = expMap.get(c);
            for (String sItem : expArrayC) {
                boolean doBreak = false;
                for (int i = 0; i < sItem.length(); i++) {
                    char cItem = sItem.charAt(i);
                    HashSet<Character> itemSet = firstMap.get(c);
                    if (itemSet == null) {
                        itemSet = new HashSet<Character>();
                    }
                    doBreak = doFirst(itemSet, c, cItem);
                    if (doBreak) {
                        break;
                    }
                }
            }
        }
    }

    private boolean doFirst(HashSet<Character> itemSet, Character c, char cItem) {
        if (cItem == 'ε' || vtSet.contains(cItem)) {
            itemSet.add(cItem);
            firstMap.put(c, itemSet);
            return true;
        } else if (vnSet.contains(cItem)) {
            ArrayList<String> expArrayCItem = expMap.get(cItem);
            for (int i = 0; i < expArrayCItem.size(); i++) {
                String s = expArrayCItem.get(i);
                char cTmp = s.charAt(0);
                doFirst(itemSet, c, cTmp);
            }
        }
        return true;
    }

    public void initFollowMap() {
        for (Character cTmp : vnSet) {
            HashSet<Character> tmpSet = new HashSet<>();
            followMap.put(cTmp, tmpSet);
        }
        for (Character cItem : vnSet) {
            Set<Character> expMapKeySet = expMap.keySet();
            for (Character expMapCharKey : expMapKeySet) {
                ArrayList<String> strItemArray = expMap.get(expMapCharKey);
                for (String strItem : strItemArray) {
                    System.out.println(expMapCharKey + "->" + strItem);
                    HashSet<Character> itemSet = followMap.get(cItem);
                    doFollow(cItem, cItem, expMapCharKey, strItem, itemSet);
                }
            }
        }
    }

    private void doFollow(Character nowChar, Character cItem,
                          Character keyChar, String strItem, HashSet<Character> itemSet) {
        if (cItem.equals(beginChar)) {
            itemSet.add('#');
            followMap.put(nowChar, itemSet);
        }
        if (isContainAb(vtSet, strItem, cItem)) {
            Character lastChar = getLastChar(strItem, cItem);
            itemSet.add(lastChar);
            followMap.put(nowChar, itemSet);
        }
        if (isContainAB(vnSet, strItem, cItem)) {
            Character lastCharAB = getLastChar(strItem, cItem);
            HashSet<Character> firstMapSet = firstMap.get(lastCharAB);
            itemSet.addAll(firstMapSet);
            if (firstMapSet.contains('ε'))
                itemSet.add('#');
            itemSet.remove('ε');
            followMap.put(nowChar, itemSet);

            if (whetherAbIsNull(vnSet, strItem, cItem, expMap)) {
                Character lastCharAb = getLastChar(strItem, cItem);
                if (!keyChar.equals(cItem)) {
                    Set<Character> keySet = expMap.keySet();
                    for (Character key : keySet) {
                        ArrayList<String> strArray = expMap.get(key);
                        for (String s : strArray) {
                            doFollow(nowChar, keyChar, key, s, itemSet);
                        }
                    }
                }
            }
        }
        if (isContainbA(strItem, cItem)) {
            if (!keyChar.equals(cItem)) {
                Set<Character> keySet = expMap.keySet();
                for (Character key : keySet) {
                    ArrayList<String> strArray = expMap.get(key);
                    for (String s : strArray) {
                        doFollow(nowChar, keyChar, key, s, itemSet);
                    }
                }
            }
        }
    }

    public void initSelectMap() {
        Set<Character> keySet = expMap.keySet();
        for (Character key : keySet) {
            HashMap<String, HashSet<Character>> selMapItem = new HashMap<>();
            ArrayList<String> expArray = expMap.get(key);
            for (String s : expArray) {
                char beginS = s.charAt(0);
                HashSet<Character> selectSet = new HashSet<>();
                if ('ε' == beginS) {
                    selectSet = followMap.get(key);
                    selectSet.remove('ε');
                    selMapItem.put(s, selectSet);
                }
                if (vtSet.contains(beginS)) {
                    selectSet.add(beginS);
                    selectSet.remove('ε');
                    selMapItem.put(s, selectSet);
                }
                if (vnSet.contains(beginS)) {
                    selectSet = firstMap.get(key);
                    selectSet.remove('ε');
                    selMapItem.put(s, selectSet);
                }
                selMap.put(key, selMapItem);
            }
        }
    }

    public void creatAlzTable() {
        Object[] vtArray = vtSet.toArray();
        Object[] vnArray = vnSet.toArray();

        this.analyzeTable = new String[vnArray.length + 1][vtArray.length + 1];

        System.out.println("-----------------------------------------------------------------------------");
        System.out.print("表" + "\t\t");

        analyzeTable[0][0] = "Vn/Vt";
        for (int i = 0; i < vtArray.length; i++) {
            if (vtArray[i].equals('ε'))
                vtArray[i] = '#';
            System.out.print(vtArray[i] + "\t\t\t");
            analyzeTable[0][i + 1] = vtArray[i] + "";
        }
        System.out.println();
        for (int i = 0; i < vnArray.length; i++) {

            System.out.print(vnArray[i] + "\t\t");
            analyzeTable[i + 1][0] = vnArray[i] + "";
            for (int j = 0; j < vtArray.length; j++) {
                String exp = searchExp(selMap,
                        Character.valueOf((Character) vnArray[i]),
                        Character.valueOf((Character) vtArray[j]));
                if (exp == null) {
                    System.out.print("-\t\t\t");
                    analyzeTable[i + 1][j + 1] = "";
                } else {
                    System.out.print(vnArray[i] + "->" + exp + "\t\t");
                    analyzeTable[i + 1][j + 1] = vnArray[i] + "->" + exp;
                }
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------------------------");
    }


    private boolean isContainAb(HashSet<Character> vtSet, String strItem, Character c) {
        String s = c.toString();
        if (strItem.contains(s)) {
            int i = strItem.indexOf(s);
            String findStr;
            try {
                findStr = String.valueOf(strItem.charAt(i + 1));
            } catch (Exception e) {
                return false;
            }
            return vtSet.contains(findStr.charAt(0));
        } else
            return false;
    }

    private boolean isContainAB(HashSet<Character> vnSet, String strItem, Character c) {
        String s = c.toString();
        if (strItem.contains(s)) {
            int i = strItem.indexOf(s);
            String findStr;
            try {
                findStr = String.valueOf(strItem.charAt(i + 1));
            } catch (Exception e) {
                return false;
            }
            return vnSet.contains(findStr.charAt(0));
        } else
            return false;
    }

    private boolean isContainbA(String strItem, Character c) {
        String s = c.toString();
        String lastStr = strItem.substring(strItem.length() - 1);
        return lastStr.equals(s);
    }

    private boolean whetherAbIsNull(HashSet<Character> vnSet, String strItem, Character c,
                                    HashMap<Character, ArrayList<String>> expMap) {
        String s = c.toString();
        if (isContainAB(vnSet, strItem, c)) {
            Character lastChar = getLastChar(strItem, c);
            ArrayList<String> expArray = expMap.get(lastChar);
            if (expArray.contains("ε")) {
                System.out.println(lastChar + " : ('ε')" + s);
                return true;
            }
        }
        return false;
    }

    private Character getLastChar(String strItem, Character c) {
        String s = c.toString();
        if (strItem.contains(s)) {
            int i = strItem.indexOf(s);
            Character cTmp;
            try {
                cTmp = strItem.charAt(i + 1);
            } catch (Exception e) {
                return null;
            }
            return cTmp;
        }
        return null;
    }

    private String searchExp(HashMap<Character, HashMap<String, HashSet<Character>>> selectMap,
                             Character keyChar, char c) {
        try {
            HashMap<String, HashSet<Character>> expStrMap = selectMap.get(keyChar);
            Set<String> keySet = expStrMap.keySet();
            for (String expSetKey : keySet) {
                HashSet<Character> expSet = expStrMap.get(expSetKey);
                if (expSet.contains(c))
                    return expSetKey;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


}
